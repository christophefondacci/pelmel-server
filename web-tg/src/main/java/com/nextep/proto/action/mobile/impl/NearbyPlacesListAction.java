package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonLightCity;
import com.nextep.json.model.impl.JsonLightEvent;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonNearbyPlacesResponse;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.NearbySearchAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.apis.model.impl.ApisLocalizationHelper;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.NearbySearchSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;
import com.videopolis.smaug.model.FacetCount;

public class NearbyPlacesListAction extends AbstractAction implements
		SearchAware, JsonProvider, TagAware, NearbySearchAware {

	private static final long serialVersionUID = 2386753201776395502L;
	// private static final Log LOGGER = LogFactory
	// .getLog(NearbyPlacesListAction.class);

	private static String APIS_ALIAS_NEARBY_PLACES = "np";
	private static String APIS_ALIAS_NEARBY_EVENTS = "ne";
	private static String APIS_ALIAS_NEARBY_USERS = "nu";
	private static String APIS_ALIAS_CITY = "parentCity";

	private static final int NEARBY_USERS_COUNT = 50;
	private static final int NEARBY_EVENTS_COUNT = 50;

	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	// Injected constants
	private double radius;
	private double cityRadius;
	private int pageSize;

	// Injected supports & services
	private CurrentUserSupport currentUserSupport;
	private SearchSupport searchSupport;
	private TagSupport tagSupport;
	private NearbySearchSupport nearbySearchSupport;
	private LocalizationService localizationService;
	private DistanceDisplayService distanceDisplayService;
	private CalPersistenceService geoService;
	private ViewManagementService viewManagementService;
	private JsonBuilder jsonBuilder;

	// Actions arguments
	private double lat, lng;
	private String searchLatStr, searchLngStr;
	private int page;
	private boolean highRes;
	private String searchText;
	private String parentKey;

	// Internal vars
	private String JsonString;
	private JsonNearbyPlacesResponse jsonResponse;

	@Override
	protected String doExecute() throws Exception {
		// Processing lat/lng when search differs from user
		Double searchLat, searchLng;
		if (searchLatStr == null && searchLngStr == null) {
			searchLat = lat == 0 ? null : lat;
			searchLng = lng == 0 ? null : lng;
		} else {
			searchLat = Double.parseDouble(searchLatStr);
			searchLng = Double.parseDouble(searchLngStr);
		}
		if (radius < 20) {
			radius = 20;
		}
		// Creating the request
		final ApisRequest request = ApisFactory.createCompositeRequest();
		// Place rating sorter
		final List<Sorter> placeSorters = Arrays.asList(SearchHelper
				.getPlaceRatingSorter());

		// Preparing the places list criterion
		ApisCriterion placesCriterion = null;
		if (searchText == null) {
			if (parentKey == null) {
				if (searchLat != null && searchLng != null) {

					// Looking for nearby places
					placesCriterion = SearchRestriction
							.searchNear(Place.class, SearchScope.NEARBY_BLOCK,
									searchLat, searchLng, radius, pageSize,
									page).sortBy(placeSorters)
							.aliasedBy(APIS_ALIAS_NEARBY_PLACES);

					// Adding activities
					// final List<Sorter> activitiesDateSorter = SearchHelper
					// .getActivitiesDefaultSorter();
					// final ApisCriterion activitiesCrit = SearchRestriction
					// .searchNear(Activity.class,
					// SearchScope.NEARBY_ACTIVITIES, searchLat,
					// searchLng, radius, NEARBY_ACTIVITIES_COUNT,
					// 0).sortBy(activitiesDateSorter)
					// .aliasedBy(APIS_ALIAS_NEARBY_ACTIVITIES);

					// Adding required elements for activity generation
					// ApisActivitiesHelper
					// .addActivityConnectedItemsQuery(activitiesCrit);
					//
					// request.addCriterion(activitiesCrit);

					// Adding users
					final ApisCriterion usersCrit = (ApisCriterion) SearchRestriction
							.searchNear(User.class, SearchScope.USERS,
									searchLat, searchLng, Math.max(radius, 50),
									NEARBY_USERS_COUNT, 0)
							.aliasedBy(APIS_ALIAS_NEARBY_USERS)
							.with(Media.class, MediaRequestTypes.THUMB);
					request.addCriterion(usersCrit);

					// Adding events
					final ApisCriterion eventsCrit = (ApisCriterion) SearchRestriction
							.searchNear(Event.class, SearchScope.EVENTS,
									searchLat, searchLng, radius,
									NEARBY_EVENTS_COUNT, 0)
							.aliasedBy(APIS_ALIAS_NEARBY_EVENTS)
							.with(Media.class, MediaRequestTypes.THUMB)
							.addCriterion(
									(ApisCriterion) SearchRestriction
											.adaptKey(eventLocationAdapter)
											.aliasedBy(
													Constants.APIS_ALIAS_EVENT_PLACE)
											.with(Media.class,
													MediaRequestTypes.THUMB));
					request.addCriterion(eventsCrit);
				} else {
					// Building every city having places by using facets of a
					// place search
					final FacetCategory cityCategory = SearchHelper
							.getCityFacetCategory();
					final ApisFacetToItemKeyAdapter cityKeyAdapter = new ApisFacetToItemKeyAdapter(
							SearchScope.CITY, cityCategory, -1);
					placesCriterion = (ApisCriterion) SearchRestriction
							.searchAll(Place.class, SearchScope.CITY, 10, 0)
							.facettedBy(Arrays.asList(cityCategory))
							.customAdapt(cityKeyAdapter,
									APIS_ALIAS_NEARBY_PLACES);
				}
			} else {
				// If we got a parent, we search for places INSIDE the parent
				// rather than nearby
				placesCriterion = SearchRestriction.withContained(Place.class,
						SearchScope.NEARBY_BLOCK, pageSize, page).aliasedBy(
						APIS_ALIAS_NEARBY_PLACES);
				// Adding users
				final ApisCriterion usersCrit = (ApisCriterion) SearchRestriction
						.withContained(User.class, SearchScope.USERS,
								NEARBY_USERS_COUNT, 0)
						.aliasedBy(APIS_ALIAS_NEARBY_USERS)
						.with(Media.class, MediaRequestTypes.THUMB);
				request.addCriterion(usersCrit);
			}
		} else {
			List<SuggestScope> scopes = new ArrayList<SuggestScope>();
			scopes.add(SuggestScope.DESTINATION);
			scopes.add(SuggestScope.PLACE);
			scopes.add(SuggestScope.EVENT);
			scopes.add(SuggestScope.GEO_FULLTEXT);
			placesCriterion = SearchRestriction.searchFromText(
					GeographicItem.class, scopes, searchText, pageSize)
					.aliasedBy(APIS_ALIAS_NEARBY_PLACES);

			List<SuggestScope> userScopes = Arrays.asList(SuggestScope.USER,
					SuggestScope.GEO_FULLTEXT);
			scopes.add(SuggestScope.GEO_FULLTEXT);
			ApisCriterion usersCrit = (ApisCriterion) SearchRestriction
					.searchFromText(User.class, userScopes, searchText,
							NEARBY_USERS_COUNT)
					.aliasedBy(APIS_ALIAS_NEARBY_USERS)
					.with(Media.class, MediaRequestTypes.THUMB);
			request.addCriterion(usersCrit);
			// FacetCategory cityCategory = SearchHelper.getCityFacetCategory();
			// request.addCriterion(SearchRestriction
			// .searchAll(Place.class, SearchScope.CITY, 10, 0)
			// .facettedBy(Arrays.asList(cityCategory))
			// .aliasedBy(APIS_ALIAS_PLACES_FACETS));
		}

		// Adding default places information
		placesCriterion
				.with(Description.class)
				.with(Media.class)
				.with(Tag.class)
				.with(AdvertisingBooster.class)
				.with((WithCriterion) SearchRestriction.with(EventSeries.class)
						.with(Media.class));
		// Integrating the criterion
		if (parentKey == null) {
			// At the root level by default
			request.addCriterion(placesCriterion);
		} else {
			// Or encapsulated inside a parent search for non-nearby places
			final ItemKey parentItemKey = CalmFactory.parseKey(parentKey);

			// Preparing query of activities for this parent
			// final List<Sorter> activitiesDateSorter = SearchHelper
			// .getActivitiesDefaultSorter();
			// final ApisCriterion activitiesCrit = SearchRestriction
			// .withContained(Activity.class,
			// SearchScope.NEARBY_ACTIVITIES,
			// NEARBY_ACTIVITIES_COUNT, 0)
			// .aliasedBy(APIS_ALIAS_NEARBY_ACTIVITIES)
			// .sortBy(activitiesDateSorter);
			// ApisActivitiesHelper.addActivityConnectedItemsQuery(activitiesCrit);

			// Adding users
			final ApisCriterion usersCrit = (ApisCriterion) SearchRestriction
					.withContained(User.class, SearchScope.USERS,
							NEARBY_USERS_COUNT, 0)
					.aliasedBy(APIS_ALIAS_NEARBY_USERS)
					.with(Media.class, MediaRequestTypes.THUMB);

			// Building parent getter, querying contained places
			request.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(parentItemKey))
					.aliasedBy(APIS_ALIAS_CITY).addCriterion(placesCriterion)
					// .addCriterion(activitiesCrit)
					.addCriterion(usersCrit));
		}
		request.addCriterion(
				SearchRestriction.searchForAllFacets(User.class,
						SearchScope.USER_LOCALIZATION).facettedBy(
						Arrays.asList(SearchHelper
								.getUserCurrentPlaceCategory())))
				.addCriterion(
						SearchRestriction.searchForAllFacets(User.class,
								SearchScope.CHILDREN).facettedBy(
								Arrays.asList(SearchHelper
										.getUserPlacesCategory())))
				.addCriterion(
						SearchRestriction.searchForAllFacets(User.class,
								SearchScope.EVENTS).facettedBy(
								Arrays.asList(SearchHelper
										.getUserEventsCategory())))
				.addCriterion(
						ApisLocalizationHelper.buildNearestCityCriterion(lat,
								lng, cityRadius));

		if (getNxtpUserToken() != null) {
			request.addCriterion((ApisCriterion) currentUserSupport
					.createApisCriterionFor(getNxtpUserToken(), true).with(
							GeographicItem.class));
		}

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Extracting current user
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Checking user status / update timeouts
		checkCurrentUser(user);

		// Extracting facets
		final FacetInformation currentPlaceFacetInfo = response
				.getFacetInformation(SearchScope.USER_LOCALIZATION);
		// Hashing current user places by place key
		Map<String, Integer> currentPlacesMap = new HashMap<String, Integer>();
		final List<FacetCount> currentPlaceCounts = currentPlaceFacetInfo
				.getFacetCounts(SearchHelper.getUserCurrentPlaceCategory());
		for (FacetCount c : currentPlaceCounts) {
			int count = c.getCount();
			// // We never count the current user in the number of persons in a
			// // place so we substract 1 when we fall on the place where the
			// // current user is located
			// if (user != null && user.getLastLocationKey() != null) {
			// if (user.getLastLocationTime() != null
			// && user.getLastLocationKey().toString()
			// .equals(c.getFacet().getFacetCode())) {
			// long timeoutTime = user.getLastLocationTime().getTime()
			// + lastSeenMaxTime;
			// if (timeoutTime > System.currentTimeMillis()) {
			// count--;
			// }
			// }
			// }
			//
			currentPlacesMap.put(c.getFacet().getFacetCode(), count);
		}

		// Hashing liked user places by place key
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		Map<String, Integer> likedPlacesMap = SearchHelper.unwrapFacets(
				facetInfo, SearchHelper.getUserPlacesCategory());

		List<? extends Place> places;
		List<? extends Event> events;
		// List<? extends Activity> activities;
		List<? extends User> users;
		if (parentKey == null) {
			places = response
					.getElements(Place.class, APIS_ALIAS_NEARBY_PLACES);
			// activities = response.getElements(Activity.class,
			// APIS_ALIAS_NEARBY_ACTIVITIES);
			users = response.getElements(User.class, APIS_ALIAS_NEARBY_USERS);
		} else {
			// If we had a parent key set we need to extract places from its
			// parent geographic element
			final GeographicItem city = response.getUniqueElement(
					GeographicItem.class, APIS_ALIAS_CITY);
			places = city.get(Place.class, APIS_ALIAS_NEARBY_PLACES);
			// activities = city.get(Activity.class,
			// APIS_ALIAS_NEARBY_ACTIVITIES);
			users = city.get(User.class, APIS_ALIAS_NEARBY_USERS);
		}
		if (searchText != null) {
			// Because we did a full text search which can return events (among
			// other objects)
			// then everything is under the same "places" alias
			events = response
					.getElements(Event.class, APIS_ALIAS_NEARBY_PLACES);
		} else {
			events = response
					.getElements(Event.class, APIS_ALIAS_NEARBY_EVENTS);
		}
		// final FacetInformation facetInfo = response
		// .getFacetInformation(SearchScope.NEARBY_BLOCK);
		final PaginationInfo paginationInfo = response
				.getPaginationInfo(Place.class);
		searchSupport.initialize(null, getUrlService(), getLocale(), null,
				null, null, paginationInfo, places);
		final List<Tag> tags = Collections.emptyList();
		tagSupport.initialize(getLocale(), tags);
		nearbySearchSupport.initialize(getLocale(), response);
		final List<JsonPlace> jsonPlaces = new ArrayList<JsonPlace>();

		// Preparing a single localized date
		Map<String, List<JsonPlace>> placesCityMap = new HashMap<String, List<JsonPlace>>();
		for (CalmObject o : searchSupport.getSearchResults()) {
			final Place place = (Place) o;

			final JsonPlace p = jsonBuilder.buildJsonPlace(place, highRes,
					getLocale(), likedPlacesMap, currentPlacesMap);

			// Distance management
			final SearchStatistic distanceStat = response.getStatistic(
					o.getKey(), SearchStatistic.DISTANCE);
			if (distanceStat != null) {
				p.setRawDistance(distanceStat.getNumericValue().doubleValue());
				final String distanceString = distanceDisplayService
						.getDistanceFromItem(o.getKey(), response, getLocale());
				p.setDistance(distanceString);
			} else {
				// Setting the city as the distance string if no nearby search
				p.setDistance(DisplayHelper.getName(place.getCity()));
			}

			// Adding place to list
			jsonPlaces.add(p);

			// Hashing places by city
			final String cityKey = place.getCity().getKey().toString();
			List<JsonPlace> jsonCityPlaces = placesCityMap.get(cityKey);
			if (jsonCityPlaces == null) {
				jsonCityPlaces = new ArrayList<JsonPlace>();
				placesCityMap.put(cityKey, jsonCityPlaces);
			}
			jsonCityPlaces.add(p);
		}

		// Building distance map (since search was made based on
		// searchLat/searchLng, the distances are incorrect)
		final Map<String, Double> distanceMap = new HashMap<String, Double>();
		for (JsonPlace p : jsonPlaces) {
			double distance = GeoHelper.distanceBetween(lat, lng, p.getLat(),
					p.getLng());
			distanceMap.put(p.getItemKey(), distance);
			p.setRawDistance(distance);
		}
		// Sorting results
		Collections.sort(jsonPlaces, new Comparator<JsonPlace>() {
			@Override
			public int compare(JsonPlace o1, JsonPlace o2) {
				int boost1 = o1.getBoostValue();
				int boost2 = o2.getBoostValue();
				// if we got 2 different ad boosts we use it for the sort
				if (boost1 != boost2) {
					return boost2 - boost1;
				} else {

					// Else we use the standard sort algorithm based on user
					// inside a place and user liking a place
					int val1 = o1.getUsersCount() * 5000 + o1.getLikesCount()
							* 500;
					int val2 = o2.getUsersCount() * 5000 + o2.getLikesCount()
							* 500;

					int penalty1 = getDistancePenalty(o1.getRawDistance());
					int penalty2 = getDistancePenalty(o2.getRawDistance());
					val1 -= penalty1;
					val2 -= penalty2;
					return val2 - val1;
				}
			}
		});

		final List<? extends City> cities = response.getElements(City.class,
				APIS_ALIAS_NEARBY_PLACES);
		final FacetInformation citiesFacetInfo = response
				.getFacetInformation(SearchScope.CITY);
		final Map<String, Integer> citiesPlacesMap = SearchHelper.unwrapFacets(
				citiesFacetInfo, SearchHelper.getCityFacetCategory());
		final List<JsonLightCity> jsonCities = new ArrayList<JsonLightCity>();
		for (City city : cities) {
			final JsonLightCity jsonCity = new JsonLightCity();
			jsonCity.setKey(city.getKey() == null ? null : city.getKey()
					.toString());
			jsonCity.setName(city.getName());
			jsonCity.setLatitude(city.getLatitude());
			jsonCity.setLongitude(city.getLongitude());
			// Getting the number of facets
			final Integer placesCount = citiesPlacesMap.get(city.getKey()
					.toString());
			if (placesCount != null) {
				jsonCity.setPlacesCount(placesCount);
			} else {
				continue;
			}
			// Building qualified localization ADM1 + COUNTRY
			final StringBuilder cityName = new StringBuilder();
			if (city.getAdm1() != null) {
				cityName.append(DisplayHelper.getName(city.getAdm1()) + ", ");
			}
			cityName.append(DisplayHelper.getName(city.getCountry()));
			jsonCity.setLocalization(cityName.toString());

			// Adding media
			final Media m = MediaHelper.getSingleMedia(city);
			if (m != null) {
				final JsonMedia jsonMedia = jsonBuilder.buildJsonMedia(m,
						highRes);
				jsonCity.setMedia(jsonMedia);
			}
			// Adding JSON city to list
			jsonCities.add(jsonCity);
		}

		// Sorting cities
		Collections.sort(jsonCities, new Comparator<JsonLightCity>() {
			@Override
			public int compare(JsonLightCity o1, JsonLightCity o2) {
				int countDiff = o2.getPlacesCount() - o1.getPlacesCount();
				if (countDiff == 0) {
					return o1.getName().compareTo(o2.getName());
				} else {
					return countDiff;
				}
			}
		});

		// Preparing events
		final List<JsonLightEvent> jsonEvents = new ArrayList<JsonLightEvent>();
		for (Event event : events) {
			final JsonLightEvent jsonEvent = new JsonLightEvent();
			jsonBuilder.fillJsonEvent(jsonEvent, event, highRes, getLocale(),
					response);

			jsonEvents.add(jsonEvent);
		}
		// Sorting by date
		Collections.sort(jsonEvents, new Comparator<JsonLightEvent>() {
			@Override
			public int compare(JsonLightEvent o1, JsonLightEvent o2) {
				return (int) (o1.getStartTime() - o2.getStartTime());
			}
		});
		// Preparing response
		jsonResponse = new JsonNearbyPlacesResponse();
		jsonResponse.setPlaces(jsonPlaces);
		jsonResponse.setCities(jsonCities);
		jsonResponse.setNearbyEvents(jsonEvents);

		// Special SEARCH TEXT filtering when a single city returns 60% of
		// results
		if (searchText != null) {
			for (String key : placesCityMap.keySet()) {
				final List<JsonPlace> cityPlaces = placesCityMap.get(key);
				// Checking if places represents 60% of results
				if (cityPlaces.size() >= 0.6 * jsonPlaces.size()) {
					jsonResponse.setPlaces(cityPlaces);
					break;
				}
			}
		}

		final MutableUser currentUser = response.getUniqueElement(
				MutableUser.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Localizing user city
		final City localizedCity = response.getUniqueElement(City.class,
				ApisLocalizationHelper.APIS_ALIAS_CITY_NEARBY);
		ContextHolder.toggleWrite();
		if (localizedCity != null) {
			geoService.setItemFor(user.getKey(), localizedCity.getKey());
			// currentUser.add(localizedCity);
		}
		if (currentUser != null && searchLat != null && searchLng != null
				&& lat == searchLat.doubleValue()
				&& lng == searchLng.doubleValue()) {
			localizationService.localize(currentUser, places, response, lat,
					lng);
		}

		// Counting views
		if (localizedCity != null) {
			viewManagementService.logViewCount(localizedCity, currentUser,
					Constants.VIEW_TYPE_MOBILE);
		}

		// Extracting activities
		// final PaginationInfo activitiesPagination = response
		// .getPaginationInfo(APIS_ALIAS_NEARBY_ACTIVITIES);
		// activitySupport.initialize(getUrlService(), getLocale(),
		// activitiesPagination, activities);
		// for (Activity activity : activities) {
		// if (activity.getActivityType() != ActivityType.LOCALIZATION
		// && activity.getActivityType() != ActivityType.CITY_CHANGE) {
		// final JsonActivity jsonActivity = jsonBuilder
		// .buildJsonActivity(activity, highRes, getLocale());
		// final String text = activitySupport
		// .getActivityHtmlLine(activity);
		// jsonActivity.setMessage(text);
		// jsonResponse.addNearbyActivity(jsonActivity);
		// }
		// }

		// JSONifying users
		for (User nearbyUser : users) {
			final JsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(
					nearbyUser, highRes, getLocale());
			jsonResponse.addNearbyUser(jsonUser);
		}

		// Building city JSON
		if (localizedCity != null) {
			final JsonLightCity jsonCity = jsonBuilder.buildJsonLightCity(
					localizedCity, highRes, getLocale());
			jsonResponse.setLocalizedCity(jsonCity);
		}
		return SUCCESS;
	}

	private int getDistancePenalty(double distance) {
		int penalty = (int) (distance * 10.0);
		// if (distance < 2) {
		// return 0;
		// } else
		if (distance > 2 && distance < 20) {
			penalty += 2000;
		} else {
			penalty += 5000;
		}
		return penalty;
	}

	@Override
	public String getJson() {
		if (JsonString == null) {
			JsonString = JSONObject.fromObject(jsonResponse).toString();
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

	public double getRadius() {
		return radius;
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

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setGeoService(CalPersistenceService geoService) {
		this.geoService = geoService;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}

	public void setSearchLat(String searchLat) {
		this.searchLatStr = searchLat;
	}

	public String getSearchLat() {
		return searchLatStr;
	}

	public void setSearchLng(String searchLng) {
		this.searchLngStr = searchLng;
	}

	public String getSearchLng() {
		return searchLngStr;
	}
}
