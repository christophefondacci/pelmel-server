package com.nextep.proto.action.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.MutableEvent;
import com.nextep.events.model.MutableEventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonHour;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.DescriptionsUpdateAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.model.ActivityConstants;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.services.PuffService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.CalContext;
import com.videopolis.smaug.common.model.SearchScope;

public class EventUpdateAction extends AbstractAction implements
		DescriptionsUpdateAware, JsonProvider {

	private static final long serialVersionUID = 381306118736563049L;
	private static final Log LOGGER = LogFactory
			.getLog(EventUpdateAction.class);
	private static final String ACTION_RESULT_PLACE = "placeRedirect";

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm");
	private static final DateFormat puffDateFormat = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	private static final String PUFF_FIELD_NAME = "evt_name";
	private static final String PUFF_FIELD_PLACE = "evt_place";
	private static final String PUFF_FIELD_DESCRIPTION = "evt_description";
	private static final String PUFF_FIELD_START = "evt_start";
	private static final String PUFF_FIELD_END = "evt_end";
	private static final String PUFF_FIELD_WEEK_OFFSET = "evt_week_offset";
	private static final String PUFF_FIELD_MONDAY = "evt_monday";
	private static final String PUFF_FIELD_TUESDAY = "evt_tuesday";
	private static final String PUFF_FIELD_WEDNESDAY = "evt_wednesday";
	private static final String PUFF_FIELD_THURSDAY = "evt_thursday";
	private static final String PUFF_FIELD_FRIDAY = "evt_friday";
	private static final String PUFF_FIELD_SATURDAY = "evt_saturday";
	private static final String PUFF_FIELD_SUNDAY = "evt_sunday";
	private static final String APIS_ALIAS_EVENT = "event";

	private ApisCriterionFactory apisUserCriterionFactory;
	private ApisCriterionFactory apisCriterionFactory;
	private CalPersistenceService eventService;
	private CalPersistenceService eventSeriesService;
	private CalPersistenceService activitiesService;
	private CalPersistenceService geoService;
	private SearchPersistenceService searchService;
	private PuffService puffService;
	private EventManagementService eventManagementService;
	@Autowired
	private JsonBuilder jsonBuilder;
	@Autowired
	private NotificationService notificationService;

	private String eventId;
	private String name;
	private String startDate, startHour, startMinute;
	private String endDate, endHour, endMinute;
	private Integer monthRecurrency;
	private boolean monday, tuesday, wednesday, thursday, friday, saturday,
			sunday;
	private String placeId;
	private String[] languages, keys, descriptions, descriptionSourceId;
	private DescriptionsManagementService descriptionsManagementService;

	private String newEventId;
	private String eventPlaceId;
	private String calendarType;
	private boolean highRes;

	private Event updatedEvent;
	private City eventCity;

	@Override
	protected String doExecute() throws Exception {
		// Some initialization first
		final ItemKey userTokenKey = CalmFactory.createKey(User.TOKEN_TYPE,
				getNxtpUserToken());
		final CalContext context = ContextFactory.createContext(getLocale());
		MutableEvent event = null;
		ApisRequest request = ApisFactory.createCompositeRequest();

		// Adding criterion that fetches user
		request.addCriterion(apisUserCriterionFactory
				.createApisCriterion(userTokenKey));

		// Now fetching event or creating new one
		boolean newEvent = eventId == null || "".equals(eventId);
		if (!newEvent) {
			// If we got an id we retrieve the event to update
			final ItemKey eventKey = CalmFactory.parseKey(eventId);
			request.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(eventKey))
					.aliasedBy(APIS_ALIAS_EVENT)
					.with(Description.class)
					.addCriterion(
							SearchRestriction.adaptKey(eventLocationAdapter)
									.aliasedBy(Constants.APIS_ALIAS_PLACE)));
		}
		// Querying localization
		ItemKey placeKey = CalmFactory.parseKey(placeId);
		request.addCriterion(SearchRestriction.uniqueKeys(
				Arrays.asList(placeKey)).aliasedBy(Constants.APIS_ALIAS_PLACE));

		// Executing our APIS request
		final ApiCompositeResponse userResponse = (ApiCompositeResponse) getApiService()
				.execute(request, context);
		// Checking user first because if KO we cancel everything
		final User user = userResponse.getUniqueElement(User.class,
				Constants.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Extracting localization
		final GeographicItem geoItem = userResponse.getUniqueElement(
				GeographicItem.class, Constants.APIS_ALIAS_PLACE);
		// We need to assign a city to this event for common operation based on
		// localization
		eventCity = null;
		if (geoItem != null) {
			// Unwrapping place city if needed
			if (geoItem instanceof Place) {
				eventCity = ((Place) geoItem).getCity();
			} else if (geoItem instanceof City) {
				eventCity = (City) geoItem;
			}
		}
		// Initializing event
		if (!newEvent) {
			event = (MutableEvent) userResponse.getUniqueElement(Event.class,
					APIS_ALIAS_EVENT);
			// For event series, we update author each time a modification is
			// made
			if (event instanceof EventSeries) {
				event.setAuthorKey(user.getKey());
			}
		} else {
			// If we don't have an existing event id, this is a new event
			if (monthRecurrency == null || monthRecurrency < -1) {
				event = (MutableEvent) eventService.createTransientObject();
			} else {
				event = (MutableEvent) eventSeriesService
						.createTransientObject();
			}
			// Setting author of a new event
			event.setAuthorKey(user.getKey());
		}
		// Updating fields
		final String oldName = event.getName();
		final String oldPlace = newEvent ? null : event.getLocationKey()
				.toString();
		final String oldStart = newEvent || event.getStartDate() == null ? null
				: puffDateFormat.format(event.getStartDate());
		final String oldEnd = newEvent || event.getEndDate() == null ? null
				: puffDateFormat.format(event.getEndDate());
		final List<? extends Description> oldDescriptions = event
				.get(Description.class);

		GeographicItem oldEventPlace = event.getUnique(GeographicItem.class,
				Constants.APIS_ALIAS_PLACE);
		// Updating information
		event.setName(name);
		event.setLocationKey(placeKey);
		Date eventStartDate = null;
		TimeZone serverTimezone = TimeZone.getDefault();
		if (startDate != null) {
			// Trying to parse event start date
			final String startStr = startDate + " " + startHour + ":"
					+ startMinute;
			try {
				if (startStr != null) {
					eventStartDate = dateFormat.parse(startStr);
				}
			} catch (ParseException e) {
				LOGGER.error("Unable to parse date of event " + eventId + " ["
						+ startStr + "]: " + e.getMessage());
			}
			final Date localizedStart = eventManagementService.convertDate(
					eventStartDate, eventCity.getTimezoneId(),
					serverTimezone.getID());
			event.setStartDate(localizedStart);
			eventStartDate = localizedStart;
		}
		Date eventEndDate = null;
		if (endDate != null) {
			// Trying to parse event end date
			final String endStr = endDate + " " + endHour + ":" + endMinute;
			try {
				if (endStr != null) {
					eventEndDate = dateFormat.parse(endStr);
				}
			} catch (ParseException e) {
				if (startDate != null) {
					try {
						Date eventStartNoon = dateFormat.parse(startDate
								+ " 00:00");
						Calendar c = Calendar.getInstance();
						c.setTime(eventStartNoon);
						c.add(Calendar.DATE, 1);
						eventEndDate = c.getTime();
					} catch (ParseException ex) {
						LOGGER.error("Unable to parse GENERATED end date of event "
								+ eventId
								+ " ["
								+ startDate
								+ " 00:00"
								+ "]: "
								+ ex.getMessage());
					}
				} else {
					LOGGER.error("Unable to parse end date of event " + eventId
							+ " [" + endStr + "]: " + e.getMessage());
				}
			}
			final Date localizedEnd = eventManagementService.convertDate(
					eventEndDate, eventCity.getTimezoneId(),
					serverTimezone.getID());
			event.setEndDate(localizedEnd);
			eventEndDate = localizedEnd;
		}
		// Now setting any event series specific information
		if (event instanceof EventSeries) {
			// Saving old values

			final MutableEventSeries series = (MutableEventSeries) event;
			final boolean oldMonday = series.isMonday();
			final boolean oldTuesday = series.isTuesday();
			final boolean oldWednesday = series.isWednesday();
			final boolean oldThursday = series.isThursday();
			final boolean oldFriday = series.isFriday();
			final boolean oldSaturday = series.isSaturday();
			final boolean oldSunday = series.isSunday();
			final Integer oldStartHour = series.getStartHour();
			final Integer oldStartMinute = series.getStartMinute();
			final Integer oldEndHour = series.getEndHour();
			final Integer oldEndMinute = series.getEndMinute();
			series.setWeekOfMonthOffset(monthRecurrency == 0 ? null
					: monthRecurrency);
			series.setMonday(monday);
			series.setTuesday(tuesday);
			series.setWednesday(wednesday);
			series.setThursday(thursday);
			series.setFriday(friday);
			series.setSaturday(saturday);
			series.setSunday(sunday);
			series.setStartHour(Integer.valueOf(startHour));
			series.setStartMinute(Integer.valueOf(startMinute));
			series.setEndHour(Integer.valueOf(endHour));
			series.setEndMinute(Integer.valueOf(endMinute));
			try {
				if (calendarType != null) {
					series.setCalendarType(CalendarType.valueOf(calendarType));
				}
			} catch (IllegalArgumentException e) {
				LOGGER.error("Invalid calendar type '" + calendarType
						+ "', ignoring: " + e.getMessage());
			}
			// Saving event back to our persistent data store
			ContextHolder.toggleWrite();
			eventService.saveItem(event);

			notificationService.sendEventSeriesUpdateEmailNotification(event,
					user, eventId, oldName, oldEventPlace, oldStart, oldEnd,
					oldStartHour, oldStartMinute, oldEndHour, oldEndMinute,
					oldMonday, oldTuesday, oldWednesday, oldThursday,
					oldFriday, oldSaturday, oldSunday, oldDescriptions,
					descriptions, keys, geoItem);
		} else {
			// Saving event back to our persistent data store
			ContextHolder.toggleWrite();
			eventService.saveItem(event);

			notificationService.sendEventUpdateEmailNotification(event, user,
					eventId, oldName, oldEventPlace, oldStart, oldEnd,
					oldDescriptions, descriptions, keys, geoItem);
		}

		// Updating descriptions by delegating to the management service
		final boolean descChanged = descriptionsManagementService
				.updateDescriptions(user, PUFF_FIELD_DESCRIPTION, event,
						languages, keys, descriptions, descriptionSourceId);

		if (eventCity != null) {
			geoService.setItemFor(event.getKey(), eventCity.getKey());
		}

		// Fetching full event object and store it back to the search layer
		if (event instanceof EventSeries) {
			eventManagementService.refreshEventSeries((EventSeries) event);
		}
		request = ApisFactory.createCompositeRequest();
		request.addCriterion(apisCriterionFactory.createApisCriterion(event
				.getKey()));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, context);
		final Event fullEvent = response.getUniqueElement(Event.class,
				Constants.APIS_ALIAS_EVENT);
		if (!(event instanceof EventSeries)) {
			searchService.storeCalmObject(fullEvent, SearchScope.CHILDREN);
		}
		newEventId = fullEvent.getKey().toString();
		final ItemKey eventKey = fullEvent.getKey();

		// Now that we have an id, we log changes for backoffice
		final Locale locale = getLocale();
		final boolean nameChanged = puffService.log(eventKey, PUFF_FIELD_NAME,
				oldName, name, locale, user);
		final boolean placeChanged = puffService.log(eventKey,
				PUFF_FIELD_PLACE, oldPlace, placeId, locale, user);
		boolean dateChanged = false;
		if (eventStartDate != null) {
			dateChanged |= puffService.log(eventKey, PUFF_FIELD_START,
					oldStart, puffDateFormat.format(eventStartDate), locale,
					user);
		}
		if (eventEndDate != null) {
			dateChanged |= puffService.log(eventKey, PUFF_FIELD_END, oldEnd,
					puffDateFormat.format(eventEndDate), locale, user);
		}
		// Loggin series info
		if (monthRecurrency != null && monthRecurrency >= -1) {
			puffService.log(eventKey, PUFF_FIELD_WEEK_OFFSET, null,
					monthRecurrency.toString(), locale, user);
		}
		puffService.log(eventKey, PUFF_FIELD_MONDAY, null,
				String.valueOf(monday), locale, user);
		puffService.log(eventKey, PUFF_FIELD_TUESDAY, null,
				String.valueOf(tuesday), locale, user);
		puffService.log(eventKey, PUFF_FIELD_WEDNESDAY, null,
				String.valueOf(wednesday), locale, user);
		puffService.log(eventKey, PUFF_FIELD_THURSDAY, null,
				String.valueOf(thursday), locale, user);
		puffService.log(eventKey, PUFF_FIELD_FRIDAY, null,
				String.valueOf(friday), locale, user);
		puffService.log(eventKey, PUFF_FIELD_SATURDAY, null,
				String.valueOf(saturday), locale, user);
		puffService.log(eventKey, PUFF_FIELD_SUNDAY, null,
				String.valueOf(sunday), locale, user);

		// Logging activity
		MutableActivity activity = null;
		if (!newEvent) {
			// When at least one information changes we initiate an activity
			if (nameChanged || placeChanged || descChanged || dateChanged
					|| (event instanceof EventSeries)) {
				activity = (MutableActivity) activitiesService
						.createTransientObject();
				activity.setDate(new Date());
				activity.setActivityType(ActivityType.UPDATE);
				String updatedFields = null;
				if (event instanceof EventSeries) {
					// Specializing field label by type of calendar
					final CalendarType calendarType = ((EventSeries) event)
							.getCalendarType();
					updatedFields = ActivityConstants.SERIES_FIELD_PREFIX
							+ (calendarType == null ? CalendarType.EVENT.name()
									: calendarType.name());
					// For a recurring event, since no dedicated page exists, we
					// log the change on parent table
					activity.setLoggedItemKey(event.getLocationKey());
				} else {
					activity.setLoggedItemKey(event.getKey());
					updatedFields = buildUpdatedFieldsString(nameChanged,
							placeChanged, descChanged, dateChanged);
				}
				activity.setExtraInformation(updatedFields);
				activity.setUserKey(user.getKey());
			}
		} else {
			// Setting up a CREATION activity that points on the place of the
			// event
			activity = (MutableActivity) activitiesService
					.createTransientObject();
			activity.setDate(new Date());
			activity.setActivityType(ActivityType.CREATION);
			activity.setLoggedItemKey(placeKey);
			activity.setExtraInformation(newEventId);
			activity.setUserKey(user.getKey());
		}
		// Whenever an activity has been created we store it
		if (activity != null) {
			// final GeographicItem localization = fullEvent
			// .getUnique(GeographicItem.class);
			if (geoItem != null) {
				activity.add(geoItem);
			}
			activitiesService.saveItem(activity);
			searchService.storeCalmObject(activity, SearchScope.CHILDREN);
		}

		updatedEvent = event;
		if (EventSeries.SERIES_CAL_ID.equals(fullEvent.getKey().getType())) {
			if (((EventSeries) fullEvent).getCalendarType() != CalendarType.EVENT) {
				eventPlaceId = fullEvent.getLocationKey().toString();
				return ACTION_RESULT_PLACE;
			}
		}

		return SUCCESS;
	}

	private String buildUpdatedFieldsString(boolean nameChanged,
			boolean placeChanged, boolean descChanged, boolean dateChanged) {
		final StringBuilder buf = new StringBuilder();
		String separator = "";
		if (nameChanged) {
			buf.append(ActivityConstants.NAME_FIELD);
			separator = ActivityConstants.SEPARATOR;
		}
		if (placeChanged) {
			buf.append(separator);
			buf.append(ActivityConstants.PLACE_FIELD);
			separator = ActivityConstants.SEPARATOR;
		}
		if (descChanged) {
			buf.append(separator);
			buf.append(ActivityConstants.DESCRIPTION_FIELD);
			separator = ActivityConstants.SEPARATOR;
		}
		if (dateChanged) {
			buf.append(separator);
			buf.append(ActivityConstants.DATE_FIELD);
			separator = ActivityConstants.SEPARATOR;
		}
		return buf.toString();
	}

	@Override
	public String getJson() {
		if (updatedEvent instanceof EventSeries) {
			final Collection<JsonHour> hours = jsonBuilder.buildJsonHours(
					Arrays.asList((EventSeries) updatedEvent), eventCity,
					getLocale());
			return JSONObject.fromObject(hours.iterator().next()).toString();
		} else if (updatedEvent instanceof Event) {
			final JsonEvent e = new JsonEvent();

			// Filling the JSON structure
			jsonBuilder.fillJsonEvent(e, updatedEvent, highRes, getLocale(),
					null);
			return JSONObject.fromObject(e).toString();
		} else {
			throw new UnsupportedOperationException(
					"JSON update of events not yet supported");
		}
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(String startMinute) {
		this.startMinute = startMinute;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(String endMinute) {
		this.endMinute = endMinute;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public void setApisCriterionFactory(
			ApisCriterionFactory apisCriterionFactory) {
		this.apisCriterionFactory = apisCriterionFactory;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public void setEventService(CalPersistenceService eventService) {
		this.eventService = eventService;
	}

	public String getNewEventId() {
		return newEventId;
	}

	public String getEventPlaceId() {
		return eventPlaceId;
	}

	public void setPuffService(PuffService puffService) {
		this.puffService = puffService;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setApisUserCriterionFactory(
			ApisCriterionFactory apisUserCriterionFactory) {
		this.apisUserCriterionFactory = apisUserCriterionFactory;
	}

	@Override
	public void setDescriptionLanguageCode(String[] languages) {
		this.languages = languages;
	}

	@Override
	public String[] getDescriptionLanguageCode() {
		return languages;
	}

	@Override
	public void setDescriptionKey(String[] descriptionKeys) {
		this.keys = descriptionKeys;
	}

	@Override
	public String[] getDescriptionKey() {
		return keys;
	}

	@Override
	public void setDescription(String[] descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public void setDescriptionsManagementService(
			DescriptionsManagementService descriptionManagementService) {
		this.descriptionsManagementService = descriptionManagementService;
	}

	@Override
	public String[] getDescription() {
		return descriptions;
	}

	public void setMonthRecurrency(Integer monthRecurrency) {
		this.monthRecurrency = monthRecurrency;
	}

	public Integer getMonthRecurrency() {
		return monthRecurrency;
	}

	public boolean isMonday() {
		return monday;
	}

	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	public boolean isTuesday() {
		return tuesday;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	public boolean isWednesday() {
		return wednesday;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	public boolean isThursday() {
		return thursday;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	public boolean isFriday() {
		return friday;
	}

	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public boolean isSunday() {
		return sunday;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	public void setEventSeriesService(CalPersistenceService eventSeriesService) {
		this.eventSeriesService = eventSeriesService;
	}

	public void setEventManagementService(
			EventManagementService eventManagementService) {
		this.eventManagementService = eventManagementService;
	}

	@Override
	public void setDescriptionSourceId(String[] descriptionSourceId) {
		this.descriptionSourceId = descriptionSourceId;
	}

	@Override
	public String[] getDescriptionSourceId() {
		return descriptionSourceId;
	}

	public void setGeoService(CalPersistenceService geoService) {
		this.geoService = geoService;
	}

	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	public String getCalendarType() {
		return calendarType;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}
}
