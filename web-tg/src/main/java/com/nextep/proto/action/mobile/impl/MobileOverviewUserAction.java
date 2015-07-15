package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.mobile.model.MobileOverviewService;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisUserLocationItemKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.EventManagementService;
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
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

import net.sf.json.JSONObject;

public class MobileOverviewUserAction extends AbstractAction implements JsonProvider, MobileOverviewService {

	private static final Log LOGGER = LogFactory.getLog(MobileOverviewUserAction.class);

	// Constants declaration
	private static final long serialVersionUID = 154177235838836337L;
	private static final String APIS_ALIAS_USER = "p";
	private static final String APIS_ALIAS_USER_LIKERS = "ulikers";
	private static final String APIS_ALIAS_USER_LIKED = "liked";
	private static final String APIS_ALIAS_PLACE_LIKE = "plike";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private static final ApisItemKeyAdapter userLocationAdapter = new ApisUserLocationItemKeyAdapter();

	// Injected supports
	private ViewManagementService viewManagementService;
	@Autowired
	private EventManagementService eventManagementService;
	private LocalizationService localizationService;
	private CurrentUserSupport currentUserSupport;
	private JsonBuilder jsonBuilder;
	private int maxRelatedElements;
	private double radius;
	@Resource(mappedName = "smaug/lastSeenMaxTime")
	private Long checkinTime;

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
		final ApisCriterion objCriterion = (ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(itemKey))
				.aliasedBy(APIS_ALIAS_USER).with(Description.class).with(Media.class).with(Tag.class);

		objCriterion.with(SearchRestriction.with(GeographicItem.class))
				// Adding checkin place
				.addCriterion((ApisCriterion) SearchRestriction.adaptKey(userLocationAdapter)
						.aliasedBy(Constants.APIS_ALIAS_USER_LOCATION).with(Media.class, MediaRequestTypes.THUMB))
				// Adding users that he likes
				.addCriterion((ApisCriterion) SearchRestriction.with(User.class, maxRelatedElements, 0)
						.aliasedBy(APIS_ALIAS_USER_LIKED).with(Media.class, MediaRequestTypes.THUMB))
				// Adding users that likes him (count only through pagination)
				.with(SearchRestriction.withContained(User.class, SearchScope.CHILDREN, 1, 0)
						.aliasedBy(APIS_ALIAS_USER_LIKERS))
				// Adding places that he likes
				.addCriterion((ApisCriterion) SearchRestriction.with(Place.class, maxRelatedElements, 0)
						.aliasedBy(APIS_ALIAS_PLACE_LIKE).with(Media.class, MediaRequestTypes.THUMB))
				// Adding events he will be attending with media & location info
				.addCriterion((ApisCriterion) SearchRestriction.with(Event.class).with(Media.class)
						.addCriterion((ApisCriterion) SearchRestriction.adaptKey(eventLocationAdapter)
								.aliasedBy(Constants.APIS_ALIAS_EVENT_PLACE)
								.with(Media.class, MediaRequestTypes.THUMB)))
				// Adding recurring events he will be attending with media &
				// location info
				.addCriterion((ApisCriterion) SearchRestriction.with(EventSeries.class)
						.aliasedBy(Constants.APIS_ALIAS_EVENT_SERIES).with(Media.class)
						.addCriterion((ApisCriterion) SearchRestriction.adaptKey(eventLocationAdapter)
								.aliasedBy(Constants.APIS_ALIAS_EVENT_PLACE)
								.with(Media.class, MediaRequestTypes.THUMB)));

		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest().addCriterion(objCriterion)
				// Adding global events facetting for counting how many persons
				// will attend when displaying events
				.addCriterion(SearchRestriction.searchForAllFacets(User.class, SearchScope.EVENTS).facettedBy(
						Arrays.asList(SearchHelper.getUserPlacesCategory(), SearchHelper.getUserEventsCategory())));

		// Fetching user if defined with liked elements
		final ApisCriterion userCriterion = (ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false)
				.with(SearchRestriction.with(ApisRegistry.getModelFromType(itemKey.getType()))
						.aliasedBy(Constants.APIS_ALIAS_FAVORITE));

