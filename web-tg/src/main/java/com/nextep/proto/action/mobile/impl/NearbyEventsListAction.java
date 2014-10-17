package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.NearbySearchAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.model.impl.ApisLocalizationHelper;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.NearbySearchSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.LocalizationService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

public class NearbyEventsListAction extends AbstractAction implements
		SearchAware, JsonProvider, TagAware, NearbySearchAware {

	private static final long serialVersionUID = 2386753201776395502L;
	private static final Log LOGGER = LogFactory
			.getLog(NearbyEventsListAction.class);
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private static final String APIS_ALIAS_NEARBY_EVENTS = "nearbyEvents";
	private static final String APIS_ALIAS_NEARBY_PLACES = "nearbyPlaces";

	// Injected constants
	private double radius;
	private double cityRadius;
	private int pageSize;
	private String baseUrl;

	// Injected supports & services
	private CurrentUserSupport currentUserSupport;
	private SearchSupport searchSupport;
	private TagSupport tagSupport;
	private NearbySearchSupport nearbySearchSupport;
	private LocalizationService localizationService;
	private DistanceDisplayService distanceDisplayService;
	private JsonBuilder jsonBuilder;

	// Actions arguments
	private Double lat, lng;
	private int page;
	private boolean highRes;

	// Internal vars
	private String JsonString;
	private List<JsonLightEvent> jsonList = Collections.emptyList();

	@Override
	protected String doExecute() throws Exception {
		final Collection<FacetCategory> facetCategories = SearchHelper
				.buildUserEventsCategories();

		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.searchNear(Event.class,
										SearchScope.NEARBY_BLOCK, lat, lng,
										radius, pageSize, page)
								.aliasedBy(APIS_ALIAS_NEARBY_EVENTS)
								.with(Description.class)
								.with(Media.class)
								.with(Tag.class)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(eventLocationAdapter)
												.aliasedBy(
														Constants.APIS_ALIAS_EVENT_PLACE)
												.with(Media.class,
														MediaRequestTypes.THUMB)))
				.addCriterion(
						SearchRestriction.searchAll(User.class,
								SearchScope.CHILDREN, 0, 0).facettedBy(
								facetCategories))
				.addCriterion(
						ApisLocalizationHelper.buildNearestCityCriterion(lat,
								lng, cityRadius));

		// Fetching current user
		if (getNxtpUserToken() != null) {
			ApisCriterion userCriterion = currentUserSupport
					.createApisCriterionFor(getNxtpUserToken(), true);
			// If localization is provided, we fetch the nearest place
			if (lat != null && lng != null) {
				userCriterion.addCriterion(SearchRestriction.searchNear(
						Place.class, SearchScope.NEARBY_BLOCK, lat, lng,
						radius, 5, 0).aliasedBy(APIS_ALIAS_NEARBY_PLACES));
			}
			request.addCriterion(userCriterion);
		}

		// Executing request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Checking user's validity
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Extracting facets
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		// Hashing current user events by event key
		Map<String, Integer> currentEventsMap = new HashMap<String, Integer>();
		final List<FacetCount> currentEventCounts = facetInfo
				.getFacetCounts(SearchHelper.getUserEventsCategory());
		for (FacetCount c : currentEventCounts) {
			currentEventsMap.put(c.getFacet().getFacetCode(), c.getCount());
		}

		final List<? extends Event> events = response.getElements(Event.class,
				APIS_ALIAS_NEARBY_EVENTS);

		final PaginationInfo paginationInfo = response
				.getPaginationInfo(Place.class);
		searchSupport.initialize(null, getUrlService(), getLocale(), null,
				null, null, paginationInfo, events);
		tagSupport.initialize(getLocale(), Collections.EMPTY_LIST);
		nearbySearchSupport.initialize(getLocale(), response);
		jsonList = new ArrayList<JsonLightEvent>();
		for (CalmObject o : searchSupport.getSearchResults()) {
			final Event event = (Event) o;
			final JsonLightEvent e = new JsonLightEvent();

			// Filling the JSON structure
			jsonBuilder.fillJsonEvent(e, event, highRes, getLocale(), response);

			// Injecting counts
			final Integer participants = currentEventsMap.get(event.getKey()
					.toString());
			if (participants != null) {
				e.setParticipants(participants);
			}
			jsonList.add(e);
		}

		// Sorting results
		Collections.sort(jsonList, new Comparator<JsonLightEvent>() {
			@Override
			public int compare(JsonLightEvent o1, JsonLightEvent o2) {
				long val1 = o1.getStartTime();
				long val2 = o2.getStartTime();
				return (int) (val1 - val2);
				// Only comparing popularity for events in the same week
				// if (Math.abs(val2 - val1) < 7 * 24 * 60 * 60) {
				// return o2.getParticipants() - o1.getParticipants();
				// } else {
				// long baseTime = Math.min(val1, val2);
				// int weeksDelta1 = ((int) (val1 - baseTime)) / 7 * 24 * 60 *
				// 60;
				// int weeksDelta2 = ((int) (val2 - baseTime)) / 7 * 24 * 60 *
				// 60;
				// return (weeksDelta2 * 100 + o2.getParticipants())
				// - (weeksDelta1 * 100 + o1.getParticipants());
				// }
			}
		});

		// Localizing user
		final MutableUser currentUser = response.getUniqueElement(
				MutableUser.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		if (currentUser != null) {

			// Retrieving user's nearest places
			final List<? extends Place> nearbyPlaces = currentUser.get(
					Place.class, APIS_ALIAS_NEARBY_PLACES);

			// If found, we adjust user localization
			if (nearbyPlaces != null && !nearbyPlaces.isEmpty()) {
				localizationService.localize(currentUser, nearbyPlaces,
						response, lat, lng);
			}
		}
		return SUCCESS;
	}

	@Override
	public String getJson() {
		if (JsonString == null) {
			JsonString = JSONArray.fromObject(jsonList).toString();
		}
		return JsonString;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
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
	public void setNearbySearchSupport(NearbySearchSupport nearbySearchSupport) {
		this.nearbySearchSupport = nearbySearchSupport;
	}

	@Override
	public NearbySearchSupport getNearbySearchSupport() {
		return nearbySearchSupport;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	@Override
	public SearchType getSearchType() {
		return null;
	}

	@Override
	public void setSearchType(SearchType searchType) {

	}

	public void setDistanceDisplayService(
			DistanceDisplayService distanceDisplayService) {
		this.distanceDisplayService = distanceDisplayService;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setCityRadius(double cityRadius) {
		this.cityRadius = cityRadius;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

}
