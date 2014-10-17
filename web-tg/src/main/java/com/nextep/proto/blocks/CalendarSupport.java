package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.events.model.CalendarType;
import com.nextep.events.model.EventSeries;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

/**
 * Provides support for calendars like opening hours or happy hours.
 * 
 * @author cfondacci
 * 
 */
public interface CalendarSupport {

	/**
	 * Initializes the support with the list of all series registered for an
	 * element
	 * 
	 * @param seriesList
	 *            the list of all existing calendars
	 * @param parent
	 *            the parent {@link CalmObject}
	 * @param locale
	 *            the current {@link Locale} for translations
	 */
	void initialize(List<? extends EventSeries> seriesList, CalmObject parent,
			UrlService urlService, Locale locale);

	/**
	 * Lists all calendar types to display
	 * 
	 * @return the list of {@link CalendarType} to display
	 */
	List<CalendarType> getTypes();

	/**
	 * Provides the label for a given calendar type
	 * 
	 * @param calendarType
	 *            the {@link CalendarType} to get the label for
	 * @return the label of this type of calendar
	 */
	String getCalendarTypeLabel(CalendarType calendarType);

	/**
	 * Provides all calendars registered under this specific type of calendar
	 * 
	 * @param calendarType
	 *            the {@link CalendarType} to get hours for
	 * @return the list of recurring events as {@link EventSeries}
	 */
	List<?> getCalendarsFor(CalendarType calendarType);

	/**
	 * Provides the URL that can edit the provided event series
	 * 
	 * @param series
	 *            the {@link EventSeries} to get edit URL for
	 * @return the URL of the edit form for this event series
	 */
	String getEditUrl(Object series);

	/**
	 * Provides the label to display when rendering this event series
	 * 
	 * @param series
	 *            the {@link EventSeries} to display
	 * @return the label of this event series
	 */
	String getLabel(Object series);

	/**
	 * Provides the URL of the form that could add a calendar of the specified
	 * type to the parent element
	 * 
	 * @param calendarType
	 *            the {@link CalendarType} to create
	 * @return the URL of the addition form for this type of calendar on the
	 *         current parent object
	 */
	String getAddCalendarUrl(CalendarType calendarType);

	/**
	 * Provides the label to display for the next occurrence of the given
	 * calendar type. Will return <code>null</code> when no occurrence of this
	 * type could be computed. This method will typically return something like
	 * "OPENS IN 1h"
	 * 
	 * @param calendarType
	 *            the {@link CalendarType} to compute next occurrence for
	 * @return the label
	 */
	String getNextTimeLabel(CalendarType calendarType);

	/**
	 * Provides the subtitle to display on the label for the next occurrence of
	 * the given calendar type. Will return <code>null</code> when no occurrence
	 * could be computed for this calendar type
	 * 
	 * @param calendarType
	 *            the {@link CalendarType} to compute the subtitle for
	 * @return the subtitle to display for next occurrence
	 */
	String getNextTimeSubtitle(CalendarType calendarType);

	/**
	 * The CSS class to use when displaying next occurrence.
	 * 
	 * @param calendarType
	 *            the {@link CalendarType} to display
	 * @return the name of a CSS class to apply
	 */
	String getNextTimeCSSClass(CalendarType calendarType);
}