		// If localization is provided, we fetch the nearest place
		if (lat != null && lng != null) {
			userCriterion.addCriterion(
					SearchRestriction.searchNear(Place.class, SearchScope.NEARBY_BLOCK, lat, lng, radius, 5, 0)
							.aliasedBy(Constants.APIS_ALIAS_NEARBY_PLACES));
		}
		// Appending the user criterion
		request.addCriterion(userCriterion);
		response = (ApiCompositeResponse) getApiService().execute(request, ContextFactory.createContext(getLocale()));

		// Initializing current user
		currentUser = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Extracting overviewed element
		overviewObject = response.getUniqueElement(CalmObject.class, APIS_ALIAS_USER);

		// Checking user validity and performs any timeout update
		checkCurrentUser(currentUser);

		// Initializing user beans & info
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Localizing user if information is provided
		if (lat != null && lng != null) {

			// Getting the nearest places from lat / lng
			final List<? extends Place> places = currentUser.get(Place.class, Constants.APIS_ALIAS_NEARBY_PLACES);

			// Localizing user
			localizationService.localize(currentUser, places, response, lat, lng);
		}

		// Saving the viewed item
		viewManagementService.logViewedOverview(overviewObject, currentUser);

		return SUCCESS;
	}

	@Override
	public String getJson() {
		final long oldestCheckinTime = System.currentTimeMillis() - checkinTime;

		final User user = (User) overviewObject;
		final JsonUser jsonUser = jsonBuilder.buildJsonUser(user, highRes, getLocale(), response);

		// Setting liked places count
		final PaginationInfo likePagination = response.getPaginationInfo(APIS_ALIAS_PLACE_LIKE);
		jsonUser.setLikedPlacesCount(likePagination.getItemCount());

		final List<? extends Place> likesPlaces = overviewObject.get(Place.class, APIS_ALIAS_PLACE_LIKE);
		for (Place place : likesPlaces) {
			final JsonLightPlace jsonPlace = jsonBuilder.buildJsonLightPlace(place, highRes, getLocale());
			jsonUser.addLikedPlace(jsonPlace);
		}

		// Setting distance
		final double distance = GeoHelper.distanceBetween(lat, lng, user.getLatitude(), user.getLongitude());
		jsonUser.setRawDistanceMeters(distance * Constants.METERS_PER_MILE);

		// Extracting activities for checkin information
		boolean checkedIn = false;
		try {
			final Place userLocation = user.getUnique(Place.class, Constants.APIS_ALIAS_USER_LOCATION);
			if (userLocation != null && user.getLastLocationTime().getTime() > oldestCheckinTime) {
				// We build JSON representation of it
				final JsonLightPlace jsonPlace = jsonBuilder.buildJsonLightPlace(userLocation, highRes, getLocale());
				// And add it
				jsonUser.addCheckedInPlace(jsonPlace);
			}
		} catch (CalException e) {
			LOGGER.error("Unable to get user last checked in location for user '" + user.getKey()
					+ "' / locationKey = '" + user.getLastLocationKey() + "': " + e.getMessage(), e);
		}

		// Generating checkins count
		jsonUser.setCheckedInPlacesCount(checkedIn ? 1 : 0);

		// Generating like
		List<? extends User> likesUsers = Collections.emptyList();
		likesUsers = overviewObject.get(User.class, APIS_ALIAS_USER_LIKED);
		final PaginationInfo userLikePagination = response.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		jsonUser.setLikes(userLikePagination.getItemCount());

		for (User likesUser : likesUsers) {
			final JsonLightUser jsonLikeUser = jsonBuilder.buildJsonLightUser(likesUser, highRes, getLocale());
			jsonUser.addLikeUser(jsonLikeUser);
		}

		// Filling messages count
		jsonBuilder.fillMessagingUnreadCount(jsonUser, currentUser);

		// Filling the "liked" flag
		final List<? extends CalmObject> likedObjects = currentUserSupport.getCurrentUser().get(CalmObject.class,
				Constants.APIS_ALIAS_FAVORITE);
		for (CalmObject likedObject : likedObjects) {
			if (likedObject.getKey().equals(overviewObject.getKey())) {
				jsonUser.setLiked(true);
			}
		}
		return JSONObject.fromObject(jsonUser).toString();
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	@Override
	public boolean isHighRes() {
		return highRes;
	}

	public void setMaxRelatedElements(int maxRelatedElements) {
		this.maxRelatedElements = maxRelatedElements;
	}

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setViewManagementService(ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}
}
