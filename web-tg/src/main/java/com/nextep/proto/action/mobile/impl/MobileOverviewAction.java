package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonLiker;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonOverviewElement;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.DescriptionAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.OverviewAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisUserLocationItemKeyAdapter;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.ItemsListBoxSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileOverviewAction extends AbstractAction implements
		OverviewAware, MediaAware, TagAware, DescriptionAware, JsonProvider {

	private static final Log LOGGER = LogFactory
			.getLog(MobileOverviewAction.class);

	// Constants declaration
	private static final long serialVersionUID = 154177235838836337L;
	private static final String APIS_ALIAS_PLACE = "p";
	private static final String APIS_ALIAS_USER_CITY = "ucity";
	private static final String APIS_ALIAS_USER_LIKERS = "ulikers";
	private static final String APIS_ALIAS_USER_LIKED = "liked";
	private static final String APIS_ALIAS_PLACE_LIKE = "plike";
	private static final String APIS_ALIAS_USER_NEAR = "unear";
	private static final String APIS_ALIAS_NEARBY_PLACES = "nearbyPlaces";
	private static final String APIS_ALIAS_COMMENTS = "comments";
	private static final String APIS_ALIAS_ACTIVITIES_CHECKIN = "checkins";

	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private static final ApisItemKeyAdapter userLocationAdapter = new ApisUserLocationItemKeyAdapter();

	private static final String KEY_LIKE_COUNT = "label.like.count";
	private static final String KEY_USER_COUNT = "label.user.count";

	private static final int MAX_USERS_FROM_ACTIVITY = 30;
	private static final int MAX_LOCALIZATION_ACTIVITY = 50;

	// Injected supports
	private ViewManagementService viewManagementService;
	private LocalizationService localizationService;
	private CurrentUserSupport currentUserSupport;
	private OverviewSupport overviewSupport;
	private MediaProvider mediaProvider;
	private TagSupport tagSupport;
	private ItemsListBoxSupport descriptionSupport;
	private String baseUrl;
	private JsonBuilder jsonBuilder;
	private int maxRelatedElements;
	private double radius;

	// Dynamic arguments
	private String id;
	private boolean highRes;
	private Double lat, lng;

	// Private work variables
	private CalmObject overviewObject;
	private ApiCompositeResponse response;
	private User currentUser;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(id);
		final ApisCriterion objCriterion = (ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(itemKey)).aliasedBy(APIS_ALIAS_PLACE)
				.with(Description.class).with(Media.class).with(Tag.class);
		// For a place overview we would like to know people who currently are
		// in that place
		if (Place.CAL_TYPE.equals(itemKey.getType())) {
			final Collection<FacetCategory> userFacetCategories = SearchHelper
					.buildUserFacetCategories();
			objCriterion.addCriterion((ApisCriterion) SearchRestriction
					.withContained(User.class, SearchScope.NEARBY_BLOCK,
							maxRelatedElements, 0)
					.facettedBy(userFacetCategories)
					.aliasedBy(APIS_ALIAS_USER_NEAR)
					.with(Media.class, MediaRequestTypes.THUMB));
			objCriterion.addCriterion((ApisCriterion) SearchRestriction
					.withContained(User.class, SearchScope.CHILDREN,
							maxRelatedElements, 0)
					.facettedBy(userFacetCategories)
					.aliasedBy(APIS_ALIAS_USER_LIKERS)
					.with(Media.class, MediaRequestTypes.THUMB));
			objCriterion
					.addCriterion((WithCriterion) SearchRestriction
							.with(Event.class)
							// .with(SearchRestriction.with(EventSeries.class)
							// .aliasedBy(
							// Constants.APIS_ALIAS_EVENT_SERIES))
							.with(Media.class)
							.with(Description.class)
							.addCriterion(
									(ApisCriterion) SearchRestriction
											.adaptKey(eventLocationAdapter)
											.aliasedBy(
													Constants.APIS_ALIAS_EVENT_PLACE)
											.with(Media.class,
													MediaRequestTypes.THUMB)));
			objCriterion
					.addCriterion(SearchRestriction.with(EventSeries.class));
			// Getting comments count
			objCriterion.addCriterion(SearchRestriction.with(Comment.class, 1,
					0).aliasedBy(APIS_ALIAS_COMMENTS));
			// Getting activities
			objCriterion.with(ApisActivitiesHelper.withActivities(
					MAX_LOCALIZATION_ACTIVITY, 0,
					new RequestTypeLatestActivities(MAX_LOCALIZATION_ACTIVITY,
							ActivityType.CHECKIN)));
		} else if (User.CAL_TYPE.equals(itemKey.getType())) {
			objCriterion
					.with(SearchRestriction.with(GeographicItem.class))
					.addCriterion(
							(ApisCriterion) SearchRestriction
									.adaptKey(userLocationAdapter)
									.aliasedBy(
											Constants.APIS_ALIAS_USER_LOCATION)
									.with(Media.class, MediaRequestTypes.THUMB))
					.addCriterion(
							(ApisCriterion) SearchRestriction
									.with(User.class, maxRelatedElements, 0)
									.aliasedBy(APIS_ALIAS_USER_LIKED)
									.with(Media.class, MediaRequestTypes.THUMB))
					.with(SearchRestriction
							.withContained(User.class, SearchScope.CHILDREN,
									Constants.MAX_FAVORITE_MEN, 0).aliasedBy(
									APIS_ALIAS_USER_LIKERS))
					// .addCriterion(
					// (WithCriterion) SearchRestriction
					// .withContained(User.class,
					// SearchScope.CHILDREN,
					// Constants.MAX_FAVORITE_MEN, 0)
					// .aliasedBy(APIS_ALIAS_USER_LIKE)
					// .with(Media.class))
					.addCriterion(
							(ApisCriterion) SearchRestriction
									.with(Place.class, maxRelatedElements, 0)
									.aliasedBy(APIS_ALIAS_PLACE_LIKE)
									.with(Media.class, MediaRequestTypes.THUMB))
					.with(ApisActivitiesHelper.withUserActivities(
							MAX_LOCALIZATION_ACTIVITY, 0, ActivityType.CHECKIN)
							.aliasedBy(APIS_ALIAS_ACTIVITIES_CHECKIN));
			;
		} else if (Event.CAL_ID.equals(itemKey.getType())) {
			objCriterion.addCriterion(
					(WithCriterion) SearchRestriction
							.withContained(User.class, SearchScope.CHILDREN,
									maxRelatedElements, 0)
							.aliasedBy(APIS_ALIAS_USER_LIKERS)
							.with(Media.class)).addCriterion(
					(ApisCriterion) SearchRestriction
							.adaptKey(eventLocationAdapter)
							.aliasedBy(Constants.APIS_ALIAS_EVENT_PLACE)
							.with(Media.class, MediaRequestTypes.THUMB));
			// Getting comments count
			objCriterion.addCriterion(SearchRestriction.with(Comment.class, 1,
					0).aliasedBy(APIS_ALIAS_COMMENTS));
		}
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(objCriterion)
				.addCriterion(
						SearchRestriction.searchAll(User.class,
								SearchScope.EVENTS, 0, 0).facettedBy(
								Arrays.asList(
										SearchHelper.getUserPlacesCategory(),
										SearchHelper.getUserEventsCategory())));
		if (getNxtpUserToken() != null) {
			// Fetching user if defined with liked elements
			final ApisCriterion userCriterion = (ApisCriterion) currentUserSupport
					.createApisCriterionFor(getNxtpUserToken(), false).with(
							SearchRestriction.with(
									ApisRegistry.getModelFromType(itemKey
											.getType())).aliasedBy(
									Constants.APIS_ALIAS_FAVORITE));

			// If localization is provided, we fetch the nearest place
			if (lat != null && lng != null) {
				userCriterion.addCriterion(SearchRestriction.searchNear(
						Place.class, SearchScope.NEARBY_BLOCK, lat, lng,
						radius, 5, 0).aliasedBy(APIS_ALIAS_NEARBY_PLACES));
			}
			// Appending the user criterion
			request.addCriterion(userCriterion);
		}
		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Initializing current user
		currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Extracting overviewed element
		final CalmObject o = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PLACE);
		final PaginationInfo likePagination = response
				.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		overviewSupport.initialize(getUrlService(), getLocale(), o,
				likePagination == null ? 0 : likePagination.getItemCount(), 0,
				currentUser);
		mediaProvider.initialize(o.getKey(), o.get(Media.class));
		tagSupport.initialize(getLocale(), Collections.EMPTY_LIST);
		descriptionSupport.initialize(getUrlService(), getLocale(), o,
				o.get(Description.class));

		// Checking user validity and performs any timeout update
		checkCurrentUser(currentUser);

		// Initializing user beans & info
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Localizing user if information is provided
		if (lat != null && lng != null) {

			// Getting the nearest places from lat / lng
			final List<? extends Place> places = currentUser.get(Place.class,
					APIS_ALIAS_NEARBY_PLACES);

			// Localizing user
			localizationService.localize(currentUser, places, response, lat,
					lng);
		}

		// Saving overview object for JSON exposure
		this.overviewObject = o;

		// Saving the viewed item
		viewManagementService.logViewedOverview(o, currentUser);

		return SUCCESS;
	}

	@Override
	public void setOverviewSupport(OverviewSupport overviewSupport) {
		this.overviewSupport = overviewSupport;
	}

	@Override
	public OverviewSupport getOverviewSupport() {
		return overviewSupport;
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
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}

	@Override
	public ItemsListBoxSupport getDescriptionSupport() {
		return descriptionSupport;
	}

	@Override
	public void setDescriptionSupport(ItemsListBoxSupport descriptionSupport) {
		this.descriptionSupport = descriptionSupport;
	}

	@Override
	public String getJson() {
		JsonLiker json = null;
		if (Place.CAL_TYPE.equals(overviewObject.getKey().getType())) {
			JsonOverviewElement jsonElement = jsonBuilder.buildJsonOverview(
					getLocale(), overviewObject);
			json = jsonElement;
			// Filling like and nearby facetting
			final FacetInformation likesFacetInfo = response
					.getFacetInformation(SearchScope.CHILDREN);
			jsonBuilder.fillJsonLikeFacets(getLocale(), jsonElement,
					likesFacetInfo);
			final FacetInformation nearbyFacetInfo = response
					.getFacetInformation(SearchScope.NEARBY_BLOCK);
			jsonBuilder.fillJsonUserFacets(getLocale(), jsonElement,
					nearbyFacetInfo);

			// Filling counts of likes and users near
			final PaginationInfo nearPagination = response
					.getPaginationInfo(APIS_ALIAS_USER_NEAR);
			jsonElement.setUsers(nearPagination.getItemCount());

			// Adding all users who are in or were in a place
			final Set<String> addedUserKeys = new HashSet<String>();

			// Users lastly localized in that place
			final List<? extends User> inUsers = overviewObject.get(User.class,
					APIS_ALIAS_USER_NEAR);
			for (User user : inUsers) {
				if (!addedUserKeys.contains(user.getKey().toString())) {
					// Building JSON representation
					final JsonLightUser jsonUser = jsonBuilder
							.buildJsonLightUser(user, highRes, getLocale());
					jsonElement.addInUser(jsonUser);
					// Adding key
					addedUserKeys.add(user.getKey().toString());
				}
			}

			// Appending users who recently were there
			final List<? extends Activity> activities = overviewObject
					.get(Activity.class);
			for (Activity a : activities) {

				// Only considering localization activities
				if (a.getActivityType() == ActivityType.LOCALIZATION
						|| a.getActivityType() == ActivityType.CHECKIN) {
					try {
						// Getting user of this activity
						final User user = a.getUnique(User.class,
								Constants.ALIAS_ACTIVITY_USER);

						// If not already in the list
						if (user != null
								&& !addedUserKeys.contains(user.getKey()
										.toString())) {
							// We build JSON representation of it
							final JsonLightUser jsonUser = jsonBuilder
									.buildJsonLightUser(user, highRes,
											getLocale());
							// And add it
							jsonElement.addInUser(jsonUser);
							addedUserKeys.add(user.getKey().toString());
						}
					} catch (CalException e) {
						LOGGER.error(
								"Problems while getting user from a localization activity: "
										+ e.getMessage(), e);
					}
				}
			}

			// Filling localization
			if (overviewObject instanceof Localized) {
				final Localized localized = (Localized) overviewObject;
				jsonElement.setLat(localized.getLatitude());
				jsonElement.setLng(localized.getLongitude());
			}
			// Filling upcoming events
			final List<? extends Event> events = overviewObject
					.get(Event.class);
			for (Event event : events) {
				if (!(event instanceof EventSeries)) {
					final JsonLightEvent jsonEvent = new JsonLightEvent();
					jsonBuilder.fillJsonEvent(jsonEvent, event, highRes,
							getLocale(), response);
					jsonElement.addEvent(jsonEvent);
				}
			}

			// Filling number of comments
			PaginationInfo pagination = response
					.getPaginationInfo(APIS_ALIAS_COMMENTS);
			jsonElement.setCommentsCount(pagination.getItemCount());

			// Image & thumb (TODO: need to factorize this with events and
			// users)
			final Media m = MediaHelper.getSingleMedia(overviewObject);
			if (m != null) {
				final JsonMedia jsonMedia = jsonBuilder.buildJsonMedia(m,
						highRes);
				if (jsonMedia != null) {
					jsonElement.setThumb(jsonMedia);
				}
			}
			for (Media media : overviewObject.get(Media.class)) {
				if (media != m) {
					final JsonMedia jsonMedia = jsonBuilder.buildJsonMedia(
							media, highRes);

					// We select as main image one with higher height for proper
					// mobile display
					if (media.getWidth() < media.getHeight()
							&& m.getWidth() > m.getHeight()) {
						final JsonMedia previousJsonThumb = jsonElement
								.getThumb();
						jsonElement.setThumb(jsonMedia);
						jsonElement.addOtherImage(previousJsonThumb);
					} else if (jsonMedia != null) {
						jsonElement.addOtherImage(jsonMedia);
					}
				}
			}
		} else if (User.CAL_TYPE.equals(overviewObject.getKey().getType())) {
			final User user = (User) overviewObject;
			final JsonUser jsonUser = jsonBuilder.buildJsonUser(user, highRes,
					getLocale());
			json = jsonUser;

			// Setting liked places count
			final PaginationInfo likePagination = response
					.getPaginationInfo(APIS_ALIAS_PLACE_LIKE);
			jsonUser.setLikedPlacesCount(likePagination.getItemCount());

			final List<? extends Place> likesPlaces = overviewObject.get(
					Place.class, APIS_ALIAS_PLACE_LIKE);
			for (Place place : likesPlaces) {
				final JsonLightPlace jsonPlace = jsonBuilder
						.buildJsonLightPlace(place, highRes, getLocale());
				jsonUser.addLikedPlace(jsonPlace);
			}

			// Extracting activities for checkin information
			final List<? extends Activity> activities = overviewObject.get(
					Activity.class, APIS_ALIAS_ACTIVITIES_CHECKIN);
			for (Activity a : activities) {

				// Only considering localization activities
				if (a.getActivityType() == ActivityType.LOCALIZATION
						|| a.getActivityType() == ActivityType.CHECKIN) {
					try {
						// Getting place of this activity
						final Place place = a.getUnique(Place.class,
								Constants.ALIAS_ACTIVITY_TARGET);

						// Making sure we don't add the same place twice (when
						// several checkins)
						final Set<String> placeKeys = new HashSet<String>();

						// If not already in the list
						if (place != null
								&& !placeKeys.contains(place.getKey()
										.toString())) {
							// We build JSON representation of it
							final JsonLightPlace jsonPlace = jsonBuilder
									.buildJsonLightPlace(place, highRes,
											getLocale());
							// And add it
							jsonUser.addCheckedInPlace(jsonPlace);
							placeKeys.add(place.getKey().toString());
						}
					} catch (CalException e) {
						LOGGER.error(
								"Problems while getting user checkins from a localization activity: "
										+ e.getMessage(), e);
					}
				}
			}

			// Generating checkins count
			final PaginationInfo checkinsPagination = response
					.getPaginationInfo(APIS_ALIAS_ACTIVITIES_CHECKIN);
			jsonUser.setCheckedInPlacesCount(checkinsPagination.getItemCount());

		} else if (Event.CAL_ID.equals(overviewObject.getKey().getType())) {
			final JsonEvent jsonEvent = new JsonEvent();
			jsonBuilder.fillJsonEvent(jsonEvent, (Event) overviewObject,
					highRes, getLocale(), response);
			// Filling number of comments
			PaginationInfo pagination = response
					.getPaginationInfo(APIS_ALIAS_COMMENTS);
			jsonEvent.setCommentsCount(pagination.getItemCount());

			json = jsonEvent;
		}
		if (json != null) {
			// Generating like

			List<? extends User> likesUsers = Collections.emptyList();
			if (User.CAL_TYPE.equals(overviewObject.getKey().getType())) {
				likesUsers = overviewObject.get(User.class,
						APIS_ALIAS_USER_LIKED);
				final PaginationInfo likePagination = response
						.getPaginationInfo(APIS_ALIAS_USER_LIKED);
				json.setLikes(likePagination.getItemCount());
			} else {
				likesUsers = overviewObject.get(User.class,
						APIS_ALIAS_USER_LIKERS);
				final PaginationInfo likePagination = response
						.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
				json.setLikes(likePagination.getItemCount());
			}
			for (User user : likesUsers) {
				final JsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(
						user, highRes, getLocale());
				json.addLikeUser(jsonUser);
			}

			// Filling messages count
			int unreadMessagesCount = currentUser.get(Message.class).size();
			json.setUnreadMsgCount(unreadMessagesCount);

			if (getNxtpUserToken() != null) {
				final List<? extends CalmObject> likedObjects = currentUserSupport
						.getCurrentUser().get(CalmObject.class,
								Constants.APIS_ALIAS_FAVORITE);
				for (CalmObject likedObject : likedObjects) {
					if (likedObject.getKey().equals(overviewObject.getKey())) {
						json.setLiked(true);
					}
				}
			}

		}
		return JSONObject.fromObject(json).toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setMaxRelatedElements(int maxRelatedElements) {
		this.maxRelatedElements = maxRelatedElements;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}
}
