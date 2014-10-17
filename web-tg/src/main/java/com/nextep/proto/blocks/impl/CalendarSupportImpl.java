package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.events.model.CalendarType;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.CalendarSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

/**
 * Default calendar support implementation
 * 
 * @author cfondacci
 */
public class CalendarSupportImpl implements CalendarSupport {

	// Constants
	private static final String MSG_KEY_PREFIX_CALTYPE = "calendarType.";
	private static final String MSG_KEY_PREFIX_ADD = "calendarType.add.";
	private static final String MSG_KEY_PREFIX_NEXT_DATE = "overview.calendar.";

	private static final String KEY_PENDING = "pending";
	private static final String KEY_VALID = "valid";
	private static final String KEY_SUBTITLE = ".subtitle.";
	private static final String KEY_TITLE = ".title.";

	// Injected support
	private MessageSource messageSource;
	private EventManagementService eventManagementService;
	private DistanceDisplayService distanceDisplayService;

	// Local variables
	private Map<CalendarType, List<EventSeries>> seriesMap;
	private Map<CalendarType, DateSuffixHolder> nextStartsMap;
	private List<CalendarType> sortedCalendarTypes;
	private Date localizedNow;
	private Locale locale;
	private CalmObject parent;
	private UrlService urlService;
	private boolean nextTimesInitialized = false;

	/**
	 * A class to hold information for common processing
	 */
	private class DateSuffixHolder {
		String suffix;
		Date date;

		public DateSuffixHolder(String suffix, Date date) {
			this.suffix = suffix;
			this.date = date;
		}
	}

	@Override
	public void initialize(List<? extends EventSeries> seriesList,
			CalmObject parent, UrlService urlService, Locale locale) {

		// Initialization of local constructs
		seriesMap = new HashMap<CalendarType, List<EventSeries>>();
		nextStartsMap = new HashMap<CalendarType, DateSuffixHolder>();
		sortedCalendarTypes = Arrays.asList(CalendarType.OPENING,
				CalendarType.HAPPY_HOUR);

		// Hashing all series by their attached calendar type
		for (EventSeries series : seriesList) {
			List<EventSeries> typedList = seriesMap.get(series
					.getCalendarType());
			if (typedList == null) {
				typedList = new ArrayList<EventSeries>();
				seriesMap.put(series.getCalendarType(), typedList);
			}
			typedList.add(series);
		}

		// Saving other info
		this.locale = locale;
		this.parent = parent;
		this.urlService = urlService;
	}

	@Override
	public List<CalendarType> getTypes() {
		return sortedCalendarTypes;
	}

	@Override
	public String getCalendarTypeLabel(CalendarType calendarType) {
		try {
			return messageSource.getMessage(MSG_KEY_PREFIX_CALTYPE
					+ calendarType.name(), null, locale);
		} catch (NoSuchMessageException e) {
			return calendarType.name();
		}
	}

	@Override
	public List<?> getCalendarsFor(CalendarType calendarType) {
		final List<EventSeries> list = seriesMap.get(calendarType);
		if (list == null) {
			return Arrays.asList(calendarType);
		} else {
			return list;
		}
	}

	@Override
	public String getEditUrl(Object series) {
		if (series instanceof EventSeries) {
			return urlService.getEventEditionFormUrl(
					DisplayHelper.getDefaultAjaxContainer(),
					((EventSeries) series).getKey());
		} else if (series instanceof CalendarType) {
			return getAddCalendarUrl((CalendarType) series);
		}
		return null;
	}

	@Override
	public String getLabel(Object obj) {
		StringBuilder buf = new StringBuilder();
		if (obj instanceof EventSeries) {
			final EventSeries series = (EventSeries) obj;
			if (series.getCalendarType() == CalendarType.HAPPY_HOUR) {
				buf.append(series.getName() + " - ");
			}
			buf.append(eventManagementService.buildReadableTimeframe(series,
					locale));
			// } else {
			// return messageSource.getMessage(MSG_KEY_PREFIX_ADD
			// + ((CalendarType) series).name(), null, locale);
		}
		return buf.toString();
	}

