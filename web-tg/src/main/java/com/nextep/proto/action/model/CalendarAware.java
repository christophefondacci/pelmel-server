package com.nextep.proto.action.model;

import com.nextep.proto.blocks.CalendarSupport;

/**
 * This interface defines action that could have support for calendars like
 * opening hours, happy hours, etc.
 * 
 * @author cfondacci
 * 
 */
public interface CalendarAware {

	/**
	 * Retrieves the installed support implementation
	 * 
	 * @returnthe {@link CalendarSupport} implementation installed
	 */
	CalendarSupport getCalendarSupport();

	/**
	 * Installs the support for calendars on the current element
	 * 
	 * @param calendarSupport
	 *            the {@link CalendarSupport} implementation to install
	 */
	void setCalendarSupport(CalendarSupport calendarSupport);
}
