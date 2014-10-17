package com.nextep.proto.blocks;

import java.util.Locale;

import com.nextep.events.model.CalendarType;
import com.videopolis.calm.model.CalmObject;

public interface EventEditionSupport extends CalendarEditionSupport {

	void initialize(CalmObject eventOrLocation, Locale locale);

	/**
	 * Provides the event ID
	 * 
	 * @return the event id or null if new event
	 */
	String getEventId();

	/**
	 * Provides the name of the event
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Provides the place ID where the event takes place
	 * 
	 * @return the place id
	 */
	String getPlaceId();

	/**
	 * Provides the place name where the event takes place. This is a
	 * user-friendly name that is displayed to the user.
	 * 
	 * @return
	 */
	String getPlaceName();

	boolean isSeriesEnabled();

	boolean isRecurringFor(Integer monthOffset);

	/**
	 * Provides the calendar type code to be used for edition
	 * 
	 * @return the current {@link CalendarType}
	 */
	String getCalendarType();

	/**
	 * Forces a calendar type for edition
	 * 
	 * @param type
	 *            the {@link CalendarType} to use
	 */
	void setCalendarType(CalendarType type);

	/**
	 * Whether or not the recurrency type should be shown
	 * 
	 * @return <code>true</code> when recurrency selection should not be shown,
	 *         else <code>false</code>
	 */
	boolean isRecurrencyForced();

	/**
	 * Whether or not the title should be displayed
	 * 
	 * @return <code>true</code> to show name input, else <code>false</code>
	 */
	boolean isNamed();

	/**
	 * Whether or not the user can change the localization of this event
	 * 
	 * @return <code>true</code> to show the localization "where" input,else
	 *         <code>false</code>
	 */
	boolean isRelocalizable();

	/**
	 * Provides the title to display on the edition dialog
	 * 
	 * @return the title to display on the edition dialog
	 */
	String getTitle();
}
