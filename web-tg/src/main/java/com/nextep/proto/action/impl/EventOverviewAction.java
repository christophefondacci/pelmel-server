package com.nextep.proto.action.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.nextep.activities.model.Activity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.properties.model.MutableProperty;
import com.nextep.properties.model.Property;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.ActivityAware;
import com.nextep.proto.action.model.Commentable;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.MapAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.action.model.MosaicAware;
import com.nextep.proto.action.model.OverviewAware;
import com.nextep.proto.action.model.PropertiesAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisEventSeriesAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.apis.adapters.ApisPlaceLocationAdapter;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.apis.model.impl.ApisCommentsHelper;
import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.blocks.CommentSupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.ItemsListBoxSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.MosaicSupport;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.blocks.PropertiesSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.ViewManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
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
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class EventOverviewAction extends AbstractAction implements
		OverviewAware, CurrentUserAware, TagAware, MessagingAware, MediaAware,
		LocalizationAware, SearchAware, MapAware, ActivityAware,
		DescriptionAware, Commentable, PropertiesAware, MosaicAware {

	private static final long serialVersionUID = 7264474769239357981L;
	private static final String APIS_ALIAS_TAG_LIST = "taglist";
	private static final String APIS_ALIAS_EVENT_TAGS = "eventTags";

	private static final ApisItemKeyAdapter eventSeriesAdapter = new ApisEventSeriesAdapter();
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private static final ApisCalmObjectAdapter placeLocationAdapter = new ApisPlaceLocationAdapter();
	private static final ApisCustomAdapter facetAdapter = new ApisFacetToItemKeyAdapter(
			SearchScope.CHILDREN, SearchHelper.getTagFacetCategory(),
			Constants.MAX_EVENT_TAGS);

	private ApisCriterionFactory apisCriterionFactory;
	private OverviewSupport overviewSupport;
	private CurrentUserSupport currentUserSupport;
	private SelectableTagSupport tagSupport;
	private MessagingSupport messagingSupport;
	private MediaProvider mediaProvider;
	private LocalizationSupport localizationSupport;
	private SearchSupport searchSupport;
	private MapSupport mapSupport;
	private ActivitySupport activitySupport;
	private ItemsListBoxSupport descriptionSupport;
	private ItemsListBoxSupport eventsListSupport;
	private CommentSupport commentSupport;
	private SelectableTagSupport commentTagSupport;
	private PropertiesSupport propertiesSupport;
	private CalPersistenceService propertiesService;
	private MosaicSupport mosaicSupport;
	private ViewManagementService viewManagementService;
	@Autowired
	private EventManagementService eventManagementService;
	private String id;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey eventKey = CalmFactory.parseKey(id);

		final ApisRequest request = ApisFactory.createCompositeRequest();
		request.addCriterion((ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false)
				.with(SearchRestriction.with(Event.class).aliasedBy(
						Constants.APIS_ALIAS_FAVORITE))
				.addCriterion(ApisCommentsHelper.getCommentsFor(eventKey, 0)));

		// Building user facetting
		final Collection<FacetCategory> facetCategories = SearchHelper
				.buildUserFacetCategories();
		request.addCriterion((ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(eventKey))
				.aliasedBy(Constants.APIS_ALIAS_EVENT)
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.adaptKey(eventLocationAdapter)
								.with(Media.class)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adapt(placeLocationAdapter)
												.withContained(
														Event.class,
														SearchScope.CHILDREN,
														Constants.EVENTS_OTHER_COUNT,
														0)))
				.with(Media.class)
				.with(Tag.class)
				.with(Description.class)
				.addCriterion(ApisCommentsHelper.withComments())
				.addCriterion(SearchRestriction.adaptKey(eventSeriesAdapter))
				.with((WithCriterion) SearchRestriction
						.withContained(User.class, SearchScope.CHILDREN,
								Constants.MAX_EVENT_USERS, 0)
						.facettedBy(facetCategories)
						.customAdapt(facetAdapter, APIS_ALIAS_EVENT_TAGS)
						.with(Media.class))
				.with(ApisActivitiesHelper.withActivities(
						Constants.MAX_ACTIVITIES, 0)));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Event initialization
		final Event event = response.getUniqueElement(Event.class,
				Constants.APIS_ALIAS_EVENT);
		getHeaderSupport().initialize(getLocale(), event, null, null);
		// Checking 404
		if (event == null) {
			return notFoundStatus();
		}

		// Current user initialization
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		try {
			checkCurrentUser(currentUser);
			currentUserSupport.initialize(getUrlService(), currentUser);
			// Messaging support initialization
			messagingSupport.initialize(getUrlService(), getLocale(),
					currentUser.get(Message.class),
					response.getPaginationInfo(Message.class),
					currentUser.getKey(), getHeaderSupport().getPageStyle());
		} catch (UserLoginTimeoutException e) {
			// Non-blocking logging page
		}

		// Getting like information
		final PaginationInfo likePagination = response
				.getPaginationInfo(User.class);
		overviewSupport.initialize(getUrlService(), getLocale(), event,
				likePagination.getItemCount(), 0, currentUser);

		// Localization initialization
		// -- Trying to extract place
		final GeographicItem geoItem = event.getUnique(GeographicItem.class);
		localizationSupport.initialize(SearchType.EVENTS, getUrlService(),
				getLocale(), geoItem,
				response.getFacetInformation(SearchScope.CHILDREN));

		// Media provider initialization
		final List<? extends Media> media = event.get(Media.class);
		final List<? extends Media> placeMedia = geoItem.get(Media.class);
		final List<Media> allMedia = new ArrayList<Media>();
		allMedia.addAll(media);
		allMedia.addAll(placeMedia);
		mediaProvider.initialize(event.getKey(), allMedia);

		// Search features enablement
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		searchSupport.initialize(SearchType.EVENTS, getUrlService(),
				getLocale(), event, DisplayHelper.getName(event), facetInfo,
				response.getPaginationInfo(User.class), Collections.EMPTY_LIST);

		// Map enablement
		mapSupport.initialize((Localized) geoItem, Arrays.asList(geoItem));

		// Activities enablement
		final List<? extends Activity> activities = event.get(Activity.class);
		final PaginationInfo activitiesPagination = response
				.getPaginationInfo(Activity.class);
		activitySupport.initialize(getUrlService(), getLocale(),
				activitiesPagination, activities);
		activitySupport.initializeTarget(event);

		// Description enablement
		final List<? extends Description> descriptions = event
				.get(Description.class);
		descriptionSupport.initialize(getUrlService(), getLocale(), event,
				descriptions);

		// Comments enablement
		final List<? extends Comment> comments = event.get(Comment.class);
		final PaginationInfo commentsPagination = response
				.getPaginationInfo(Comment.class);
		commentSupport.initialize(getUrlService(), getLocale(), comments,
				commentsPagination, currentUser, eventKey);

		// Tag support
		final List<? extends Tag> eventTags = response.getElements(Tag.class,
				APIS_ALIAS_EVENT_TAGS);
		event.addAll(eventTags);
		tagSupport.initialize(getLocale(), (List<Tag>) eventTags);
		tagSupport.initializeSelection(getUrlService(), eventKey, event);

		// Events participants
		final List<? extends User> users = event.get(User.class);
		mosaicSupport.initialize(getUrlService(), getLocale(), event,
				activitySupport, users, Collections.EMPTY_LIST,
				Collections.EMPTY_LIST, Collections.EMPTY_LIST);

		// Initializing other events in city
		final GeographicItem city = geoItem.getUnique(GeographicItem.class);
		final List<? extends Event> otherEvents = city.get(Event.class);
		eventsListSupport.initialize(getUrlService(), getLocale(), geoItem,
				otherEvents);

		// Being precaution, not sure we always have a City here and not an
		// Admin zone
		String timezone = TimeZone.getDefault().getID();
		if (city instanceof City) {
			timezone = ((City) city).getTimezoneId();
		}
		// Initializing properties
		final DateFormat dateFormat = new SimpleDateFormat(
				"EEEE dd MMMM HH:mm", getLocale());
		final List<Property> props = new ArrayList<Property>();
		if (event.getStartDate() != null) {
			final MutableProperty p = (MutableProperty) propertiesService
					.createTransientObject();
			p.setCode(Constants.PROPERTY_EVENT_START);

			final Date localizedStart = eventManagementService.convertDate(
					event.getStartDate(), TimeZone.getDefault().getID(),
					timezone);
			p.setValue(dateFormat.format(localizedStart));
			props.add(p);
		}

		if (event.getEndDate() != null) {
			final MutableProperty p = (MutableProperty) propertiesService
					.createTransientObject();
			p.setCode(Constants.PROPERTY_EVENT_END);
			final Date localizedEnd = eventManagementService
					.convertDate(event.getEndDate(), TimeZone.getDefault()
							.getID(), timezone);
			p.setValue(dateFormat.format(localizedEnd));
			props.add(p);
		}

		if (EventSeries.SERIES_CAL_ID.equals(event.getKey().getType())) {
			final MutableProperty p = (MutableProperty) propertiesService
					.createTransientObject();
			p.setCode(Constants.PROPERTY_EVENT_FREQ);
			final EventSeries series = (EventSeries) event;
			String freqKey = null;
			if (series.getWeekOfMonthOffset() == null) {
				freqKey = "event.form.monthevery";
			} else if (series.getWeekOfMonthOffset() == 1) {
				freqKey = "event.form.monthfirst";
			} else if (series.getWeekOfMonthOffset() == 2) {
				freqKey = "event.form.monthsecond";
			} else if (series.getWeekOfMonthOffset() == 3) {
				freqKey = "event.form.monththird";
			} else if (series.getWeekOfMonthOffset() == 4) {
				freqKey = "event.form.monthfourth";
			} else if (series.getWeekOfMonthOffset() == -1) {
				freqKey = "event.form.monthlast";
			}
			final MessageSource m = getMessageSource();
			final Locale l = getLocale();
			final String freqStr = m.getMessage(freqKey, null, l);
			String separator = "";
			StringBuilder daysBuf = new StringBuilder();
			if (series.isMonday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.monday", null, l));
				separator = ", ";
			}
			if (series.isTuesday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.tuesday", null, l));
				separator = ", ";
			}
			if (series.isWednesday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.wednesday", null, l));
				separator = ", ";
			}
			if (series.isThursday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.thursday", null, l));
				separator = ", ";
			}
			if (series.isFriday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.friday", null, l));
				separator = ", ";
			}
			if (series.isSaturday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.saturday", null, l));
				separator = ", ";
			}
			if (series.isSunday()) {
				daysBuf.append(separator);
				daysBuf.append(m.getMessage("event.form.sunday", null, l));
				separator = ", ";
			}
			final DateFormat df = new SimpleDateFormat("HH:mm");
			String startHour = "";
			String endHour = "";
			if (series.getStartDate() != null && series.getEndDate() != null) {
				startHour = df.format(series.getStartDate());
				endHour = df.format(series.getEndDate());
			} else {
				startHour = String.valueOf(series.getStartHour());
				endHour = String.valueOf(series.getEndHour());
			}
			p.setValue(m.getMessage("event.recurring.sentence", new String[] {
					freqStr, daysBuf.toString(), startHour, endHour }, l));
			props.add(p);
		}
		// Initializing properties
		propertiesSupport.initialize(getLocale(), props, false);

		// Initializing view count
		viewManagementService.logViewedOverview(event, currentUser);
		return SUCCESS;
	}

	@Override
	public void setOverviewSupport(OverviewSupport overviewSupport) {
		this.overviewSupport = overviewSupport;
	}

	@Override
	public OverviewSupport getOverviewSupport() {
		return overviewSupport;
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
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = (SelectableTagSupport) tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
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
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	public void setApisCriterionFactory(
			ApisCriterionFactory apisCriterionFactory) {
		this.apisCriterionFactory = apisCriterionFactory;
	}

	@Override
	public MapSupport getMapSupport() {
		return mapSupport;
	}

	@Override
	public void setMapSupport(MapSupport mapSupport) {
		this.mapSupport = mapSupport;
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

	@Override
	public void setPropertiesSupport(PropertiesSupport propertiesSupport) {
		this.propertiesSupport = propertiesSupport;
	}

	@Override
	public PropertiesSupport getPropertiesSupport() {
		return propertiesSupport;
	}

	public void setPropertiesService(CalPersistenceService propertiesService) {
		this.propertiesService = propertiesService;
	}

	@Override
	public SearchType getSearchType() {
		return null;
	}

	@Override
	public void setSearchType(SearchType searchType) {

	}

	@Override
	public void setMosaicSupport(MosaicSupport mosaicSupport) {
		this.mosaicSupport = mosaicSupport;
	}

	@Override
	public MosaicSupport getMosaicSupport() {
		return mosaicSupport;
	}

	public void setEventsListSupport(ItemsListBoxSupport eventsListSupport) {
		this.eventsListSupport = eventsListSupport;
	}

	public ItemsListBoxSupport getEventsListSupport() {
		return eventsListSupport;
	}

	public void setViewManagementService(
			ViewManagementService viewManagementService) {
		this.viewManagementService = viewManagementService;
	}
}
