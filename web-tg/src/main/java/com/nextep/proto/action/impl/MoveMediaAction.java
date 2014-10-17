package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class MoveMediaAction extends AbstractAction implements
		CurrentUserAware, MediaAware {

	private static final long serialVersionUID = -806295405220270534L;

	private static final String APIS_ALIAS_MEDIA = "mediaParent";
	private static final Log LOGGER = LogFactory.getLog(MoveMediaAction.class);

	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService mediaService;
	private MediaProvider mediaProvider;
	private RightsManagementService rightsManagementService;

	private String id;
	private String parent;
	private int newIndex = -1;
	private int direction;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey mediaKey = CalmFactory.parseKey(id);
		final ItemKey parentKey = CalmFactory.parseKey(parent);
		final ApisRequest request = ApisFactory.createCompositeRequest();
		request.addCriterion(currentUserSupport.createApisCriterionFor(
				getNxtpUserToken(), true));
		request.addCriterion((ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(parentKey))
				.aliasedBy(APIS_ALIAS_MEDIA).with(Media.class)
				.with(AdvertisingBooster.class));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Extracting & validating current user
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Extracting all media
		final CalmObject parent = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_MEDIA);
		final List<Media> allMedia = (List) parent.get(Media.class); // Sometimes
																		// hating
																		// generics

		// Iterating over all media
		int currentIndex = -1;
		Media media = null;
		for (int i = 0; i < allMedia.size(); i++) {
			final Media m = allMedia.get(i);
			// If we are on our media
			if (m.getKey().equals(mediaKey)) {
				media = m;
				currentIndex = i;
				break;
			}
		}
		// If no new index specified, we use current index and direction
		if (newIndex == -1) {
			newIndex = currentIndex + (int) Math.signum(direction);
		}
		// Check if everything is OK
		if (currentIndex == -1 || newIndex == -1 || newIndex >= allMedia.size()) {
			// We log the incorrect input settings before exiting
			LOGGER.error("Incorrect media switch for move operation : "
					+ mediaKey.toString() + " -> " + newIndex + " [direction="
					+ direction + "]");
			return error();
		}

		// Checking if we have the right to move media
		if (rightsManagementService.canModify(currentUser, media)) {
			// We are about to write
			ContextHolder.toggleWrite();

			// Re-inserting media at new place
			allMedia.remove(media);
			allMedia.add(newIndex, media);

			// Re-assigning preferences
			for (int i = 0; i < allMedia.size(); i++) {
				final MutableMedia m = (MutableMedia) allMedia.get(i);
				m.setPreferenceOrder(i * 100);
				mediaService.saveItem(m);
			}
		} else {
			setErrorMessage(getMessageSource().getMessage(
					"media.move.notAllowed", null, getLocale()));
			return FORBIDDEN;
		}

		// Initializing media support
		mediaProvider.initialize(parentKey, allMedia);
		return SUCCESS;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParent() {
		return parent;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	public void setNewIndex(int newIndex) {
		this.newIndex = newIndex;
	}

	public int getNewIndex() {
		return newIndex;
	}

	@Override
	public RightsManagementService getRightsManagementService() {
		return rightsManagementService;
	}
}