	@Override
	public String getAddCalendarUrl(CalendarType calendarType) {
		return urlService.getEventEditionFormUrl(
				DisplayHelper.getDefaultAjaxContainer(), parent.getKey(),
				calendarType);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setEventManagementService(
			EventManagementService eventManagementService) {
		this.eventManagementService = eventManagementService;
	}

	/**
	 * Prepares next times information
	 */
	private void initNextTimes() {
		if (!nextTimesInitialized) {
			nextTimesInitialized = true;
			// Retrieving time zone for current parent
			String timeZoneId = null;
			if (parent instanceof Place) {
				timeZoneId = ((Place) parent).getCity().getTimezoneId();
			} else if (parent instanceof City) {
				timeZoneId = ((City) parent).getTimezoneId();
			}

			// Getting localized 'now' date
			localizedNow = eventManagementService.getLocalizedNow(timeZoneId);

			// Processing series
			for (CalendarType calendarType : seriesMap.keySet()) {
				final List<EventSeries> list = seriesMap.get(calendarType);
				if (list != null) {
					Date nearestStartDate = null;
					Date nearestEndDate = null;

					// Processing all defined series
					for (EventSeries series : list) {
						// Computes the next date of this series
						final Date nextStart = eventManagementService
								.computeNextStart(series, localizedNow, true);

						// Setting date as nearest if inferior to current
						if (nearestStartDate == null
								|| nextStart.getTime() < nearestStartDate
										.getTime()) {
							nearestStartDate = nextStart;
						}
						// If it exists, compute the next end
						if (nextStart != null) {
							final Date nextEnd = eventManagementService
									.computeNextStart(series, localizedNow,
											false);
							// Setting date as nearest if inferior to current
							if (nearestEndDate == null
									|| nextEnd.getTime() < nearestEndDate
											.getTime()) {
								nearestEndDate = nextEnd;
							}

						}
					}

					// Computing what needs to be displayed (next start or next
					// end?)
					DateSuffixHolder holder = null;
					if (nearestStartDate != null) {
						// If next start is before next end, then event is not
						// yet started
						if (nearestStartDate.getTime() < nearestEndDate
								.getTime()) {
							holder = new DateSuffixHolder(KEY_PENDING,
									nearestStartDate);
						} else {
							holder = new DateSuffixHolder(KEY_VALID,
									nearestEndDate);
						}
					}
					nextStartsMap.put(calendarType, holder);
				}
			}
		}
	}

	@Override
	public String getNextTimeLabel(CalendarType calendarType) {
		return getNextTimeLabel(calendarType, KEY_TITLE);
	}

	private String getNextTimeLabel(CalendarType calendarType,
			String messageType) {
		// Ensuring proper init
		initNextTimes();

		final DateSuffixHolder dataHolder = nextStartsMap.get(calendarType);
		String label = null;
		if (dataHolder != null) {
			// Retrieving values
			final String suffix = dataHolder.suffix;
			final Date nextDate = dataHolder.date;

			// Building time difference label
			final String timeLabel = distanceDisplayService
					.getTimeBetweenDates(localizedNow, nextDate, locale);

			// Generating label
			label = messageSource.getMessage(MSG_KEY_PREFIX_NEXT_DATE
					+ calendarType.name() + messageType + suffix,
					new Object[] { timeLabel }, locale);
		}
		return label;

	}

	@Override
	public String getNextTimeSubtitle(CalendarType calendarType) {
		return getNextTimeLabel(calendarType, KEY_SUBTITLE);
	}

	@Override
	public String getNextTimeCSSClass(CalendarType calendarType) {
		// Ensuring proper init
		initNextTimes();

		final DateSuffixHolder dataHolder = nextStartsMap.get(calendarType);
		String cssClass = "";
		if (dataHolder != null) {
			// Retrieving values
			final String suffix = dataHolder.suffix;
			final Date date = dataHolder.date;

			final long delta = date.getTime() - localizedNow.getTime();
			if (KEY_PENDING.equals(suffix) && delta > 6 * 60 * 60 * 1000) {
				cssClass = "event-closed";
			} else {
				cssClass = "event-" + suffix;
			}
		}
		return cssClass;
	}

	public void setDistanceDisplayService(
			DistanceDisplayService distanceDisplayService) {
		this.distanceDisplayService = distanceDisplayService;
	}
}
