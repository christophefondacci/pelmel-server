package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.EventEditionSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;

public class EventEditionSupportImpl implements EventEditionSupport {

	private static final Log LOGGER = LogFactory
			.getLog(EventEditionSupportImpl.class);
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy/MM/dd");
	private static final DateFormat HOUR_FORMAT = new SimpleDateFormat("HH");
	private static final DateFormat MIN_FORMAT = new SimpleDateFormat("mm");
	private Event event;
	private GeographicItem location;
	private CalendarType calendarType = CalendarType.EVENT;
	private Locale locale;
	private Date localizedStartDate;
	private Date localizedEndDate;
	private UrlService urlService;

	private MessageSource messageSource;
	@Autowired
	private EventManagementService eventManagementService;

	@Override
	public void initialize(CalmObject eventOrLocation, UrlService urlService,
			Locale locale) {
		final String eventType = eventOrLocation.getKey().getType();
		this.urlService = urlService;
		this.locale = locale;
		if (Event.CAL_ID.equals(eventType)
				|| EventSeries.SERIES_CAL_ID.equals(eventType)) {
			this.event = (Event) eventOrLocation;
			if (EventSeries.SERIES_CAL_ID.equals(eventType)) {
				calendarType = ((EventSeries) eventOrLocation)
						.getCalendarType();
			}

			// Localizing dates
			final String fromTimezoneId = TimeZone.getDefault().getID();
			final String toTimezoneId = eventManagementService
					.getEventTimezoneId(event);
			if (event.getStartDate() != null) {
				localizedStartDate = eventManagementService.convertDate(
						event.getStartDate(), fromTimezoneId, toTimezoneId);
			}
			if (event.getEndDate() != null) {
				localizedEndDate = eventManagementService.convertDate(
						event.getEndDate(), fromTimezoneId, toTimezoneId);
			}
		} else {
			location = (GeographicItem) eventOrLocation;
		}
	}

	@Override
	public String getEventId() {
		return event == null ? "" : event.getKey().toString();
	}

	@Override
	public String getName() {
		return event == null ? "" : DisplayHelper.getName(event);
	}

	@Override
	public String getStartDate() {
		return event == null ? "" : DATE_FORMAT.format(event.getStartDate());
	}

	@Override
	public String getStartHour() {
		if (event != null) {
			if (Event.CAL_ID.equals(event.getKey().getType())) {
				return HOUR_FORMAT.format(localizedStartDate);
			} else {
				return String.valueOf(((EventSeries) event).getStartHour());
			}
		}
		return "";
	}

	@Override
	public String getStartMinute() {
		if (event != null) {
			if (Event.CAL_ID.equals(event.getKey().getType())) {
				return MIN_FORMAT.format(localizedStartDate);
			} else {
				return String.valueOf(((EventSeries) event).getStartMinute());
			}
		}
		return null;
	}

	@Override
	public String getEndDate() {

		return event == null ? "" : DATE_FORMAT.format(event.getEndDate());
	}

	@Override
	public String getEndHour() {
		if (event != null) {
			if (Event.CAL_ID.equals(event.getKey().getType())) {
				return HOUR_FORMAT.format(localizedEndDate);
			} else {
				return String.valueOf(((EventSeries) event).getEndHour());
			}
		}
		return null;
	}

	@Override
	public String getEndMinute() {
		if (event != null) {
			if (Event.CAL_ID.equals(event.getKey().getType())) {
				return MIN_FORMAT.format(localizedEndDate);
			} else {
				return String.valueOf(((EventSeries) event).getEndMinute());
			}
		}
		return null;
	}

	@Override
	public String getPlaceId() {
		if (location != null) {
			return location.getKey().toString();
		} else {
			return event == null ? "" : event.getLocationKey().toString();
		}
	}

	@Override
	public String getPlaceName() {
		GeographicItem geoItem = null;
		if (event != null) {
			try {
				geoItem = event.getUnique(GeographicItem.class);
			} catch (CalException e) {
				LOGGER.error("Unable to extract event localization for edition ["
						+ event.getKey() + "]: " + e.getMessage());
			}
		} else if (location != null) {
			geoItem = location;
		}
		if (geoItem instanceof Place) {
			return GeoHelper.buildShortPlaceLocalizationString((Place) geoItem);
		} else if (geoItem instanceof City) {
			return GeoHelper.buildShortLocalizationString((City) geoItem);
		} else {
			return DisplayHelper.getName(geoItem);
		}
	}

	@Override
	public boolean isSeriesEnabled() {
		return event == null
				|| EventSeries.SERIES_CAL_ID.equals(event.getKey().getType());
	}

	@Override
	public boolean isRecurringFor(Integer monthOffset) {
		if (event instanceof EventSeries) {
			if (monthOffset == 0) {
				monthOffset = null;
			}
			final Integer offset = ((EventSeries) event).getWeekOfMonthOffset();
			return (offset == monthOffset)
					|| (offset != null && offset.equals(monthOffset));
		}
		return false;
	}

	@Override
	public boolean isMonday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isMonday();
		}
		return false;
	}

	@Override
	public boolean isTuesday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isTuesday();
		}
		return false;
	}

	@Override
	public boolean isWednesday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isWednesday();
		}
		return false;
	}

	@Override
	public boolean isThursday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isThursday();
		}
		return false;
	}

	@Override
	public boolean isFriday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isFriday();
		}
		return false;
	}

	@Override
	public boolean isSaturday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isSaturday();
		}
		return false;
	}

	@Override
	public boolean isSunday() {
		if (event instanceof EventSeries) {
			return ((EventSeries) event).isSunday();
		}
		return false;
	}

	@Override
	public String getCalendarType() {
		return calendarType == null ? null : calendarType.name();
	}

	@Override
	public void setCalendarType(CalendarType type) {
		this.calendarType = type;
	}

	@Override
	public boolean isRecurrencyForced() {
		return calendarType != CalendarType.EVENT;
	}

	@Override
	public boolean isNamed() {
		return calendarType != CalendarType.OPENING;
	}

	@Override
	public boolean isRelocalizable() {
		return calendarType == CalendarType.EVENT || calendarType == null;
	}

	@Override
	public String getTitle() {
		try {
			return messageSource.getMessage("event.form.title"
					+ (calendarType == null ? "" : "." + calendarType.name()),
					null, locale);
		} catch (NoSuchMessageException e) {
			LOGGER.error("Message not found for edit dialog title : "
					+ e.getMessage());
			return messageSource.getMessage("event.form.title", null, locale);
		}
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getDeleteEventUrl() {
		return urlService.getEventDeletionUrl(event);
	}
}
