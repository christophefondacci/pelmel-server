package com.nextep.proto.services;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.properties.model.Property;

public interface EventManagementService {

	void refreshEventSeries(EventSeries series);

	Event createEventFromSeries(EventSeries series, Date startDate, Date endDate);

	/**
	 * Computes the next date of the given series starting from the given start
	 * date.
	 * 
	 * @param series
	 *            the {@link EventSeries} containing the calendar rules
	 * @param startDate
	 *            the current start date to generate from
	 * @param isStart
	 *            whether we need to compute the next start or the next end time
	 *            of the series
	 * @return the next date, which will always happen after startDate
	 */
	Date computeNextStart(EventSeries series, Date startDate, boolean isStart);

	/**
	 * Computes the next date of an event series from "now" located in the
	 * provided timezone. Since series are defined by days of week and hours of
	 * days, it is mandatory to put ourselves in the local time before computing
	 * next date
	 * 
	 * @param series
	 *            the {@link EventSeries} to compute a date for
	 * @param timezoneId
	 *            the timezone of the computation
	 * @param isStart
	 *            whether we compute the start or end date of the series
	 * @return the next requested date
	 */
	Date computeNext(EventSeries series, String timezoneId, boolean isStart);

	/**
	 * Provides the "now" time in the given timezone as this is critical for
	 * proper event series computation
	 * 
	 * @param timezoneId
	 *            the timezone id
	 * @return the "now" date
	 */
	Date getLocalizedNow(String timezoneId);

	/**
	 * Converts a date from one timezone to another
	 * 
	 * @param date
	 *            the {@link Date} to convert
	 * @param fromTimezoneId
	 *            the ID of the timezone in which the date is expressed
	 * @param toTimezoneId
	 *            the ID of the timezone to convert the date to
	 * @return the converted date
	 */
	Date convertDate(Date date, String fromTimezoneId, String toTimezoneId);

	/**
	 * Builds a readable string expressing the given event series
	 * 
	 * @param series
	 *            the {@link EventSeries} to compute expression for
	 * @param l
	 *            the {@link Locale} for text translation
	 * @return a readable string like 'mon/tue/fri: 5pm-8pm' or 'mon-fri:
	 *         6pm-2am'
	 */
	String buildReadableTimeframe(EventSeries series, Locale l);

	/**
	 * Converts the list of {@link EventSeries} to a list of human-readable
	 * properties
	 * 
	 * @param seriesList
	 *            list of {@link EventSeries} (all series may not be converted
	 * @param l
	 *            current locale
	 * @return the list of {@link Property}
	 */
	List<Property> convertToProperties(List<EventSeries> seriesList, Locale l);

	/**
	 * Provides the timezone of the event
	 * 
	 * @param event
	 *            the {@link Event} to get the timezone
	 * @return the timezone ID
	 */
	String getEventTimezoneId(Event event);
}
