package com.nextep.proto.action.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

public class AddMediaAction extends AbstractAction implements MediaAware,
		JsonProvider {

	private static final long serialVersionUID = -2162490187996856628L;
	private static final Log LOGGER = LogFactory.getLog(AddMediaAction.class);
	private static final String APIS_ALIAS_PARENT_ITEM = "parent";
	private static final ApisItemKeyAdapter EVENT_LOCATION_ADAPTER = new ApisEventLocationAdapter();

	private MediaPersistenceSupport mediaPersistenceSupport;
	private CalPersistenceService activitiesService;
	private ApisCriterionFactory userCriterionFactory;
	private SearchPersistenceService searchService;
	private MediaProvider mediaProvider;
	private JsonBuilder jsonBuilder;
	private NotificationService notificationService;

	private String mediaDesc;
	private String parentKey;
	private File media;
	private String contentType, fileName;
	private boolean video;
	private boolean highRes;
	private String videoStr;
	private CalmObject parentItem;
	private Media addedMedia;

	// @Override
	// public Media createMedia(ItemKey parentItemKey, File tmpFile,
	// String filename, String contentType, String title, boolean isVideo)
	// throws IOException {
	// return mediaPersistenceSupport.createMedia(parentItemKey, tmpFile,
	// filename, contentType, title, isVideo);
	// }
	//
	// @Override
	// public void generateThumb(MutableMedia mutableMedia, File localFile) {
	// mediaPersistenceSupport.generateThumb(mutableMedia, localFile);
	// }

	@Override
	protected String doExecute() throws Exception {
		final ItemKey parentItemKey = CalmFactory.parseKey(parentKey);
		final ItemKey userTokenKey = CalmFactory.createKey(User.TOKEN_TYPE,
				getNxtpUserToken());
		// Querying user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						userCriterionFactory.createApisCriterion(userTokenKey))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(parentItemKey))
								.aliasedBy(APIS_ALIAS_PARENT_ITEM)
								.with(GeographicItem.class)
								.with(Media.class)
								.addCriterion(
										SearchRestriction
												.adaptKey(EVENT_LOCATION_ADAPTER)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final User user = response.getUniqueElement(User.class,
				Constants.APIS_ALIAS_CURRENT_USER);

		// Getting media's parent object
		parentItem = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PARENT_ITEM);
		getHeaderSupport().initialize(getLocale(), parentItem, null, null);

		// Only current user can add a photo to himself
		if (User.CAL_TYPE.equals(parentItemKey.getType())
				&& !parentItemKey.equals(user.getKey())) {
			return FORBIDDEN;
		}

		// Checking user validity
		checkCurrentUser(user);

		// If Ok, we create our media
		ContextHolder.toggleWrite();
		final List<? extends Media> parentMedia = parentItem.get(Media.class);
		final Integer firstMediaPriority = getLowestPriority(parentMedia);
		addedMedia = mediaPersistenceSupport.createMedia(user, parentItemKey,
				media, fileName, contentType, mediaDesc, video,
				firstMediaPriority);

		try {
			notificationService.sendMediaAddedEmailNotification(parentItem,
					user, addedMedia);
		} catch (Exception e) {
			LOGGER.error("Cannot send email notification: " + e.getMessage(), e);
		}

		// Now we log this activity
		final GeographicItem localization = GeoHelper
				.extractLocalization(parentItem);
		// Cannot add a non-localized activity
		if (localization != null) {
			final MutableActivity activity = (MutableActivity) activitiesService
					.createTransientObject();
			activity.setActivityType(ActivityType.CREATION);
			activity.setLoggedItemKey(parentItemKey);
			activity.setDate(new Date());
			activity.setUserKey(user.getKey());
			activity.setExtraInformation(addedMedia.getKey().toString());
			activity.add(localization);
			activitiesService.saveItem(activity);
			searchService.storeCalmObject(activity, SearchScope.CHILDREN);
		} else {
			LOG.warn("Adding a media to ["
					+ parentItemKey
					+ "] without related activity cause item localization not available");
		}

		mediaProvider.initialize(parentItemKey, Arrays.asList(addedMedia));
		return SUCCESS;
	}

	private int getLowestPriority(List<? extends Media> parentMedia) {
		Integer minPriority = null;
		for (Media m : parentMedia) {
			if (minPriority == null) {
				minPriority = m.getPreferenceOrder();
			} else {
				if (m.getPreferenceOrder() < minPriority.intValue()) {
					minPriority = m.getPreferenceOrder();
				}
			}
		}
		return minPriority == null ? 0 : minPriority.intValue();
	}

	@Override
	protected int getLoginRequiredErrorCode() {
		return 200;
	}

	public void setVideo(String video) {
		this.video = Boolean.parseBoolean(video);
		this.videoStr = video;
	}

	public String getVideo() {
		return videoStr;
	}

	public void setMediaContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setMediaFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setMedia(File media) {
		this.media = media;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setMediaDesc(String mediaDesc) {
		this.mediaDesc = mediaDesc;
	}

	public String getMediaDesc() {
		return mediaDesc;
	}

	public void setMediaPersistenceSupport(
			MediaPersistenceSupport mediaPersistenceSupport) {
		this.mediaPersistenceSupport = mediaPersistenceSupport;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setUserCriterionFactory(
			ApisCriterionFactory userCriterionFactory) {
		this.userCriterionFactory = userCriterionFactory;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public String getTargetUrl() {
		return getUrlService().getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), parentItem);
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public String getJson() {
		final JsonMedia json = jsonBuilder.buildJsonMedia(addedMedia, highRes);
		return JSONObject.fromObject(json).toString();
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
