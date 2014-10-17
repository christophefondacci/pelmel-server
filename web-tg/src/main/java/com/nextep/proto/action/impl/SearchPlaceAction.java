package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.Childable;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.GeoPlaceTypesAware;
import com.nextep.proto.action.model.HeaderPopularAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MapAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.NearbySearchAware;
import com.nextep.proto.action.model.PopularAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisCityToCountryAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.apis.adapters.ApisLocalizedBannersAdapter;
import com.nextep.proto.blocks.ChildSupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.GeoPlaceTypesSupport;
import com.nextep.proto.blocks.HeaderSearchSupport;
import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.NearbySearchSupport;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchCriterion;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

public class SearchPlaceAction extends AbstractAction implements SearchAware,
		LocalizationAware, MessagingAware, CurrentUserAware, PopularAware,
		Childable, NearbySearchAware, MapAware, TagAware, HeaderPopularAware,
		GeoPlaceTypesAware {

	// Constants declaration
	private static final long serialVersionUID = -7514162123751575502L;
	private static final String APIS_ALIAS_PLACE_LIST = "plist";
	private static final String APIS_ALIAS_GEO = "geo";
	private static final String APIS_ALIAS_CITIES = "cities";
	private static final String APIS_ALIAS_SUPER_CITIES = "superCities";
	private static final String APIS_ALIAS_PLACE_FACETS = "pFacets";
	private static final String APIS_ALIAS_SPONSORED = "sponsored";

	private static final RequestType DESC_MAIN_TYPE = DescriptionRequestType.SINGLE_DESC;

	private static final ApisCustomAdapter FACET_CITY_ADAPTER = new ApisFacetToItemKeyAdapter(
			SearchScope.CITY, SearchHelper.getCityFacetCategory(),
			Constants.MAX_PLACE_POPULAR_CITIES);
	private static final ApisCalmObjectAdapter CITY_COUNTRY_ADAPTER = new ApisCityToCountryAdapter();
	private static final ApisCustomAdapter ADS_ADAPTER = new ApisLocalizedBannersAdapter();

	// Injected services & supports
	private SearchSupport searchSupport;
	private LocalizationSupport localizationSupport;
	private SearchSupport placeSearchSupport;
	private CurrentUserSupport currentUserSupport;
	private MessagingSupport messagingSupport;
	private PopularSupport popularSupport;
	private PopularSupport headerPopularSupport;
	private PopularSupport sponsoredPopularSupport;
	private ChildSupport childSupport;
	private NearbySearchSupport nearbySearchSupport;
	private MapSupport mapSupport;
	private CalService geoService;
	private TagSupport tagSupport;
	private GeoPlaceTypesSupport geoPlaceTypesSupport;
	private ViewManagementService viewManagementService;
	private int placesPerPage = 16;

	// Dynamic action arguments
	private double nearbyRadius;
	private String geoKey;
	private String facets;
	private String amenities;
	private int pageOffset = 0;
	private SearchType searchType;

	// Internal variables
	private boolean hasRoot = true;

	@Override
	protected String doExecute() throws Exception {
		hasRoot = geoKey != null;
		final ItemKey geoItemKey = geoKey == null ? null : CalmFactory
				.parseKey(geoKey);

		// Building our request with first attempt : inside search
		ApisRequest request = buildApisRequest(geoItemKey, false);
		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		// Fetching results
		GeographicItem geopoint = response.getUniqueElement(
				GeographicItem.class, APIS_ALIAS_GEO);
		getHeaderSupport().initialize(getLocale(), geopoint,
				getSearchSupport(), searchType);
		if (geopoint == null) {
			return notFoundStatus();
		}
		// Checking whether or not we have results
		Collection<? extends Place> places = null;

		if (!hasRoot) {
			places = response.getElements(Place.class, APIS_ALIAS_PLACE_LIST);
		} else {
			places = geopoint.get(Place.class);
			if (places == null || places.isEmpty()) {
				if (geopoint instanceof Localized) {
					// If we have nothing, second attempt : nearby search
					request = buildApisRequest(geoItemKey, true);
					response = (ApiCompositeResponse) getApiService().execute(
							request, ContextFactory.createContext(getLocale()));

					// Extracting root geopoint
					geopoint = response.getUniqueElement(GeographicItem.class,
							APIS_ALIAS_GEO);

					// Initializing nearby support
					nearbySearchSupport.initialize(getLocale(), response);

					// Flagging this page as nearby for SEO
					if (getHeaderSupport() instanceof HeaderSupport) {
						((HeaderSearchSupport) getHeaderSupport())
								.setNearbySearch(true);
					}
				}
			}
		}

		// Current user setup
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		try {
			checkCurrentUser(currentUser);
			currentUserSupport.initialize(getUrlService(), currentUser);
			// Instant messaging setup
			messagingSupport.initialize(getUrlService(), getLocale(),
					currentUser.get(Message.class),
					response.getPaginationInfo(Message.class),
					currentUser.getKey(), getHeaderSupport().getPageStyle());
		} catch (UserLoginTimeoutException e) {
			// We accept non-logged users
		}

		// Search Support setup
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.POI);

		searchSupport.initialize(searchType, getUrlService(), getLocale(),
				geopoint, DisplayHelper.getName(geopoint), facetInfo,
				response.getPaginationInfo(Place.class),
				geopoint.get(Place.class));
		placeSearchSupport
				.initialize(searchType, getUrlService(), getLocale(), geopoint,
						DisplayHelper.getName(geopoint), facetInfo,
						response.getPaginationInfo(Place.class),
						Collections.EMPTY_LIST);

		// Localization support
		localizationSupport.initialize(searchType, getUrlService(),
				getLocale(), geopoint,
				response.getFacetInformation(SearchScope.POI));

		// Cities for popular cities blocks
		final List<? extends City> cities = response.getElements(City.class,
				APIS_ALIAS_SUPER_CITIES);
		final FacetInformation cityFacetInfo = response
				.getFacetInformation(SearchScope.CITY);
		List<FacetCount> cityFacets = cityFacetInfo.getFacetCounts(SearchHelper
				.getPlacePopularCitiesFacetCategory());

		// Initializing header pre-built geo dropdown lists
		List<FacetCount> subGeofacets = facetInfo.getFacetCounts(SearchHelper
				.getGeoSubFacettingCategory(geoItemKey));
		List<? extends GeographicItem> geoSubItems = response.getElements(
				GeographicItem.class, APIS_ALIAS_CITIES);
		headerPopularSupport.initialize(searchType, getLocale(),
				getUrlService(), geopoint, geoSubItems.isEmpty() ? cities
						: geoSubItems, geoSubItems.isEmpty() ? cityFacets
						: subGeofacets);

		popularSupport.initialize(searchType, getLocale(), getUrlService(),
				geopoint, cities, cityFacets);
		childSupport.initialize(getLocale(), getUrlService(), geopoint);

		// Initializing sponsored popular support
		final List<? extends Place> sponsoredPlaces = geopoint.get(Place.class,
				APIS_ALIAS_SPONSORED);
		sponsoredPopularSupport.initialize(searchType, getLocale(),
				getUrlService(), geopoint, sponsoredPlaces, null);

		// Initializing map support
		final Localized mainPoint = geopoint instanceof Localized ? (Localized) geopoint
				: null;
		mapSupport.initialize(mainPoint, geopoint.get(Place.class));

		// Initializing tags support
		tagSupport.initialize(getLocale(), Collections.EMPTY_LIST);

		// Initializing other place types support
		geoPlaceTypesSupport.initialize(getLocale(), getUrlService(), geopoint,
				facetInfo, searchType.getSubtype(), response);

		// Advertising management
		final List<? extends AdvertisingBanner> banners = response
				.getElements(AdvertisingBanner.class,
						ApisLocalizedBannersAdapter.APIS_ALIAS);
		getAdBannerSupport().initialize(getHeaderSupport(), banners);

		// Counting views
		viewManagementService
				.logViewedSearch(geopoint, searchType, currentUser);
		return SUCCESS;
	}

	private ApisRequest buildApisRequest(ItemKey geoItemKey, boolean isNearby)
			throws ApisException, UserLoginTimeoutException, CalException {
		// Preparing facet categories
		final Collection<FacetCategory> userCategories = SearchHelper
				.buildUserFacetCategories();
		final Collection<FacetCategory> placeCategories = SearchHelper
				.buildPlaceFacetCategories(geoItemKey);
		final List<Sorter> placeSorters = SearchHelper.getPlaceDefaultSorters();

		final ApisCriterion currentUserCriterion = currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false);
		final ApisRequest request = ApisFactory.createCompositeRequest();
		request.addCriterion(currentUserCriterion);
		// Building facet filters
		final List<Facet> facetFilters = SearchHelper.buildFacetFilters(
				SearchHelper.getTagFacetCategory(), facets, searchType);
		facetFilters
				.addAll(SearchHelper.buildFacetFilters(
						SearchHelper.getAmenitiesFacetCategory(), amenities,
						searchType));
		// Building geo "super" facetting
		final List<FacetCategory> geoFacetCategories = new ArrayList<FacetCategory>();
		geoFacetCategories.add(SearchHelper.getCityFacetCategory());
		geoFacetCategories.add(SearchHelper.getRegionFacetCategory());

		// Preparing facet filter for popular cities
		final List<Facet> popularCitiesFilters = SearchHelper
				.buildFacetFilters(null, null, searchType);
		// Querying content
		ApisCriterion subCriterion = null;
		if (geoItemKey != null) {
			subCriterion = (ApisCriterion) SearchRestriction
					.alternateKey(GeographicItem.class, geoItemKey)
					.aliasedBy(APIS_ALIAS_GEO)
					.with(Description.class)
					.addCriterion(
							SearchRestriction.customAdapt(ADS_ADAPTER,
									ApisLocalizedBannersAdapter.APIS_ALIAS))
					.addCriterion(
							(ApisCriterion) SearchRestriction.adapt(
									CITY_COUNTRY_ADAPTER).with(
									(WithCriterion) SearchRestriction
											.withContained(Place.class,
													SearchScope.CITY, 5, 0)
											.filteredBy(popularCitiesFilters)
											.facettedBy(geoFacetCategories)
											.aliasedBy(APIS_ALIAS_PLACE_FACETS)
											.customAdapt(FACET_CITY_ADAPTER,
													APIS_ALIAS_SUPER_CITIES)));
		}
		SearchCriterion inOrNearbyPlaceSearchCrit = null;
		if (geoItemKey == null) {
			// If we have no root geo item, we list every place
			inOrNearbyPlaceSearchCrit = (SearchCriterion) SearchRestriction
					.searchAll(Place.class, SearchScope.POI, placesPerPage,
							pageOffset).sortBy(placeSorters)
					.aliasedBy(APIS_ALIAS_PLACE_LIST);
		} else if (!isNearby) {
			// Inside search sorted by default sorters
			inOrNearbyPlaceSearchCrit = (SearchCriterion) SearchRestriction
					.withContained(Place.class, SearchScope.POI, placesPerPage,
							pageOffset).sortBy(placeSorters);

			// Fetching sponsored places
			subCriterion.addCriterion((ApisCriterion) SearchRestriction
					.withContained(Place.class, SearchScope.BOOSTED_PLACES, 5,
							0).aliasedBy(APIS_ALIAS_SPONSORED)
					.with(Media.class, MediaRequestTypes.THUMB));

			// Fetching users and events count
			subCriterion.addCriterion(
					SearchRestriction.withContained(User.class,
							SearchScope.USERS, 0, 0).aliasedBy(
							Constants.APIS_ALIAS_USER_COUNT)).addCriterion(
					SearchRestriction.withContained(Event.class,
							SearchScope.EVENTS, 0, 0).aliasedBy(
							Constants.APIS_ALIAS_EVENT_COUNT));
		} else {
			// Nearby search sorted by distance
			inOrNearbyPlaceSearchCrit = (SearchCriterion) SearchRestriction
					.withNearest(Place.class, SearchScope.POI, placesPerPage,
							pageOffset, nearbyRadius).sortBy(
							SearchHelper.getDistanceSorter());
			// Fetching sponsored places
			subCriterion.addCriterion((ApisCriterion) SearchRestriction
					.withNearest(Place.class, SearchScope.BOOSTED_PLACES, 5, 0,
							nearbyRadius).aliasedBy(APIS_ALIAS_SPONSORED)
					.with(Media.class, MediaRequestTypes.THUMB));

		}
		// Facetting, filtering and adding content
		final ApisCustomAdapter facetAdapter = new ApisFacetToItemKeyAdapter(
				SearchScope.POI,
				SearchHelper.getGeoSubFacettingCategory(geoItemKey),
				Constants.MAX_PLACE_POPULAR_CITIES);

		inOrNearbyPlaceSearchCrit.facettedBy(placeCategories)
				.filteredBy(facetFilters).with(Tag.class).with(Media.class)
				.with(Description.class, DESC_MAIN_TYPE)
				.customAdapt(facetAdapter, APIS_ALIAS_CITIES);
		// We add to our sub criterion if we have a geo root
		if (subCriterion != null) {
			subCriterion.addCriterion(inOrNearbyPlaceSearchCrit);
			request.addCriterion(subCriterion);
		} else {
			request.addCriterion(inOrNearbyPlaceSearchCrit);
		}

		return request;
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	public void setGeoKey(String geoKey) {
		this.geoKey = geoKey;
	}

	public String getGeoKey() {
		return geoKey;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	public void setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	public void setFacets(String facets) {
		this.facets = facets;
	}

	public String getFacets() {
		return facets;
	}

	public void setPlaceSearchSupport(SearchSupport placeSearchSupport) {
		this.placeSearchSupport = placeSearchSupport;
	}

	public SearchSupport getPlaceSearchSupport() {
		return placeSearchSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public PopularSupport getPopularSupport() {
		return popularSupport;
	}

	@Override
	public void setPopularSupport(PopularSupport popularSupport) {
		this.popularSupport = popularSupport;
	}

	public void setGeoService(CalService geoService) {
		this.geoService = geoService;
	}

	@Override
	public void setChildSupport(ChildSupport childSupport) {
		this.childSupport = childSupport;
	}

	@Override
	public ChildSupport getChildSupport() {
		return childSupport;
	}

	public void setNearbyRadius(double nearbyRadius) {
		this.nearbyRadius = nearbyRadius;
	}

	public double getNearbyRadius() {
		return nearbyRadius;
	}

	@Override
	public NearbySearchSupport getNearbySearchSupport() {
		return nearbySearchSupport;
	}

	@Override
	public void setNearbySearchSupport(NearbySearchSupport nearbySearchSupport) {
		this.nearbySearchSupport = nearbySearchSupport;
	}

	@Override
	public void setMapSupport(MapSupport mapSupport) {
		this.mapSupport = mapSupport;
	}

	@Override
	public MapSupport getMapSupport() {
		return mapSupport;
	}

	@Override
	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	@Override
	public SearchType getSearchType() {
		return searchType;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public String getAmenities() {
		return amenities;
	}

	@Override
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}

	public void setPlacesPerPage(int placesPerPage) {
		this.placesPerPage = placesPerPage;
	}

	@Override
	public void setHeaderPopularSupport(PopularSupport geoSubPopularSupport) {
		this.headerPopularSupport = geoSubPopularSupport;
	}

	@Override
	public PopularSupport getHeaderPopularSupport() {
		return headerPopularSupport;
	}

	@Override
	public void setGeoPlaceTypesSupport(
			GeoPlaceTypesSupport geoPlaceTypesSupport) {
		this.geoPlaceTypesSupport = geoPlaceTypesSupport;
	}

	@Override
	public GeoPlaceTypesSupport getGeoPlaceTypesSupport() {
		return geoPlaceTypesSupport;
	}

	public PopularSupport getSponsoredPopularSupport() {
		return sponsoredPopularSupport;
	}

	public void setSponsoredPopularSupport(
			PopularSupport sponsoredPopularSupport) {
		this.sponsoredPopularSupport = sponsoredPopularSupport;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}
}
