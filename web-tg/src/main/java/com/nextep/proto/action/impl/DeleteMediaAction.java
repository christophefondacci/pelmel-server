package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;

import net.sf.json.JSONObject;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.apis.adapters.ApisMediaParentKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.RightsManagementService;
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

public class DeleteMediaAction extends AbstractAction implements
		CurrentUserAware, JsonProviderWithError {

	private static final long serialVersionUID = -2984157457110022157L;
	private static final String APIS_ALIAS_MEDIA = "media";
	private static final String APIS_ALIAS_MEDIA_PARENT = "parent";
	private static final String REDIRECT_ACTION_PLACE_OVERVIEW = "placeOverview";
	private static final String REDIRECT_ACTION_MY_PROFILE = "myProfile";

	private static final ApisItemKeyAdapter MEDIA_PARENT_ADAPTER = new ApisMediaParentKeyAdapter();

	private CalPersistenceService mediaService;
	private CurrentUserSupport currentUserSupport;
	private RightsManagementService rightsManagementService;
	private CalPersistenceService activitiesService;
	private SearchPersistenceService searchService;

	private String id;
	private boolean confirmed;
	private boolean canProceed;
	private Media media;

	private String redirectAction;
	private String redirectId;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey mediaKey = CalmFactory.parseKey(id);
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(mediaKey))
								.aliasedBy(APIS_ALIAS_MEDIA)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(MEDIA_PARENT_ADAPTER)
												.aliasedBy(
														APIS_ALIAS_MEDIA_PARENT)
												.with(GeographicItem.class)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		// Extracting current user
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		// Checking if user is still connected
		checkCurrentUser(currentUser);

		// Now we only allow user to remove media for their own profile
		// So we check if media's parent is current user
		ContextHolder.toggleWrite();
		media = response.getUniqueElement(Media.class, APIS_ALIAS_MEDIA);
		if (!rightsManagementService.canDelete(currentUser, media)) {
			if (media == null) {
				setErrorMessage(getMessageSource().getMessage(
						"media.deletion.notFound", null, getLocale()));
			} else {
				setErrorMessage(getMessageSource().getMessage(
						"media.deletion.notAllowed", null, getLocale()));
			}
			return FORBIDDEN;
		}
		getHeaderSupport().initialize(getLocale(), currentUser, null,
				SearchType.BARS);
		if (confirmed) {
			((MutableMedia) media).setOnline(false);
			mediaService.saveItem(media);

			// Now we log this activity
			final MutableActivity activity = (MutableActivity) activitiesService
					.createTransientObject();
			activity.setActivityType(ActivityType.DELETION);
			activity.setLoggedItemKey(media.getRelatedItemKey());
			activity.setDate(new Date());
			activity.setUserKey(currentUser.getKey());
			activity.setExtraInformation(media.getKey().toString());

			final CalmObject mediaParent = media.getUnique(CalmObject.class,
					APIS_ALIAS_MEDIA_PARENT);
			final GeographicItem localization = GeoHelper
					.extractLocalization(mediaParent);
			activity.add(localization);
			activitiesService.saveItem(activity);
			searchService.storeCalmObject(activity, SearchScope.CHILDREN);

			if (Place.CAL_TYPE.equals(media.getRelatedItemKey().getType())) {
				redirectAction = REDIRECT_ACTION_PLACE_OVERVIEW;
				redirectId = media.getRelatedItemKey().toString();
			} else {
				redirectAction = REDIRECT_ACTION_MY_PROFILE;
			}

			return SUCCESS;
		} else {
			return CONFIRM;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public boolean getConfirmed() {
		return confirmed;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	@Override
	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	public Media getMedia() {
		return media;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	@Override
	public String getJson() {
		JsonStatus json = new JsonStatus();
		return JSONObject.fromObject(json).toString();
	}

	@Override
	public String getJsonError() {
		JsonStatus jsonError = new JsonStatus();
		jsonError.setError(true);
		jsonError.setMessage(getErrorMessage());
		return JSONObject.fromObject(jsonError).toString();
	}

	public String getRedirectAction() {
		return redirectAction;
	}

	public String getRedirectId() {
		return redirectId;
	}
}
