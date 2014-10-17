package com.nextep.proto.services.impl;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.MutableEvent;
import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.properties.model.MutableProperty;
import com.nextep.properties.model.Property;
import com.nextep.properties.model.impl.PropertyImpl;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.exceptions.GenericWebappException;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.apis.service.ApiService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.smaug.common.model.SearchScope;

public class EventManagementServiceImpl implements EventManagementService {

	private static final Log LOGGER = LogFactory
			.getLog(EventManagementServiceImpl.class);
	private static final DateFormat DAY_FORMAT = new SimpleDateFormat(
			"yyyyMMdd");
	private static final DateFormat STD_TIME_FORMAT = new SimpleDateFormat(
			"HH:mm");
	private static final String PUFF_FIELD_DESCRIPTION = "evt_description";
	private static final String MSG_KEY_DAILY = "event.daily";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	private CalPersistenceService eventService;
	private CalPersistenceService mediaService;
	private SearchPersistenceService searchService;
	private MessageSource messageSource;
	private ApiService apiService;
	private DescriptionsManagementService descriptionsManagementService;
	private TaskRunnerService taskService;
	private int maxEventsCreation;

	private class RefreshTask extends AbstractTask<EventSeries> {

		private EventSeries series;

		public RefreshTask(EventSeries series) {
			this.series = series;
		}

