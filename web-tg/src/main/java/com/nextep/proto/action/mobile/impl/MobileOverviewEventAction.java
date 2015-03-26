package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.mobile.model.MobileOverviewService;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisExpirableLikesCustomAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileOverviewEventAction extends AbstractAction implements
		JsonProvider, MobileOverviewService {

	private static final Log LOGGER = LogFactory
			.getLog(MobileOverviewEventAction.class);

	// Constants declaration
	private static final long serialVersionUID = 154177235838836337L;
	private static final String APIS_ALIAS_EVENT = "p";
	private static final String APIS_ALIAS_USER_LIKERS = "ulikers";
	private static final String APIS_ALIAS_NEARBY_PLACES = "nearbyPlaces";
	private static final String APIS_ALIAS_COMMENTS = "comments";

	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

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

		// Getting the overview event
		final ApisCriterion objCriterion = (ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(itemKey)).aliasedBy(APIS_ALIAS_EVENT)
				.with(Description.class).with(Media.class).with(Tag.class);

		// For recurring events, we need to do a specific search that looks for
		// "likers" (attendees)
		// with an expiration date matching the next expiration date of the
		// series
		if (itemKey.getType().equals(EventSeries.SERIES_CAL_ID)) {

			// The role of this adapter is to compute the expiration date of the
			// parent series and
			// to perform the search for likers (attendees in this case) with
			// the expiration date
			// so that expired "likes" will not be counted or returned
			final ApisCustomAdapter customLikeAdapter = new ApisExpirableLikesCustomAdapter(
					eventManagementService, APIS_ALIAS_USER_LIKERS,
					maxRelatedElements, 0);

			// This specific adaptation needs to be plugged to the root of the
			// response
			// because of the custom adapter
			objCriterion.addCriterion(SearchRestriction.customAdapt(
					customLikeAdapter, APIS_ALIAS_USER_LIKERS));
		} else {

			// Classic "likes" (attendees) retrieval, we search for users
			// flagged with this
			// event key
			objCriterion.addCriterion((WithCriterion) SearchRestriction
					.withContained(User.class, SearchScope.CHILDREN,
							maxRelatedElements, 0)
					.aliasedBy(APIS_ALIAS_USER_LIKERS).with(Media.class));
		}

		// Getting event location with thumb
		objCriterion.addCriterion((ApisCriterion) SearchRestriction
				.adaptKey(eventLocationAdapter)
				.aliasedBy(Constants.APIS_ALIAS_EVENT_PLACE)
				.with(Media.class, MediaRequestTypes.THUMB));

		// Getting comments count
		objCriterion.addCriterion(SearchRestriction.with(Comment.class, 1, 0)
				.aliasedBy(APIS_ALIAS_COMMENTS));

		// Building the request
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(objCriterion)
				// Adding global events facetting for knowing how many people
				// will be attending
				.addCriterion(
						SearchRestriction.searchForAllFacets(User.class,
								SearchScope.EVENTS).facettedBy(
								Arrays.asList(
										SearchHelper.getUserPlacesCategory(),
										SearchHelper.getUserEventsCategory())));

		// Fetching user with liked elements matching the type of the requested
		// overview object
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
					0).aliasedBy(APIS_ALIAS_NEARBY_PLACES));
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
				APIS_ALIAS_EVENT);

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

		// Saving the viewed item
		viewManagementService.logViewedOverview(overviewObject, currentUser);

		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonEvent jsonEvent = new JsonEvent();
		jsonBuilder.fillJsonEvent(jsonEvent, (Event) overviewObject, highRes,
				getLocale(), response);
		// Filling number of comments
		PaginationInfo pagination = response
				.getPaginationInfo(APIS_ALIAS_COMMENTS);
		jsonEvent.setCommentsCount(pagination.getItemCount());

		// Generating like
		List<? extends User> likesUsers = Collections.emptyList();

		// Setting likes as both likes and participants (like might be later
		// used
		// for real "likes" if needed)
		final PaginationInfo likePagination = response
				.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		jsonEvent.setLikes(likePagination.getItemCount());
		jsonEvent.setParticipants(likePagination.getItemCount());

		try {
			likesUsers = response.getElements(User.class,
					APIS_ALIAS_USER_LIKERS);
		} catch (ApisException e) {
			likesUsers = Collections.emptyList();
			LOGGER.error("Unable to get LIKERS of EventSeries '"
					+ overviewObject.getKey() + "': " + e.getMessage(), e);
		}
		for (User user : likesUsers) {
			final JsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(user,
					highRes, getLocale());
			jsonEvent.addLikeUser(jsonUser);
		}

		// Filling messages count
		int unreadMessagesCount = currentUser.get(Message.class).size();
		jsonEvent.setUnreadMsgCount(unreadMessagesCount);

		// Filling the "liked" flag
		final List<? extends CalmObject> likedObjects = currentUserSupport
				.getCurrentUser().get(CalmObject.class,
						Constants.APIS_ALIAS_FAVORITE);
		for (CalmObject likedObject : likedObjects) {
			if (likedObject.getKey().equals(overviewObject.getKey())) {
				jsonEvent.setLiked(true);
			}
		}

		return JSONObject.fromObject(jsonEvent).toString();
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
