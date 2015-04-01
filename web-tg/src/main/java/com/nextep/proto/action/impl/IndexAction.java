package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.NoSuchMessageException;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.ActivityAware;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.FooterAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MapAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisActivityExtraKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityTargetKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.impl.TagTypeRequestType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.unit.model.UnitSystem;
import com.videopolis.unit.service.DistanceService;

public class IndexAction extends AbstractAction implements TagAware,
		LocalizationAware, CurrentUserAware, MessagingAware, SearchAware,
		ActivityAware, MapAware, FooterAware {
	private static final long serialVersionUID = 5751164176193689904L;
	private static final String KEY_DEFAULT_ERROR_MESSAGE = "error.default";
	private static final String APIS_ALIAS_TAGS = "tags";
	private static final String APIS_ALIAS_CITIES = "cities";
	private static final String APIS_ALIAS_PLACE_FACETS = "facets";
	private static final String APIS_ALIAS_COUNTRIES = "countries";
	private static final String APIS_ALIAS_RECENT_CHANGES = "recent";
	private static final String APIS_ALIAS_USER_ACTIVITY = "userActivity";
	private static final String APIS_ALIAS_TOP_PLACES = "topPlaces";
	private static final String APIS_ALIAS_TOP_USERS = "topUsers";
	private static final RequestType USER_TAGS_REQUEST_TYPE = new TagTypeRequestType(
			User.CAL_TYPE);
	private static final ApisCustomAdapter cityFacetAdapter = new ApisFacetToItemKeyAdapter(
			SearchScope.CHILDREN, SearchHelper.getCityFacetCategory(),
			Constants.MAX_TOP_CITIES);
	private static final ApisCustomAdapter countryFacetAdapter = new ApisFacetToItemKeyAdapter(
			SearchScope.CHILDREN, SearchHelper.getCountryFacetCategory(),
			Constants.MAX_TOP_COUNTRIES);
	private static ApisCustomAdapter userFacetAdapter;
	private static final ApisItemKeyAdapter activityTargetKeyAdapter = new ApisActivityTargetKeyAdapter();
	private static final ApisItemKeyAdapter activityUserKeyAdapter = new ApisActivityUserAdapter();
	private static final ApisItemKeyAdapter activityExtraKeyAdapter = new ApisActivityExtraKeyAdapter();

	private DistanceService distanceService;
	private PopularSupport citiesPopularSupport;
	private PopularSupport countriesPopularSupport;
	private PopularSupport latestChangesPopularSupport;
	private SearchSupport searchSupport;
	private LocalizationSupport localizationSupport;
	private MessagingSupport messagingSupport;
	private String loginErrorMessage;
	private CurrentUserSupport currentUserSupport;
	private ActivitySupport activitySupport;
	private MapSupport mapSupport;
	private FavoritesSupport favoritePlacesSupport;
	private FavoritesSupport favoriteUsersSupport;
	private RightsManagementService rightsManagementService;

	private int maxLastChangesCount = Constants.MAX_ACTIVITIES_HOMEPAGE_FETCH;
	private int maxUserActivities = 20;
	private int maxTopUsersSecurity = 5;
	private List<Tag> tags;
	private TagSupport tagSupport;
	private String url;
	private String queryParams;
	private String email;
	private String searchTerm;

	@SuppressWarnings("unchecked")
	@Override
	public String doExecute() throws Exception {
		getLoginSupport().initialize(getLocale(), getUrlService(),
				getHeaderSupport(), url);
		// Initializing adapter with custom injected values
		userFacetAdapter = new ApisFacetToItemKeyAdapter(SearchScope.USERS,
				SearchHelper.getUserFacetCategory(), Constants.MAX_TOP_USERS
						+ maxTopUsersSecurity);
		// We force redirection as we should never land on current page after
		// successful login
		getLoginSupport().setForceRedirect(true);
		final FacetCategory cityCategory = SearchHelper.getCityFacetCategory();
		final FacetCategory countryCategory = SearchHelper
				.getCountryFacetCategory();
		final FacetCategory tagsCategory = SearchHelper.getTagFacetCategory();
		final FacetCategory amenitiesCategory = SearchHelper
				.getAmenitiesFacetCategory();

		ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false))
				.addCriterion(
						SearchRestriction.list(Tag.class,
								USER_TAGS_REQUEST_TYPE).aliasedBy(
								APIS_ALIAS_TAGS))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.withContained(Place.class,
										SearchScope.CHILDREN, 10, 0)
								.facettedBy(
										Arrays.asList(cityCategory,
												countryCategory, tagsCategory,
												amenitiesCategory))
								.addCriterion(
										SearchRestriction.customAdapt(
												cityFacetAdapter,
												APIS_ALIAS_CITIES))
								.addCriterion(
										SearchRestriction.customAdapt(
												countryFacetAdapter,
												APIS_ALIAS_COUNTRIES)))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.list(Activity.class,
										new RequestTypeLatestActivities(
												maxLastChangesCount,
												ActivityType.CREATION,
												ActivityType.UPDATE,
												ActivityType.COMMENT,
												ActivityType.SEO_OPEN))
								.aliasedBy(APIS_ALIAS_RECENT_CHANGES)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														activityTargetKeyAdapter)
												.with(Media.class)))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.list(Activity.class,
										new RequestTypeLatestActivities(true,
												maxUserActivities,
												ActivityType.LIKE,
												ActivityType.UNLIKE,
												ActivityType.COMMENT,
												ActivityType.REGISTER,
												ActivityType.REMOVAL_REQUESTED))
								.aliasedBy(APIS_ALIAS_USER_ACTIVITY)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														activityUserKeyAdapter)
												.aliasedBy(
														Constants.ALIAS_ACTIVITY_USER)
												.with(Media.class))
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														activityTargetKeyAdapter)
												.aliasedBy(
														Constants.ALIAS_ACTIVITY_TARGET)
												.with(Media.class))
								.addCriterion(
										SearchRestriction
												.adaptKey(
														activityExtraKeyAdapter)
												.aliasedBy(
														Constants.ALIAS_ACTIVITY_OBJECT)))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.searchAll(Place.class, SearchScope.PLACES, 3,
										0)
								.sortBy(SearchHelper.getPlaceRatingSorter())
								.aliasedBy(APIS_ALIAS_TOP_PLACES)
								.with(Media.class))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.searchAll(Activity.class, SearchScope.USERS,
										5, 0)
								.facettedBy(
										Arrays.asList(SearchHelper
												.getUserFacetCategory()))
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.customAdapt(userFacetAdapter,
														APIS_ALIAS_TOP_USERS)
												.with(Media.class)));

		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		currentUserSupport.initialize(getUrlService(), user);
		try {
			checkCurrentUser(user);
		} catch (UserLoginTimeoutException e) {

		}
		// Initializing tags (for registration form)
		tags = (List<Tag>) response.getElements(Tag.class, APIS_ALIAS_TAGS);
		tagSupport.initialize(getLocale(), tags);

		// Initializing cities popular support
		final List<? extends City> popularCities = response.getElements(
				City.class, APIS_ALIAS_CITIES);
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		citiesPopularSupport.initialize(SearchType.BARS, getLocale(),
				getUrlService(), null, popularCities,
				facetInfo.getFacetCounts(cityCategory));

		// Initializing countries popular support
		final List<? extends Country> popularCountries = response.getElements(
				Country.class, APIS_ALIAS_COUNTRIES);
		countriesPopularSupport.initialize(SearchType.BARS, getLocale(),
				getUrlService(), null, popularCountries,
				facetInfo.getFacetCounts(countryCategory));

		// Initializing header support
		getHeaderSupport().initialize(getLocale(), null, null, null);
		localizationSupport.initialize(SearchType.BARS, getUrlService(),
				getLocale(), null, null);

		// Initializing messaging
		if (user != null) {
			messagingSupport.initialize(getUrlService(), getLocale(),
					user.get(Message.class),
					response.getPaginationInfo(Message.class), user.getKey(),
					getHeaderSupport().getPageStyle());
		}

		// Initializing latest activities
		List<? extends Activity> latestActivities = response.getElements(
				Activity.class, APIS_ALIAS_RECENT_CHANGES);

		// if (latestActivities.size() > 36) {
		// latestActivities = latestActivities.subList(0, 36);
		// }
		latestChangesPopularSupport.initialize(null, getLocale(),
				getUrlService(), null, latestActivities, null);

		// Initializing facets
		final PaginationInfo paginationInfo = response
				.getPaginationInfo(Place.class);
		final List<? extends Place> places = Collections.emptyList();
		searchSupport.initialize(getSearchType(), getUrlService(), getLocale(),
				null, "World", facetInfo, paginationInfo, places);

		// Initializing activity support
		final List<? extends Activity> activities = response.getElements(
				Activity.class, APIS_ALIAS_USER_ACTIVITY);
		final PaginationInfo activitiesPagination = response
				.getPaginationInfo(APIS_ALIAS_USER_ACTIVITY);
		// Filtering out user to user likes
		for (Activity a : new ArrayList<Activity>(activities)) {
			if (a.getActivityType() == ActivityType.LIKE
					|| a.getActivityType() == ActivityType.UNLIKE) {
				if (a.getLoggedItemKey().getType().equals(User.CAL_TYPE)) {
					activities.remove(a);
				}
			}
		}
		activitySupport.initialize(getUrlService(), getLocale(),
				activitiesPagination, activities);

		final List<? extends Place> hotPlaces = response.getElements(
				Place.class, APIS_ALIAS_TOP_PLACES);
		favoritePlacesSupport.initilialize(getUrlService(), getLocale(), null,
				hotPlaces);

		// Extracting top users
		final List<? extends User> hotUsers = response.getElements(User.class,
				APIS_ALIAS_TOP_USERS);
		// Filtering to remove any admin
		final List<User> filteredHotUsers = new ArrayList<User>(
				Constants.MAX_TOP_USERS);
		for (User u : hotUsers) {
			if (!rightsManagementService.isAdministrator(u)
					&& filteredHotUsers.size() < Constants.MAX_TOP_USERS) {
				filteredHotUsers.add(u);
			}
		}
		favoriteUsersSupport.initilialize(getUrlService(), getLocale(), null,
				filteredHotUsers);

		for (Activity a : latestActivities) {
			try {
				final CalmObject target = a.getUnique(CalmObject.class);
				a.addAll(Constants.ALIAS_ACTIVITY_TARGET, Arrays.asList(target));
			} catch (Exception e) {
				LOG.warn("Conversion failed: " + e.getMessage(), e);
			}
		}
		mapSupport.initialize(null, latestActivities);
		return SUCCESS;
	}

	@Override
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}

	public List<? extends UnitSystem> listUnitSystems() {
		return distanceService.listUnitSystems();
	}

	public void setDistanceService(DistanceService distanceService) {
		this.distanceService = distanceService;
	}

	public void setLoginErrorMessage(String loginErrorMessage) {
		this.loginErrorMessage = loginErrorMessage;
	}

	public String getLoginErrorMessage() {
		return loginErrorMessage;
	}

	public String getLoginErrorMessageLabel() {
		if (loginErrorMessage != null) {
			try {
				return getMessageSource().getMessage(loginErrorMessage, null,
						getLocale());
			} catch (NoSuchMessageException e) {
				return getMessageSource().getMessage(KEY_DEFAULT_ERROR_MESSAGE,
						null, getLocale());
			}
		} else {
			return null;
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setQueryParams(String queryParams) {
		this.queryParams = queryParams;
	}

	@Override
	public String getQueryParams() {
		return queryParams;
	}

	public void setEmail(String userEmail) {
		this.email = userEmail;
	}

	public String getEmail() {
		return email;
	}

	public PopularSupport getLatestChangesPopularSupport() {
		return latestChangesPopularSupport;
	}

	public void setLatestChangesPopularSupport(
			PopularSupport latestChangesPopularSupport) {
		this.latestChangesPopularSupport = latestChangesPopularSupport;
	}

	@Override
	public void setSecondaryPopularSupport(
			PopularSupport countriesPopularSupport) {
		this.countriesPopularSupport = countriesPopularSupport;
	}

	@Override
	public PopularSupport getSecondaryPopularSupport() {
		return countriesPopularSupport;
	}

	public String getHomePageSentence() {
		int random = (int) (Math.random() * 4d) + 1;
		return getMessageSource().getMessage("homepage.sentence." + random,
				null, getLocale());
	}

	public boolean isHomePage() {
		return true;
	}

	@Override
	public PopularSupport getPopularSupport() {
		return citiesPopularSupport;
	}

	@Override
	public void setPopularSupport(PopularSupport support) {
		this.citiesPopularSupport = support;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setMaxLastChangesCount(int maxLastChangesCount) {
		this.maxLastChangesCount = maxLastChangesCount;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
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
	public SearchType getSearchType() {
		return SearchType.BARS;
	}

	@Override
	public void setSearchType(SearchType searchType) {

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
	public void setMapSupport(MapSupport mapSupport) {
		this.mapSupport = mapSupport;
	}

	@Override
	public MapSupport getMapSupport() {
		return mapSupport;
	}

	public FavoritesSupport getFavoritePlacesSupport() {
		return favoritePlacesSupport;
	}

	public void setFavoritePlacesSupport(FavoritesSupport favoritePlacesSupport) {
		this.favoritePlacesSupport = favoritePlacesSupport;
	}

	public FavoritesSupport getFavoriteUsersSupport() {
		return favoriteUsersSupport;
	}

	public void setFavoriteUsersSupport(FavoritesSupport favoriteUsersSupport) {
		this.favoriteUsersSupport = favoriteUsersSupport;
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

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setMaxTopUsersSecurity(int maxTopUsersSecurity) {
		this.maxTopUsersSecurity = maxTopUsersSecurity;
	}
}