		@Override
		public EventSeries execute(TaskExecutionContext context)
				throws TaskExecutionException, InterruptedException {
			try {
				final ApisRequest request = (ApisRequest) ApisFactory
						.createRequest(EventSeries.class)
						.uniqueKey(series.getKey().getId())
						.with((WithCriterion) SearchRestriction.with(
								Event.class).with(Description.class))
						.with(Description.class)
						.with(Media.class)
						.addCriterion(
								SearchRestriction
										.adaptKey(eventLocationAdapter));
				final ApiResponse response = apiService.execute(request,
						ContextFactory.createContext((Locale) null));

				// Retrieving series + attached events
				final EventSeries mySeries = (EventSeries) response
						.getUniqueElement();
				final List<? extends Event> seriesEvents = mySeries
						.get(Event.class);
				LOGGER.info("Series " + mySeries.getKey() + " has "
						+ seriesEvents + " existing events.");

				// Initializing iterator
				final Iterator<? extends Event> eventsIt = seriesEvents
						.iterator();
				Event currentEventInSerie = null;

				// Computing next event instances
				Date date = new Date();
				Date nextEventStart = date;
				int createdEvents = 0;
				final Calendar timeCal = Calendar.getInstance();
				ContextHolder.toggleWrite();
				while (nextEventStart != null
						&& createdEvents < maxEventsCreation) {
					// Next start
					nextEventStart = computeNextStart(mySeries, date, true);
					// If we got a next event
					if (nextEventStart != null) {
						createdEvents++;
						LOGGER.info("Series " + mySeries.getKey()
								+ " is processing event on " + nextEventStart
								+ " for generation");
						// Next end
						Date nextEventEnd = computeNextStart(mySeries, date,
								false);

						// Locating whether this event has already been created
						// in this series
						while ((currentEventInSerie == null || compareDays(
								currentEventInSerie.getStartDate(),
								nextEventStart) < 0)
								&& eventsIt.hasNext()) {
							currentEventInSerie = eventsIt.next();
						}

						// Filling existing event or creating a new one
						Event thisEvent = null;
						boolean newEvent = false;
						if (currentEventInSerie != null
								&& compareDays(
										currentEventInSerie.getStartDate(),
										nextEventStart) == 0) {
							LOGGER.info("  > Updating 1 matching existing event "
									+ currentEventInSerie.getKey()
									+ " found on "
									+ currentEventInSerie.getStartDate());
							thisEvent = currentEventInSerie;
							fillEventFromSeries(mySeries,
									(MutableEvent) thisEvent, nextEventStart,
									nextEventEnd);
						} else {
							LOGGER.info("  > Creating new event from "
									+ nextEventStart + " to " + nextEventEnd);
							thisEvent = createEventFromSeries(mySeries,
									nextEventStart, nextEventEnd);
							newEvent = true;
						}
						if (thisEvent != null) {
							// Persisting
							eventService.saveItem(thisEvent);
							LOGGER.info("Event " + thisEvent.getKey()
									+ (newEvent ? " created" : " updated"));

							// Storing event in search
							searchService.storeCalmObject(thisEvent,
									SearchScope.CHILDREN);
							LOGGER.info("Event " + thisEvent.getKey()
									+ " stored in search index");

							// Preparing event descriptions structures
							if (newEvent) {
								final List<String> descriptionLanguageCodes = new ArrayList<String>();
								final List<String> descriptionItemKeys = new ArrayList<String>();
								final List<String> descriptions = new ArrayList<String>();
								final List<String> sourceIds = new ArrayList<String>();
								final List<? extends Description> descs = mySeries
										.get(Description.class);
								for (Description d : descs) {
									descriptionLanguageCodes.add(d.getLocale()
											.getLanguage());
									descriptionItemKeys.add(null);
									descriptions.add(d.getDescription());
									sourceIds.add(String.valueOf(d
											.getSourceId()));
								}
								try {
									descriptionsManagementService
											.updateDescriptions(
													null, // No author
													PUFF_FIELD_DESCRIPTION,
													thisEvent,
													descriptionLanguageCodes
															.toArray(new String[descriptionLanguageCodes
																	.size()]),
													descriptionItemKeys
															.toArray(new String[descriptionItemKeys
																	.size()]),
													descriptions
															.toArray(new String[descriptions
																	.size()]),
													sourceIds
															.toArray(new String[sourceIds
																	.size()]));
								} catch (GenericWebappException e) {
									LOGGER.error(
											"Unable to store descriptions for Event "
													+ thisEvent.getKey()
													+ " of series "
													+ mySeries.getKey() + ": "
													+ e.getMessage(), e);
								}
							}

							// Registers any media from series to event, only if
							// current event has no media
							// and series has some
							final List<? extends Media> currentEventMedias = thisEvent
									.get(Media.class);
							final List<? extends Media> seriesMedias = mySeries
									.get(Media.class);
							if (currentEventMedias.isEmpty()
									&& !seriesMedias.isEmpty()) {
								for (Media m : seriesMedias) {
									if (m.isOnline()) {
										final MutableMedia newMedia = (MutableMedia) mediaService
												.createTransientObject();
										newMedia.setFormat(m.getFormat());
										newMedia.setMiniThumbUrl(m
												.getMiniThumbUrl());
										newMedia.setOriginalUrl(m
												.getOriginalUrl());
										newMedia.setPreferenceOrder(m
												.getPreferenceOrder());
										newMedia.setRelatedItemKey(thisEvent
												.getKey());
										newMedia.setSourceId(m.getSourceId());
										newMedia.setThumbUrl(m.getThumbUrl());
										newMedia.setTitle(m.getTitle());
										newMedia.setUploadDate(m
												.getUploadDate());
										newMedia.setUrl(m.getUrl());
										newMedia.setVideo(m.isVideo());
										mediaService.saveItem(newMedia);
									}
								}
							}
							timeCal.setTime(nextEventEnd);
							timeCal.add(Calendar.HOUR, 1);
							date = timeCal.getTime();
						}
					}
				}

			} catch (ApisException e) {
				LOGGER.error(
						"APIS exception while fetching event series list: "
								+ e.getMessage(), e);
			} catch (RuntimeException e) {
				LOGGER.error(
						"Runtime exception during generation of event series "
								+ series.getKey() + ": " + e.getMessage(), e);
			}
			return series;
		}
	}

	private int compareDays(Date d1, Date d2) {
		if (d1 == null) {
			return -1;
		}
		return DAY_FORMAT.format(d1).compareTo(DAY_FORMAT.format(d2));
	}

	@Override
	public Event createEventFromSeries(EventSeries series, Date startDate,
			Date endDate) {
		if (series.getCalendarType() != null
				&& series.getCalendarType().generateEvents()) {
			final MutableEvent event = (MutableEvent) eventService
					.createTransientObject();
			fillEventFromSeries(series, event, startDate, endDate);
			return event;
		} else {
			return null;
		}
	}

