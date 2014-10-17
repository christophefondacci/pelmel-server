package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.nextep.activities.model.Activity;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.properties.model.MutableProperty;
import com.nextep.properties.model.Property;
import com.nextep.properties.model.impl.PropertyImpl;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.ActivityAware;
import com.nextep.proto.action.model.Commentable;
import com.nextep.proto.action.model.CurrentObjectAware;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MapAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.MosaicAware;
import com.nextep.proto.action.model.OverviewAware;
import com.nextep.proto.action.model.PropertiesAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.apis.model.impl.ApisCommentsHelper;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.blocks.CommentSupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.blocks.ItemsListBoxSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.MosaicSupport;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.blocks.PropertiesSupport;
import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.helpers.UserHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
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

public class UserOverviewAction extends AbstractAction implements
		LocalizationAware, MediaAware, OverviewAware, TagAware,
		CurrentUserAware, MessagingAware, CurrentObjectAware, ActivityAware,
		PropertiesAware, DescriptionAware, Commentable, MosaicAware, MapAware {

	// Constants declaration
	private static final long serialVersionUID = 1055359408041351626L;
	private static final String APIS_ALIAS_USER = "user";
	private static final String APIS_ALIAS_TAG_LIST = "tagList";
	private static final String APIS_ALIAS_USER_LIKE = "like";
	private static final SearchType searchType = SearchType.MEN;

	// Support for side bars (current user and local search)
	private CurrentUserSupport currentUserSupport;
	private MessagingSupport messagingSupport;

	// Support for overview-specifics
	private LocalizationSupport localizationSupport;
	private MediaProvider mediaProvider;
	private OverviewSupport userSupport;
	private TagSupport tagSupport;
	private FavoritesSupport favoritePlacesSupport;
	private ItemsListBoxSupport eventsListSupport;
	private FavoritesSupport favoriteUsersSupport;
	private ActivitySupport activitySupport;
	private PropertiesSupport propertiesSupport;
	private ItemsListBoxSupport descriptionSupport;
	private CommentSupport commentSupport;
	private SelectableTagSupport commentTagSupport;
	private MosaicSupport mosaicSupport;
	private MapSupport mapSupport;
	private ViewManagementService viewManagementService;

	// Dynamic action parameters
	private String id;
	private ItemKey userKey;

	@Override
	public String doExecute() throws Exception {
		final ApisCriterion currentUserCriterion = (ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false).with(
						SearchRestriction.with(User.class).aliasedBy(
								Constants.APIS_ALIAS_FAVORITE));
		userKey = CalmFactory.parseKey(id);
		final Collection<FacetCategory> userFacets = SearchHelper
				.buildUserFacetCategories();
		ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) currentUserCriterion
								.addCriterion(ApisCommentsHelper
										.getUserTagsFor(userKey)))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(userKey))
								.aliasedBy(APIS_ALIAS_USER)
								.with(Media.class)
								.addCriterion(ApisCommentsHelper.withComments())
								.with(Tag.class)
								.with(SearchRestriction
										.with(GeographicItem.class))
								.with((WithCriterion) SearchRestriction.with(
										Place.class).with(Media.class))
								.with((WithCriterion) SearchRestriction.with(
										User.class, Constants.MAX_FAVORITE_MEN,
										0).with(Media.class))
								.with(Property.class)
								.with(Description.class)
								.with((WithCriterion) SearchRestriction
										.withContained(User.class,
												SearchScope.CHILDREN,
												Constants.MAX_FAVORITE_MEN, 0)
										.facettedBy(userFacets)
										.aliasedBy(APIS_ALIAS_USER_LIKE)
										.with(Media.class))
								.with((WithCriterion) SearchRestriction.with(
										Event.class).with(Media.class))
								.with(ApisActivitiesHelper.withUserActivities(
										Constants.MAX_ACTIVITIES, 0)))
				.addCriterion(
						ApisCommentsHelper.listCommentTags(User.CAL_TYPE)
								.aliasedBy(APIS_ALIAS_TAG_LIST));

		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Overviewed user extraction and initialization
		final User user = response
				.getUniqueElement(User.class, APIS_ALIAS_USER);
		getHeaderSupport().initialize(getLocale(), user, null, null);

		// Current user extraction and initialization
		User currentUser = null;
		currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		final UrlService urlService = getUrlService();
		currentUserSupport.initialize(urlService, currentUser);

		messagingSupport.initialize(urlService, getLocale(),
				currentUser.get(Message.class),
				response.getPaginationInfo(Message.class),
				currentUser.getKey(), getHeaderSupport().getPageStyle());

		// Checking 404
		if (user == null) {
			return notFoundStatus();
		}
		final City city = user.getUnique(City.class);
		localizationSupport.initialize(searchType, urlService, getLocale(),
				city, null);
		final List<? extends Media> media = user.get(Media.class);
		mediaProvider.initialize(user.getKey(), media);

		// Initializing favorite places
		final List<? extends Place> places = user.get(Place.class);
		favoritePlacesSupport.initilialize(getUrlService(), getLocale(), user,
				places);
		// Initializing favorite events
		final List<? extends Event> events = user.get(Event.class);
		eventsListSupport
				.initialize(getUrlService(), getLocale(), user, events);
		// Initializing favorite users
		final List<? extends User> users = user.get(User.class);
		favoriteUsersSupport.initilialize(getUrlService(), getLocale(), user,
				users);

		// Getting like information
		final PaginationInfo likePagination = response
				.getPaginationInfo(APIS_ALIAS_USER_LIKE);
		userSupport.initialize(getUrlService(), getLocale(), user,
				likePagination.getItemCount(), 0, currentUser);

		// Search
		// final FacetInformation facetInfo = response
		// .getFacetInformation(SearchScope.CHILDREN);
		// final PaginationInfo likeUserPagination = response
		// .getPaginationInfo(APIS_ALIAS_USER_LIKE);
		// likeSearchSupport.initialize(searchType, getUrlService(),
		// getLocale(),
		// user, user.getPseudo(), facetInfo, likeUserPagination,
		// Collections.EMPTY_LIST);

		// Initializing activities
		final List<? extends Activity> activities = user.get(Activity.class);
		final PaginationInfo activitiesPagination = response
				.getPaginationInfo(Activity.class);
		activitySupport.initialize(getUrlService(), getLocale(),
				activitiesPagination, activities);
		activitySupport.initializeUser(user);

		// Initializing properties
		final List<Property> properties = new ArrayList<Property>(
				user.get(Property.class));
		final MutableProperty ageProp = new PropertyImpl();
		ageProp.setCode("age");
		final Integer age = UserHelper.getAge(user);
		if (age != null) {
			ageProp.setValue(String.valueOf(age));
			properties.add(ageProp);
		}
		final MutableProperty heightProp = new PropertyImpl();
		heightProp.setCode("height");
		heightProp.setValue(DisplayHelper.getHeight(getMessageSource(),
				user.getHeightInCm(), getLocale()));
		properties.add(heightProp);

		final MutableProperty weightProp = new PropertyImpl();
		weightProp.setCode("weight");
		weightProp.setValue(DisplayHelper.getWeight(getMessageSource(),
				user.getWeightInKg(), getLocale()));
		properties.add(weightProp);

		propertiesSupport.initialize(getLocale(), properties, true);
		descriptionSupport.initialize(getUrlService(), getLocale(), user,
				user.get(Description.class));
		// Initializing comments
		final List<? extends Comment> comments = user.get(Comment.class);
		final PaginationInfo commentsPagination = response
				.getPaginationInfo(Comment.class);
		commentSupport.initialize(getUrlService(), getLocale(), comments,
				commentsPagination, currentUser, user.getKey());
		final List<Tag> availableTags = (List<Tag>) response.getElements(
				Tag.class, APIS_ALIAS_TAG_LIST);
		commentTagSupport.initialize(getLocale(), availableTags);
		commentTagSupport.initializeSelection(getUrlService(), userKey,
				currentUser);

		final List<? extends User> likers = user.get(User.class,
				APIS_ALIAS_USER_LIKE);
		mosaicSupport.initialize(getUrlService(), getLocale(), user,
				activitySupport, likers, users, activities, null, events,
				places);

		Localized l;
		final List<CalmObject> points = new ArrayList<CalmObject>(1);
		if (user.getLatitude() == 0 && user.getLongitude() == 0) {
			l = city;
		} else {
			l = user;
			points.add(user);
		}
		mapSupport.initialize(l, points);

		// Logging user view count
		viewManagementService.logViewedOverview(user, currentUser);

		return SUCCESS;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setOverviewSupport(OverviewSupport userSupport) {
		this.userSupport = userSupport;
	}

	@Override
	public OverviewSupport getOverviewSupport() {
		return userSupport;
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
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
	}

	// @Override
	// public void setSearchSupport(SearchSupport searchSupport) {
	// this.searchSupport = searchSupport;
	// }
	//
	// @Override
	// public SearchSupport getSearchSupport() {
	// return searchSupport;
	// }
	//
	// public SearchSupport getPlaceSearchSupport() {
	// return placeSearchSupport;
	// }
	//
	// public void setPlaceSearchSupport(SearchSupport placeSearchSupport) {
	// this.placeSearchSupport = placeSearchSupport;
	// }

	// @Override
	// public TabbedSearchSupport getTabbedSearchSupport() {
	// return null;
	// }
	//
	// @Override
	// public void setTabbedSearchSupport(TabbedSearchSupport
	// tabbedSearchSupport) {
	//
	// }

	public void setFavoritePlacesSupport(FavoritesSupport favoritePlacesSupport) {
		this.favoritePlacesSupport = favoritePlacesSupport;
	}

	public FavoritesSupport getFavoritePlacesSupport() {
		return favoritePlacesSupport;
	}

	public void setEventsListSupport(ItemsListBoxSupport eventsListSupport) {
		this.eventsListSupport = eventsListSupport;
	}

	public ItemsListBoxSupport getEventsListSupport() {
		return eventsListSupport;
	}

	@Override
	public ItemKey getCurrentObjectKey() {
		return userKey;
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
	public PropertiesSupport getPropertiesSupport() {
		return propertiesSupport;
	}

	@Override
	public void setPropertiesSupport(PropertiesSupport propertiesSupport) {
		this.propertiesSupport = propertiesSupport;
	}

	@Override
	public void setDescriptionSupport(ItemsListBoxSupport descriptionsSupport) {
		this.descriptionSupport = descriptionsSupport;
	}

	@Override
	public ItemsListBoxSupport getDescriptionSupport() {
		return descriptionSupport;
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

	public void setFavoriteUsersSupport(FavoritesSupport favoriteUsersSupport) {
		this.favoriteUsersSupport = favoriteUsersSupport;
	}

	public FavoritesSupport getFavoriteUsersSupport() {
		return favoriteUsersSupport;
	}

	@Override
	public void setMosaicSupport(MosaicSupport mosaicSupport) {
		this.mosaicSupport = mosaicSupport;
	}

	@Override
	public MosaicSupport getMosaicSupport() {
		return mosaicSupport;
	}

	@Override
	public void setMapSupport(MapSupport mapSupport) {
		this.mapSupport = mapSupport;
	}

	@Override
	public MapSupport getMapSupport() {
		return mapSupport;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}
}
