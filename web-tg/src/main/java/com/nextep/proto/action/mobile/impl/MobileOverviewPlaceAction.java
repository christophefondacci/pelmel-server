package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonHour;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonOverviewElement;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.mobile.model.MobileOverviewService;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
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
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileOverviewPlaceAction extends AbstractAction implements
		JsonProvider, MobileOverviewService {

	// private static final Log LOGGER = LogFactory
	// .getLog(MobileOverviewPlaceAction.class);

	// Constants declaration
	private static final long serialVersionUID = 154177235838836337L;
	private static final String APIS_ALIAS_PLACE = "p";
	private static final String APIS_ALIAS_USER_LIKERS = "ulikers";
	private static final String APIS_ALIAS_USER_CHECKEDIN = "ucheckin";
	private static final String APIS_ALIAS_USER_AUTO_LOCALIZATION = "autoloc";
	private static final String APIS_ALIAS_COMMENTS = "comments";

	// Injected supports
	private ViewManagementService viewManagementService;
	@Autowired
	private EventManagementService eventManagementService;
	private LocalizationService localizationService;
	private CurrentUserSupport currentUserSupport;
	// private MediaProvider mediaProvider;
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
		final ApisCriterion objCriterion = (ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(itemKey)).aliasedBy(APIS_ALIAS_PLACE)
				.with(Description.class).with(Media.class).with(Tag.class);
		// For a place overview we would like to know people who currently are
		// in that place
		final Collection<FacetCategory> userFacetCategories = Arrays
				.asList(SearchHelper.getTagFacetCategory());

		// Checked in users
		objCriterion.addCriterion((ApisCriterion) SearchRestriction
				.withContained(User.class, SearchScope.NEARBY_BLOCK,
						maxRelatedElements, 0).facettedBy(userFacetCategories)
				.aliasedBy(APIS_ALIAS_USER_CHECKEDIN)
				.with(Media.class, MediaRequestTypes.THUMB));
		// Auto-localization
		objCriterion.addCriterion(SearchRestriction.withContained(User.class,
				SearchScope.USER_LOCALIZATION, 1, 0).aliasedBy(
				APIS_ALIAS_USER_AUTO_LOCALIZATION));

		// Likers
		objCriterion.addCriterion((ApisCriterion) SearchRestriction
				.withContained(User.class, SearchScope.CHILDREN,
						maxRelatedElements, 0).facettedBy(userFacetCategories)
				.aliasedBy(APIS_ALIAS_USER_LIKERS)
				.with(Media.class, MediaRequestTypes.THUMB));

		// Events and recurring events
		objCriterion.addCriterion((WithCriterion) SearchRestriction
				.with(Event.class).with(Media.class).with(Description.class));
		objCriterion.addCriterion((WithCriterion) SearchRestriction
				.with(EventSeries.class).with(Media.class)
				.with(Description.class));

		// Getting comments count
		objCriterion.addCriterion(SearchRestriction.with(Comment.class, 1, 0)
				.aliasedBy(APIS_ALIAS_COMMENTS));

		// Querying along with some global user facetting
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(objCriterion)
				.addCriterion(
						SearchRestriction.searchForAllFacets(User.class,
								SearchScope.EVENTS).facettedBy(
								Arrays.asList(
										SearchHelper.getUserPlacesCategory(),
										SearchHelper.getUserEventsCategory())));

		// Fetching user if defined with liked elements
		final ApisCriterion userCriterion = (ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false).with(
						SearchRestriction
								.with(ApisRegistry.getModelFromType(itemKey
										.getType())).aliasedBy(
										Constants.APIS_ALIAS_FAVORITE));

		// If localization is provided, we fetch the nearest place
		if (lat != null && lng != null) {
			userCriterion.addCriterion(SearchRestriction.searchNear(
					Place.class, SearchScope.NEARBY_BLOCK, lat, lng, radius, 5,
					0).aliasedBy(Constants.APIS_ALIAS_NEARBY_PLACES));
		}
		// Appending the user criterion
		request.addCriterion(userCriterion);

		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Initializing current user
		currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Extracting overviewed element
		overviewObject = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PLACE);

		// Checking user validity and performs any timeout update
		checkCurrentUser(currentUser);

		// Initializing user beans & info
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Localizing user if information is provided
		if (lat != null && lng != null) {

			// Getting the nearest places from lat / lng
			final List<? extends Place> places = currentUser.get(Place.class,
					Constants.APIS_ALIAS_NEARBY_PLACES);

			// Localizing user
			localizationService.localize(currentUser, places, response, lat,
					lng);
		}

		// Filling the place of the events (we did not fetch it again) to
		// current place
		for (Event e : overviewObject.get(Event.class)) {
			e.addAll(Constants.APIS_ALIAS_EVENT_PLACE,
					Arrays.asList(overviewObject));
		}
		// Saving the viewed item
		viewManagementService.logViewedOverview(overviewObject, currentUser);

		return SUCCESS;
	}

	@Override
	public String getJson() {
		JsonOverviewElement json = jsonBuilder.buildJsonOverview(getLocale(),
				overviewObject, highRes);
		// Filling like and nearby facetting
		final FacetInformation likesFacetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		jsonBuilder.fillJsonLikeFacets(getLocale(), json, likesFacetInfo);
		final FacetInformation nearbyFacetInfo = response
				.getFacetInformation(SearchScope.NEARBY_BLOCK);
		jsonBuilder.fillJsonUserFacets(getLocale(), json, nearbyFacetInfo);

		// Filling counts of likes and users near
		final PaginationInfo nearPagination = response
				.getPaginationInfo(APIS_ALIAS_USER_CHECKEDIN);
		final PaginationInfo autoLocPagination = response
				.getPaginationInfo(APIS_ALIAS_USER_AUTO_LOCALIZATION);
		json.setUsers(nearPagination.getItemCount()
				+ autoLocPagination.getItemCount());

		// Users lastly localized in that place
		final List<? extends User> inUsers = overviewObject.get(User.class,
				APIS_ALIAS_USER_CHECKEDIN);
		for (User user : inUsers) {
			// Building JSON representation
			final JsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(user,
					highRes, getLocale());
			json.addInUser(jsonUser);
		}

		// Filling localization
		if (overviewObject instanceof Localized) {
			final Localized localized = (Localized) overviewObject;
			json.setLat(localized.getLatitude());
			json.setLng(localized.getLongitude());
		}
		// Filling upcoming events
		final List<? extends Event> events = overviewObject.get(Event.class);
		for (Event event : events) {
			if (!(event instanceof EventSeries)) {
				final JsonLightEvent jsonEvent = new JsonLightEvent();
				jsonBuilder.fillJsonEvent(jsonEvent, event, highRes,
						getLocale(), response);
				json.addEvent(jsonEvent);
			}
		}

		// Filling hours
		final List<? extends EventSeries> series = overviewObject
				.get(EventSeries.class);
		if (series != null) {
			final Collection<JsonHour> hours = jsonBuilder.buildJsonHours(
					series, ((Place) overviewObject).getCity(), getLocale(),
					response);
			json.setHours(hours);
		}
		// Filling number of comments
		PaginationInfo pagination = response
				.getPaginationInfo(APIS_ALIAS_COMMENTS);
		json.setCommentsCount(pagination.getItemCount());

		// Generating like
		List<? extends User> likesUsers = Collections.emptyList();

		// Setting likes
		final PaginationInfo likePagination = response
				.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		json.setLikes(likePagination.getItemCount());

		likesUsers = overviewObject.get(User.class, APIS_ALIAS_USER_LIKERS);
		for (User user : likesUsers) {
			final JsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(user,
					highRes, getLocale());
			json.addLikeUser(jsonUser);
		}

		// Filling messages count
		int unreadMessagesCount = currentUser.get(Message.class).size();
		json.setUnreadMsgCount(unreadMessagesCount);

		// Filling the "liked" flag
		final List<? extends CalmObject> likedObjects = currentUserSupport
				.getCurrentUser().get(CalmObject.class,
						Constants.APIS_ALIAS_FAVORITE);
		for (CalmObject likedObject : likedObjects) {
			if (likedObject.getKey().equals(overviewObject.getKey())) {
				json.setLiked(true);
			}
		}

		return JSONObject.fromObject(json).toString();
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

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}
}