	public static void fillEventFromSeries(EventSeries series,
			MutableEvent event, Date startDate, Date endDate) {

		event.setName(series.getName());
		event.setLocationKey(series.getLocationKey());
		event.setStartDate(startDate);
		event.setEndDate(endDate);
		event.setSeriesKey(series.getKey());
		try {
			final GeographicItem geoItem = series
					.getUnique(GeographicItem.class);
			if (geoItem != null) {
				event.add(geoItem);
			} else {
				LOGGER.error("No localization found for event series "
						+ series.getKey());
			}
		} catch (CalException e) {
			LOGGER.error(
					"Unable to get location of event series " + series.getKey()
							+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public Date computeNextStart(EventSeries series, Date startDate,
			boolean isStart) {
		// If we are out of bound, we don't have anything to do
		if (series.getEndDate() != null
				&& startDate.compareTo(series.getEndDate()) > 0) {
			return null;
		}
		// Building the list of days of the week for this series
		final List<Integer> days = buildDaysOfWeekList(series);

		final Integer weekOffset = series.getWeekOfMonthOffset();
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		// Setting hours
		// final Calendar seriesCal = Calendar.getInstance();
		// if (isStart) {
		// seriesCal.setTime(series.getStartDate());
		// } else {
		// seriesCal.setTime(series.getEndDate());
		// }
		// c.set(Calendar.HOUR_OF_DAY, seriesCal.get(Calendar.HOUR_OF_DAY));

		final int currentWeekOffset = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		// If we are after the event series week offset, we need to
		// recompute for next month
		if (weekOffset != null && currentWeekOffset > weekOffset) {
			// We set the date as 1st of next month
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.add(Calendar.MONTH, 1);
			// And we recompute and stop if we are more than 100 days
			if (System.currentTimeMillis() + 8640000000l < c.getTimeInMillis()) {
				return null;
			}
			return computeNextStart(series, c.getTime(), isStart);
		} else if (weekOffset == null || currentWeekOffset == weekOffset) {
			final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			Date minDate = null;
			// Iterating on all days defined on the series
			for (Integer day : days) {
				// Compute the day delta within a week
				int delta = day - dayOfWeek;

				// If the series is for yesterday but the end finishes the day
				// after, then we need to consider
				int hourDelta = 0;
				if (delta == -1 && !isStart
						&& series.getEndHour() < series.getStartHour()) {
					delta = 0;
					hourDelta = -24;
				}
				// If day is passed, then we compute for next week
				if (delta < 0) {
					delta += 7;
				} else if (delta == 0) {

					// If this is today, we need to consider time of day
					final int currentHour = c.get(Calendar.HOUR_OF_DAY);
					final int currentMinute = c.get(Calendar.MINUTE);

					// Current time in the form of HHMM integer for numeric
					// comparison
					final int currentTime = currentHour * 100 + currentMinute;

					// Computing event time
					int eventTime;
					if (isStart) {
						eventTime = series.getStartHour() * 100
								+ series.getStartMinute();
					} else {
						int seriesEnd = series.getEndHour();
						// Handling the end on next day
						if (seriesEnd < series.getStartHour()) {
							seriesEnd += 24 + hourDelta;
							delta += hourDelta / 24;
						}
						eventTime = seriesEnd * 100 + series.getEndMinute();
					}
					// If too late, then we compute for next week
					if (currentTime > eventTime) {
						delta += 7;
					}
				}
				// Computing next date
				final Calendar cd = Calendar.getInstance();
				cd.setTime(c.getTime());
				cd.add(Calendar.DATE, delta);
				int dayWeekOffset = cd.get(Calendar.DAY_OF_WEEK_IN_MONTH);
				if ((minDate == null || cd.getTime().compareTo(minDate) < 0)
						&& (weekOffset == null || dayWeekOffset == weekOffset)) {
					minDate = cd.getTime();
				}
			}
			if (minDate != null) {
				c.setTime(minDate);
				return computeDate(series, c, isStart);
			}
			if (weekOffset != null) {
				// We fall here if the day in the week is past, we go for next
				// month
				// We set the date as 1st of next month
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.MONTH, 1);
			} else {
				if (days == null || days.isEmpty()) {
					return null;
				}
				// We try next week
				int day = days.iterator().next();
				c.add(Calendar.DATE, day + 7 - dayOfWeek);
			}
			// And we recompute
			return computeNextStart(series, c.getTime(), isStart);
		} else {
			Date minDate = null;
			c.set(Calendar.DAY_OF_MONTH, 1);
			c.add(Calendar.DATE, (weekOffset - 1) * 7);
			final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			// Looking for the closest day starting from the first day with
			// the specified offset
			for (Integer day : days) {
				// Starting from our current first day of the "week offset"
				// and jumping to the day where the event takes place
				final Calendar cd = Calendar.getInstance();
				cd.setTime(c.getTime());
				int delta = day - dayOfWeek;
				if (delta < 0) {
					delta += 7;
				}
				cd.add(Calendar.DATE, delta);
				// Now we check if this is the closest date
				final Date d = cd.getTime();
				if (minDate == null || d.compareTo(minDate) < 0) {
					minDate = d;
				}
			}
			// Returning the closest date
			final Calendar finalCal = Calendar.getInstance();
			finalCal.setTime(minDate);
			return computeDate(series, finalCal, isStart);
		}
	}

	private static Date computeDate(EventSeries series, Calendar c,
			boolean isStart) {
		if (isStart) {
			final int hour = series.getStartHour(); // scal.get(Calendar.HOUR_OF_DAY);
			final int minute = series.getStartMinute(); // scal.get(Calendar.MINUTE);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
		} else {
			final int hour = series.getEndHour();
			final int minute = series.getEndMinute();
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			if (hour < series.getStartHour()) {
				c.add(Calendar.DATE, 1);
			}
		}
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	private static List<Integer> buildDaysOfWeekList(EventSeries series) {
		final List<Integer> days = new ArrayList<Integer>();
		if (series.isSunday()) {
			days.add(Calendar.SUNDAY);
		}
		if (series.isMonday()) {
			days.add(Calendar.MONDAY);
		}
		if (series.isTuesday()) {
			days.add(Calendar.TUESDAY);
		}
		if (series.isWednesday()) {
			days.add(Calendar.WEDNESDAY);
		}
		if (series.isThursday()) {
			days.add(Calendar.THURSDAY);
		}
		if (series.isFriday()) {
			days.add(Calendar.FRIDAY);
		}
		if (series.isSaturday()) {
			days.add(Calendar.SATURDAY);
		}
		return days;
	}

	@Override
	public void refreshEventSeries(EventSeries series) {
		if (series.getCalendarType() != null
				&& series.getCalendarType().generateEvents()) {
			final RefreshTask task = new RefreshTask(series);
			taskService.execute(task);
		}
	}

	@Override
	public Date computeNext(EventSeries series, String timezoneId,
			boolean isStart) {
		final Date localizedNow = getLocalizedNow(timezoneId);
		// Computing next date with this information
		final Date nextDate = computeNextStart(series, localizedNow, isStart);
		if (nextDate != null) {
			final Date serverNextDate = convertDate(nextDate, timezoneId,
					TimeZone.getDefault().getID());
			return serverNextDate;
		} else {
			return null;
		}
	}

	@Override
	public Date getLocalizedNow(String timezoneId) {
		Date now = new Date();
		TimeZone fromTimeZone = TimeZone.getDefault();
		// TimeZone toTimeZone = TimeZone.getTimeZone(timezoneId);

		final Date date = convertDate(now, fromTimeZone.getID(), timezoneId);
		return date;
	}

	@Override
	public Date convertDate(Date date, String fromTimezoneId,
			String toTimezoneId) {
		final TimeZone fromTimeZone = TimeZone.getTimeZone(fromTimezoneId);
		final TimeZone toTimeZone = TimeZone.getTimeZone(toTimezoneId);
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(fromTimeZone);

		// Translating our time to GMT time
		calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
		if (fromTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, calendar.getTimeZone()
					.getDSTSavings() * -1);
		}

		// Translating our time to requested timezone
		calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
		if (toTimeZone.inDaylightTime(calendar.getTime())) {
			calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
		}
		calendar.setTimeZone(toTimeZone);
		return calendar.getTime();
	}

	@Override
	public String buildReadableTimeframe(EventSeries series, Locale l) {
		final DateFormatSymbols symbols = new DateFormatSymbols(l);
		final String[] dayNames = symbols.getShortWeekdays();

		// Building a list from days
		final List<Boolean> enabledList = new ArrayList<Boolean>();
		enabledList.add(series.isSunday());
		enabledList.add(series.isMonday());
		enabledList.add(series.isTuesday());
		enabledList.add(series.isWednesday());
		enabledList.add(series.isThursday());
		enabledList.add(series.isFriday());
		enabledList.add(series.isSaturday());

		int i = 0;
		Integer start = null;
		StringBuilder buf = new StringBuilder();
		String sep = "";
		boolean allTrue = true;
		while (i < enabledList.size()) {
			// Is this day active?
			boolean enabled = enabledList.get(i);
			allTrue = allTrue && enabled;
			// If yes and no start, we register it
			if (start == null && enabled) {
				start = i;
			}
			// If not enabled we print last range
			if (!enabled && start != null) {
				buf.append(sep);
				buf.append(dayNames[start + 1]);
				if (i > start + 1) {
					buf.append("-" + dayNames[i]);
				}
				sep = ",";
				start = null;
			}
			i++;
		}
		// Last part may not have been added
		if (start != null) {
			buf.append(sep);
			buf.append(dayNames[start + 1]);
			if (i > start + 1) {
				buf.append("-" + dayNames[i]);
			}
		}
		if (allTrue) {
			buf = new StringBuilder();
			buf.append(messageSource.getMessage(MSG_KEY_DAILY, null, l));
		}

		// Handling US / european dates
		buf.append(" "
				+ getFormattedTime(series.getStartHour(),
						series.getStartMinute(), l));
		buf.append("-"
				+ getFormattedTime(series.getEndHour(), series.getEndMinute(),
						l));

		return buf.toString();
	}

	/**
	 * Converts the global time into a locale-specific time format (like am/pm
	 * for US, 24-hours clock for french, etc.)
	 * 
	 * @param hour
	 *            the hour component of the time to format
	 * @param minutes
	 *            the minutes component of the time to format
	 * @param l
	 *            the {@link Locale} for which we format this time
	 * @return the formatted time string
	 */
	private String getFormattedTime(int hour, int minutes, Locale l) {
		// Handling US / european dates
		try {
			final Date startDate = STD_TIME_FORMAT.parse(String.format("%02d",
					hour) + ":" + String.format("%02d", minutes));
			final String formattedTime = SimpleDateFormat.getTimeInstance(
					SimpleDateFormat.SHORT, l).format(startDate);
			// Compacting time
			return formattedTime.replace(" ", "").toLowerCase();

		} catch (ParseException e) {
			LOGGER.error("Unable to generate formatted date: " + e.getMessage());
			return "n/a";
		}
	}

	@Override
	public List<Property> convertToProperties(List<EventSeries> seriesList,
			Locale l) {
		final Map<CalendarType, StringBuilder> textMap = new HashMap<CalendarType, StringBuilder>();
		for (EventSeries series : seriesList) {
			StringBuilder buf = textMap.get(series.getCalendarType());
			String sep = "";
			if (buf == null) {
				buf = new StringBuilder();
				textMap.put(series.getCalendarType(), buf);
				sep = " / ";
			}
			buf.append(sep + buildReadableTimeframe(series, l));
		}
		final List<Property> props = new ArrayList<Property>();
		for (CalendarType type : textMap.keySet()) {
			String propCode = null;
			switch (type) {
			case OPENING:
				propCode = Constants.PROP_CODE_OPENING_HOURS;
				break;
			case HAPPY_HOUR:
				propCode = type.name();
				break;
			}
			if (propCode != null) {
				MutableProperty prop = new PropertyImpl();
				prop.setCode(propCode);
				prop.setValue(textMap.get(type).toString());
				props.add(prop);
			}
		}
		return props;
	}

	public void setApiService(ApiService apiService) {
		this.apiService = apiService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public void setEventService(CalPersistenceService eventService) {
		this.eventService = eventService;
	}

	public void setDescriptionsManagementService(
			DescriptionsManagementService descriptionManagementService) {
		this.descriptionsManagementService = descriptionManagementService;
	}

	public void setMaxEventsCreation(int maxEventsCreation) {
		this.maxEventsCreation = maxEventsCreation;
	}

	public void setTaskService(TaskRunnerService taskService) {
		this.taskService = taskService;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
