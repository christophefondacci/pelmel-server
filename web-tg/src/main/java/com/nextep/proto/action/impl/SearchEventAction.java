package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.Childable;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.GeoPlaceTypesAware;
import com.nextep.proto.action.model.HeaderPopularAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MapAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.PopularAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.blocks.ChildSupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.GeoPlaceTypesSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

public class SearchEventAction extends AbstractAction implements SearchAware,
		LocalizationAware, MessagingAware, CurrentUserAware, PopularAware,
		MapAware, Childable, MediaAware, HeaderPopularAware, GeoPlaceTypesAware {

	// Constants declaration
	private static final long serialVersionUID = 3058582101295026496L;
	private static final String APIS_ALIAS_GEOITEM = "geo";
	private static final String APIS_ALIAS_FACETS = "facets";
	private static final ApisItemKeyAdapter eventLocalizationAdapter = new ApisEventLocationAdapter();
	private static final SearchType searchType = SearchType.EVENTS;
	private static final ApisCustomAdapter FACET_ADAPTER = new ApisFacetToItemKeyAdapter(
			SearchScope.CHILDREN, SearchHelper.getEventPopularFacetCategory(),
			Constants.MAX_EVENT_POPULAR_CITIES);

	// Injected services & supports
	private SearchSupport searchSupport;
	private LocalizationSupport localizationSupport;
	private MessagingSupport messagingSupport;
	private CurrentUserSupport currentUserSupport;
	private PopularSupport popularSupport;
	private MapSupport mapSupport;
	private CalService geoService;
	private ChildSupport childSupport;
	private MediaProvider mediaProvider;
	private PopularSupport headerPopularSupport;
	private ViewManagementService viewManagementService;
	private GeoPlaceTypesSupport geoPlaceTypesSupport;

	// Dynamic action parameters
	private String geoKey;
	private int pageOffset = 0;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey geoItemKey = CalmFactory.parseKey(geoKey);
		final Collection<FacetCategory> eventFacets = SearchHelper
				.buildEventFacetCategories();
		final Collection<FacetCategory> placeFacets = Arrays
				.asList(SearchHelper.getPlaceTypeCategory());
		final List<Sorter> sorters = SearchHelper.getEventDefaultSorters();
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.alternateKey(GeographicItem.class, geoItemKey)
								.aliasedBy(APIS_ALIAS_GEOITEM)
								.with((WithCriterion) SearchRestriction
										.withContained(Event.class,
												SearchScope.CHILDREN,
												Constants.EVENTS_PER_PAGE,
												pageOffset)
										.facettedBy(eventFacets)
										.sortBy(sorters)
										.customAdapt(FACET_ADAPTER,
												APIS_ALIAS_FACETS)
										.addCriterion(
												(ApisCriterion) SearchRestriction
														.adaptKey(
																eventLocalizationAdapter)
														.with(Media.class))
										.with(Media.class)
										.with(Description.class,
												DescriptionRequestType.SINGLE_DESC))
								.with(SearchRestriction
										.withContained(User.class,
												SearchScope.USERS, 0, 0)
										.aliasedBy(
												Constants.APIS_ALIAS_USER_COUNT))
								.with(SearchRestriction.withContained(
										Place.class, SearchScope.PLACES, 0, 0)
										.facettedBy(placeFacets)));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final GeographicItem currentPlace = response.getUniqueElement(
				GeographicItem.class, APIS_ALIAS_GEOITEM);
		getHeaderSupport().initialize(getLocale(), currentPlace,
				getSearchSupport(), SearchType.EVENTS);
		if (currentPlace == null) {
			return notFoundStatus();
		}
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		try {
			checkCurrentUser(currentUser);
			messagingSupport.initialize(getUrlService(), getLocale(),
					currentUser.get(Message.class),
					response.getPaginationInfo(Message.class),
					currentUser.getKey(), getHeaderSupport().getPageStyle());
			currentUserSupport.initialize(getUrlService(), currentUser);
		} catch (UserLoginTimeoutException e) {
			// Non blocking logged page
		}
		final List<? extends Event> events = currentPlace.get(Event.class);
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		localizationSupport.initialize(searchType, getUrlService(),
				getLocale(), currentPlace, facetInfo);
		searchSupport.initialize(searchType, getUrlService(), getLocale(),
				currentPlace, DisplayHelper.getName(currentPlace), facetInfo,
				response.getPaginationInfo(Event.class), events);

		// Fetching cities from city facets for the popular block
		List<FacetCount> facets = facetInfo.getFacetCounts(SearchHelper
				.getEventPopularFacetCategory());
		final List<? extends City> cities = response.getElements(City.class,
				APIS_ALIAS_FACETS);
		popularSupport.initialize(searchType, getLocale(), getUrlService(),
				currentPlace, cities, facets);

		// Initializing header popular
		headerPopularSupport.initialize(searchType, getLocale(),
				getUrlService(), currentPlace, cities, facets);

		// Initializing map support
		final List<CalmObject> mapPoints = new ArrayList<CalmObject>();
		for (Event event : events) {
			final GeographicItem geoItem = event
					.getUnique(GeographicItem.class);
			if (geoItem instanceof Localized) {
				mapPoints.add(geoItem);
			}
		}
		mapSupport.initialize(
				currentPlace instanceof Localized ? (Localized) currentPlace
						: null, mapPoints);
		childSupport.initialize(getLocale(), getUrlService(), currentPlace);

		// Initializing media
		final List<Media> medias = new ArrayList<Media>();
		for (Event event : events) {
			final Media media = MediaHelper.getSingleMedia(event);
			if (media != null) {
				medias.add(media);
			}
		}
		mediaProvider.initialize(currentPlace.getKey(), medias);

		// Initializing geo place types
		final FacetInformation fi = response
				.getFacetInformation(SearchScope.PLACES);
		getGeoPlaceTypesSupport().initialize(getLocale(), getUrlService(),
				currentPlace, fi, SearchType.EVENTS.getSubtype(), response);
		// View statistics
		viewManagementService.logViewedSearch(currentPlace, searchType,
				currentUser);
		return SUCCESS;
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
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

	public void setGeoKey(String geoKey) {
		this.geoKey = geoKey;
	}

	public String getGeoKey() {
		return geoKey;
	}

	public void setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	@Override
	public void setPopularSupport(PopularSupport popularSupport) {
		this.popularSupport = popularSupport;
	}

	@Override
	public PopularSupport getPopularSupport() {
		return popularSupport;
	}

	public void setGeoService(CalService geoService) {
		this.geoService = geoService;
	}

	@Override
	public void setSearchType(SearchType searchType) {
	}

	@Override
	public SearchType getSearchType() {
		return searchType;
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
	public void setChildSupport(ChildSupport childSupport) {
		this.childSupport = childSupport;
	}

	@Override
	public ChildSupport getChildSupport() {
		return childSupport;
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
	public void setHeaderPopularSupport(PopularSupport headerPopularSupport) {
		this.headerPopularSupport = headerPopularSupport;
	}

	@Override
	public PopularSupport getHeaderPopularSupport() {
		return headerPopularSupport;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
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

}
