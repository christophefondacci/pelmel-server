package com.nextep.proto.action.impl.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventRequestTypes;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.apis.adapters.ApisEventAuthorKeyAdapter;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.impl.AdminPaginationSupport;
import com.nextep.proto.blocks.impl.HeaderAdminSupportImpl;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;

public class AdminEventsAction extends AbstractAction implements MediaAware,
		CurrentUserAware {

	private static final long serialVersionUID = 5199841368119304685L;

	private static final String APIS_ALIAS_EVENTS = "e";
	private static final String URL = "/admin/events";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private static final ApisItemKeyAdapter eventAuthorAdapter = new ApisEventAuthorKeyAdapter();

	// Injected supports
	@Autowired
	@Qualifier("mediaProvider")
	private MediaProvider mediaProvider;
	@Autowired
	@Qualifier("eventSearchSupport")
	private SearchSupport searchSupport;
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private RightsManagementService rightsManagementService;
	@Autowired
	@Qualifier("adminPaginationSupport")
	private AdminPaginationSupport paginationSupport;
	@Autowired
	private EventManagementService eventManagementService;

	// Dynamic arguments
	private int page = 0;
	private int pageSize = 30;
	private boolean allEvents = true;

	// Internal
	private List<? extends Event> events;
	private DateFormat dateFormatter;

	@Override
	protected String doExecute() throws Exception {

		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.list(Event.class,
										allEvents ? EventRequestTypes.ALL_EVENTS
												: null)
								.paginatedBy(pageSize, page)
								.aliasedBy(APIS_ALIAS_EVENTS)
								.with(Media.class)
								.addCriterion(
										SearchRestriction
												.adaptKey(eventLocationAdapter))
								.addCriterion(
										SearchRestriction
												.adaptKey(eventAuthorAdapter)))
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);
		currentUserSupport.initialize(getUrlService(), user);
		if (!rightsManagementService.isAdministrator(user)) {
			return unauthorized();
		}

		events = response.getElements(Event.class, APIS_ALIAS_EVENTS);
		searchSupport.initialize(SearchType.EVENTS, getUrlService(),
				getLocale(), null, null, null, null, events);
		final PaginationInfo paginationInfo = response
				.getPaginationInfo(APIS_ALIAS_EVENTS);

		// Preparing args map
		Map<String, String> args = new HashMap<String, String>();

		paginationSupport.initialize(paginationInfo, getHeaderSupport()
				.getCanonical(), args);
		((HeaderAdminSupportImpl) getHeaderSupport()).setCanonical(URL);

		dateFormatter = new SimpleDateFormat("EEEE, MMMM dd, HH:mm",
				getLocale());
		return SUCCESS;
	}

	public String getSearchUrl(Place place) {
		final SearchType searchType = SearchType.fromPlaceType(place
				.getPlaceType());
		return getUrlService().buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), place.getCity(),
				searchType);
	}

	public List<? extends Event> getEvents() {
		return events;
	}

	public String getLocalizedDate(Event event, Date date) throws CalException {
		// Getting event timezone
		final String eventTimezone = eventManagementService
				.getEventTimezoneId(event);
		final Date localizedDate = eventManagementService.convertDate(date,
				eventTimezone, TimeZone.getDefault().getID());

		return dateFormatter.format(localizedDate);
	}

	public String getAuthor(Event event) throws CalException {
		final User user = event.getUnique(User.class);
		if (user != null) {
			return DisplayHelper.getName(user);
		}
		return "";
	}

	public String getAuthorUrl(Event event) throws CalException {
		final User user = event.getUnique(User.class);
		if (user != null) {
			return getUrlService().getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), user);
		} else {
			return null;
		}
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
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public AdminPaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	public void setAllEvents(boolean allEvents) {
		this.allEvents = allEvents;
	}

	public boolean isAllEvents() {
		return allEvents;
	}
}
