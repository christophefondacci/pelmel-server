package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.properties.model.Property;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.ActivityAware;
import com.nextep.proto.action.model.CalendarAware;
import com.nextep.proto.action.model.Commentable;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionAware;
import com.nextep.proto.action.model.FooterAware;
import com.nextep.proto.action.model.GeoPlaceTypesAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MapAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.MosaicAware;
import com.nextep.proto.action.model.OverviewAware;
import com.nextep.proto.action.model.PropertiesAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisCommentUserKeyAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.apis.adapters.ApisLocalizedBannersAdapter;
import com.nextep.proto.apis.adapters.ApisPlaceAdm1Adapter;
import com.nextep.proto.apis.adapters.ApisPlaceCountryAdapter;
import com.nextep.proto.apis.adapters.ApisPlaceLocationAdapter;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.apis.model.impl.ApisCommentsHelper;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.blocks.CalendarSupport;
import com.nextep.proto.blocks.CommentSupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.blocks.GeoPlaceTypesSupport;
import com.nextep.proto.blocks.ItemsListBoxSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.MosaicSupport;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.blocks.PropertiesSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class PlaceOverviewAction extends AbstractAction implements
		OverviewAware, TagAware, MessagingAware, CurrentUserAware, MediaAware,
		LocalizationAware, MapAware, ActivityAware, PropertiesAware,
		DescriptionAware, Commentable, MosaicAware, FooterAware,
		GeoPlaceTypesAware, CalendarAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1092921943647294480L;
	private static final Log LOGGER = LogFactory
			.getLog(PlaceOverviewAction.class);
	private static final String APIS_ALIAS_PLACE = "place";
	private static final String APIS_ALIAS_TAG_LIST = "tags";
	private static final String APIS_ALIAS_USER_LIKE = "likes";
	private static final String APIS_ALIAS_USER_NEAR = "near";
	private static final String APIS_ALIAS_SUPER_CITIES = "scities";
	private static final String APIS_ALIAS_TOP_COUNTRY_PLACES = "cplaces";
	private static final String APIS_ALIAS_EVENT_SERIES = "series";

	private static final ApisItemKeyAdapter COMMENT_USER_ADAPTER = new ApisCommentUserKeyAdapter();
	private static final ApisCalmObjectAdapter PLACE_COUNTRY_ADAPTER = new ApisPlaceCountryAdapter();
	private static final ApisCalmObjectAdapter PLACE_ADM1_ADAPTER = new ApisPlaceAdm1Adapter();
	private static final ApisCalmObjectAdapter PLACE_LOCATION_ADAPTER = new ApisPlaceLocationAdapter();
	private static final ApisCustomAdapter FACET_CITY_ADAPTER = new ApisFacetToItemKeyAdapter(
			SearchScope.CITY, SearchHelper.getCityFacetCategory(),
			Constants.MAX_PLACE_POPULAR_CITIES);
	private static final ApisCustomAdapter countryFacetAdapter = new ApisFacetToItemKeyAdapter(
			SearchScope.CHILDREN, SearchHelper.getCountryFacetCategory(),
			Constants.MAX_TOP_COUNTRIES);
	private static final ApisCustomAdapter LOCALIZED_BANNERS_ADAPTER = new ApisLocalizedBannersAdapter();

	private LocalizationSupport localizationSupport;
	private OverviewSupport overviewSupport;
	private TagSupport tagSupport;
	private MessagingSupport messagingSupport;
	private CurrentUserSupport currentUserSupport;
	private MediaProvider mediaProvider;
	private SearchSupport localSearchSupport;
	private MapSupport mapSupport;
	private ItemsListBoxSupport eventsListSupport;
	private ActivitySupport activitySupport;
	private FavoritesSupport nearbySupport;
	private PropertiesSupport propertiesSupport;
	private ItemsListBoxSupport descriptionSupport;
	private CommentSupport commentSupport;
	private SelectableTagSupport commentTagSupport;
	private ViewManagementService viewManagementService;
	private PopularSupport popularSupport;
	private PopularSupport secondaryPopularSupport;
	private GeoPlaceTypesSupport geoPlaceTypesSupport;
	private RightsManagementService rightsManagementService;
	private MosaicSupport mosaicSupport;
	private CalendarSupport calendarSupport;

	private String id;

	private ApisRequest buildApisRequestFor(ItemKey placeKey)
			throws ApisException, UserLoginTimeoutException, CalException {
		final String userToken = getNxtpUserToken();
		final ApisRequest request = ApisFactory.createCompositeRequest();
		// Building user request fragment for left column
		if (userToken != null) {
			final ApisCriterion criterion = (ApisCriterion) currentUserSupport
					.createApisCriterionFor(getNxtpUserToken(), false)
					.with(SearchRestriction.with(Place.class).aliasedBy(
							Constants.APIS_ALIAS_FAVORITE))
					.with(ApisCommentsHelper.getUserTagsFor(placeKey));
			request.addCriterion(criterion);
		}
		// Retrieving facets for users
		final Collection<FacetCategory> userFacets = SearchHelper
				.buildUserFacetCategories();

		// Preparing facet filter for popular cities
		final List<Facet> popularCitiesFilters = SearchHelper
				.buildFacetFilters(null, null, getCurrentSearchType());

		// Building APIS request
		request.addCriterion((ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(placeKey))
				.aliasedBy(APIS_ALIAS_PLACE)
				.addCriterion(ApisCommentsHelper.withComments())
				.addCriterion(
						SearchRestriction.customAdapt(
								LOCALIZED_BANNERS_ADAPTER,
								ApisLocalizedBannersAdapter.APIS_ALIAS))
				.with(Tag.class)
				.with(Media.class)
				.with(Description.class)
				.with(AdvertisingBooster.class)
				.with(Property.class)
				.with((WithCriterion) SearchRestriction.with(Event.class)
						.with(Media.class).with(Description.class))
				.with(SearchRestriction.with(EventSeries.class).aliasedBy(
						APIS_ALIAS_EVENT_SERIES))
				.with((WithCriterion) SearchRestriction
						.withContained(User.class, SearchScope.CHILDREN, 20, 0)
						.facettedBy(userFacets).aliasedBy(APIS_ALIAS_USER_LIKE)
						.with(Media.class))
				.with(SearchRestriction
						.withContained(User.class, SearchScope.NEARBY_BLOCK, 0,
								0).facettedBy(userFacets)
						.aliasedBy(APIS_ALIAS_USER_NEAR))
				.with((WithCriterion) SearchRestriction.withNearest(
						Place.class, SearchScope.NEARBY_BLOCK,
						Constants.OVERVIEW_NEARBY_PLACES, 0, 100).with(
						Media.class))
				.with(ApisActivitiesHelper.withActivities(
						Constants.MAX_ACTIVITIES, 0))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.adapt(PLACE_COUNTRY_ADAPTER)
								.with((WithCriterion) SearchRestriction
										.withContained(Place.class,
												SearchScope.CITY, 5, 0)
										.filteredBy(popularCitiesFilters)
										.facettedBy(
												Arrays.asList(SearchHelper
														.getCityFacetCategory()))
										.customAdapt(FACET_CITY_ADAPTER,
												APIS_ALIAS_SUPER_CITIES)))
				.addCriterion(
						(ApisCriterion) SearchRestriction.adapt(
								PLACE_ADM1_ADAPTER).with(
								SearchRestriction
										.withContained(Place.class,
												SearchScope.PLACES,
												Constants.MAX_TOP_PLACES, 0)
										.sortBy(SearchHelper
												.getPlaceRatingSorter())
										.aliasedBy(
												APIS_ALIAS_TOP_COUNTRY_PLACES)))
				.addCriterion(
						(ApisCriterion) SearchRestriction.adapt(
								PLACE_LOCATION_ADAPTER).with(
								SearchRestriction.withContained(Place.class,
										SearchScope.POI, 5, 0).facettedBy(
										Arrays.asList(SearchHelper
												.getPlaceTypeCategory())))));
		request.addCriterion(ApisCommentsHelper.listCommentTags(Place.CAL_TYPE)
				.aliasedBy(APIS_ALIAS_TAG_LIST));
		return request;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doExecute() throws Exception {
		// Parsing place item key
		final ItemKey placeKey = CalmFactory.parseKey(id);

		// Building APIS request
		ApisRequest request = buildApisRequestFor(placeKey);

		// Executing APIS request
		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Fetching place
		Place place = response.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
		if (place == null) {
			return notFoundStatus();
		}
		// Checking place validity / redirection
		if (!place.isOnline() && place.getRedirectionItemKey() != null) {
			// In this case we rebuild information with the redirected place
			request = buildApisRequestFor(place.getRedirectionItemKey());

			// Re-executing (response overhead: this is a second APIS call for a
			// same page)
			response = (ApiCompositeResponse) getApiService().execute(request,
					ContextFactory.createContext(getLocale()));

			// Extracting the new place
			place = response.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
		}
		final SearchType searchType = SearchType.fromPlaceType(place
				.getPlaceType());
		getHeaderSupport().initialize(getLocale(), place, null, searchType);

		// Initializing components
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		try {
			checkCurrentUser(user);
			currentUserSupport.initialize(getUrlService(), user);
			// Messaging
			final List<? extends Message> messages = user.get(Message.class);
			final PaginationInfo pagination = response
					.getPaginationInfo(Message.class);
			messagingSupport.initialize(getUrlService(), getLocale(), messages,
					pagination, user.getKey(), getHeaderSupport()
							.getPageStyle());
		} catch (UserLoginTimeoutException e) {
			// Non blocking
		}

		// We need likes stats
		final PaginationInfo likeUserPagination = response
				.getPaginationInfo(APIS_ALIAS_USER_LIKE);
		overviewSupport.initialize(getUrlService(), getLocale(), place,
				likeUserPagination.getItemCount(), 0, user);
		final List<Tag> tags = (List<Tag>) place.get(Tag.class);
		tagSupport.initialize(getLocale(), tags);

		// Media
		mediaProvider.initialize(placeKey, place.get(Media.class));

		// Localization
		localizationSupport.initialize(searchType, getUrlService(),
				getLocale(), place, null);

		// Search
		final FacetInformation nearFacetInfo = response
				.getFacetInformation(SearchScope.NEARBY_BLOCK);
		final PaginationInfo localUserPagination = response
				.getPaginationInfo(APIS_ALIAS_USER_NEAR);
		localSearchSupport.initialize(searchType, getUrlService(), getLocale(),
				place, DisplayHelper.getName(place), nearFacetInfo,
				localUserPagination, Collections.EMPTY_LIST);

		// Map
		final List<? extends Place> nearbyPlaces = place.get(Place.class);
		mapSupport.initialize(place, nearbyPlaces);

		// Events
		final List<? extends Event> events = place.get(Event.class);
		eventsListSupport.initialize(getUrlService(), getLocale(), place,
				events);

		// Activities
		final List<? extends Activity> activities = place.get(Activity.class);
		final PaginationInfo activitiesPagination = response
				.getPaginationInfo(Activity.class);
		activitySupport.initialize(getUrlService(), getLocale(),
				activitiesPagination, activities);
		activitySupport.initializeTarget(place);

		// Nearbys
		nearbySupport.initilialize(getUrlService(), getLocale(), place,
				nearbyPlaces);
		nearbySupport.setMetadata(response);

		// Properties
		final List<? extends Property> props = place.get(Property.class);
		propertiesSupport.initialize(getLocale(), props, true);

		// Descriptions
		final List<? extends Description> descs = place.get(Description.class);
		descriptionSupport.initialize(getUrlService(), getLocale(), place,
				descs);

		// Comments
		final List<? extends Comment> comments = place.get(Comment.class);
		final PaginationInfo paginationInfo = response
				.getPaginationInfo(Comment.class);
		commentSupport.initialize(getUrlService(), getLocale(), comments,
				paginationInfo, user, place.getKey());

		final List<Tag> commentTags = (List<Tag>) response.getElements(
				Tag.class, APIS_ALIAS_TAG_LIST);
		commentTagSupport.initialize(getLocale(), commentTags);
		commentTagSupport.initializeSelection(getUrlService(), place.getKey(),
				user);

		// Initializing the mosaic
		final List<? extends User> likers = place.get(User.class,
				APIS_ALIAS_USER_LIKE);
		mosaicSupport.initialize(getUrlService(), getLocale(), place,
				activitySupport, likers, null, activities, null);

		// Popular support initialization
		final List<? extends City> popularCities = response.getElements(
				City.class, APIS_ALIAS_SUPER_CITIES);
		popularSupport.initialize(searchType, getLocale(), getUrlService(),
				place, popularCities, null);

		// Secondary popular initialisation
		final Admin placeAdm1 = place.getUnique(Admin.class);
		if (placeAdm1 != null) {
			final List<? extends Place> topPlaces = placeAdm1.get(Place.class,
					APIS_ALIAS_TOP_COUNTRY_PLACES);
			secondaryPopularSupport.initialize(getCurrentSearchType(),
					getLocale(), getUrlService(), placeAdm1, topPlaces, null);
		}

		// Geo place types support initialization
		final FacetInformation geoFacetInfo = response
				.getFacetInformation(SearchScope.POI);
		geoPlaceTypesSupport.initialize(getLocale(), getUrlService(),
				place.getCity(), geoFacetInfo, place.getPlaceType(), response);

		final List<? extends AdvertisingBanner> banners = response
				.getElements(AdvertisingBanner.class,
						ApisLocalizedBannersAdapter.APIS_ALIAS);
		getAdBannerSupport().initialize(getHeaderSupport(), banners);

		// Configuring calendars
		final List<? extends EventSeries> seriesList = place.get(
				EventSeries.class, APIS_ALIAS_EVENT_SERIES);
		calendarSupport.initialize(seriesList, place, getUrlService(),
				getLocale());

		// Logging view count
		viewManagementService.logViewedOverview(place, user);
		return SUCCESS;
	}

	@Override
	public OverviewSupport getOverviewSupport() {
		return overviewSupport;
	}

	@Override
	public void setOverviewSupport(OverviewSupport overviewSupport) {
		this.overviewSupport = overviewSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}

	@Override
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
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
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public MapSupport getMapSupport() {
		return mapSupport;
	}

	@Override
	public void setMapSupport(MapSupport mapSupport) {
		this.mapSupport = mapSupport;
	}

	public void setEventsListSupport(ItemsListBoxSupport eventsListSupport) {
		this.eventsListSupport = eventsListSupport;
	}

	public ItemsListBoxSupport getEventsListSupport() {
		return eventsListSupport;
	}

	public FavoritesSupport getNearbySupport() {
		return nearbySupport;
	}

	public void setNearbySupport(FavoritesSupport nearbySupport) {
		this.nearbySupport = nearbySupport;
	}

	@Override
	public void setActivitySupport(ActivitySupport activitySupport) {
		this.activitySupport = activitySupport;
	}

	@Override
	public ActivitySupport getActivitySupport() {
		return activitySupport;
	}

	@Override
	public void setPropertiesSupport(PropertiesSupport propertiesSupport) {
		this.propertiesSupport = propertiesSupport;
	}

	@Override
	public PropertiesSupport getPropertiesSupport() {
		return propertiesSupport;
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
	public void setCommentSupport(CommentSupport commentSupport) {
		this.commentSupport = commentSupport;
	}

	@Override
	public CommentSupport getCommentSupport() {
		return commentSupport;
	}

	@Override
	public void setCommentTagSupport(SelectableTagSupport commentTagSupport) {
		this.commentTagSupport = commentTagSupport;
	}

	@Override
	public SelectableTagSupport getCommentTagSupport() {
		return commentTagSupport;
	}

	public void setLocalSearchSupport(SearchSupport localSearchSupport) {
		this.localSearchSupport = localSearchSupport;
	}

	public SearchSupport getLocalSearchSupport() {
		return localSearchSupport;
	}

	@Override
	public void setMosaicSupport(MosaicSupport mosaicSupport) {
		this.mosaicSupport = mosaicSupport;
	}

	@Override
	public MosaicSupport getMosaicSupport() {
		return mosaicSupport;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}

	@Override
	public void setPopularSupport(PopularSupport popularSupport) {
		this.popularSupport = popularSupport;
	}

	@Override
	public PopularSupport getPopularSupport() {
		return popularSupport;
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

	@Override
	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	@Override
	public RightsManagementService getRightsManagementService() {
		return rightsManagementService;
	}

	@Override
	public void setCalendarSupport(CalendarSupport calendarSupport) {
		this.calendarSupport = calendarSupport;
	}

	@Override
	public CalendarSupport getCalendarSupport() {
		return calendarSupport;
	}

	@Override
	public void setSecondaryPopularSupport(
			PopularSupport secondaryPopularSupport) {
		this.secondaryPopularSupport = secondaryPopularSupport;
	}

	@Override
	public PopularSupport getSecondaryPopularSupport() {
		return secondaryPopularSupport;
	}
}
