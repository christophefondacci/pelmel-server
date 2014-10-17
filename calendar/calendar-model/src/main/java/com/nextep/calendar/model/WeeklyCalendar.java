package com.nextep.calendar.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * A calendar represents hours of operations within a week. It defines the days
 * where applies a same time range (start / end time).<br>
 * It is not specific to anything and could represents opening hours, happy
 * hours, rush hours, etc.
 * 
 * @author cfondacci
 * 
 */
public interface WeeklyCalendar extends CalmObject {
	String CAL_ID = "CLDR";

	/**
	 * When this calendar becomes valid
	 * 
	 * @return the date when this calendar is valid
	 */
	Date getStartDate();

	/**
	 * The end date of the calendar, included
	 * 
	 * @return the end date of this calendar's validity
	 */
	Date getEndDate();

	/**
	 * Enabled for mondays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isMonday();

	/**
	 * Enabled for tuesdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isTuesday();

	/**
	 * Enabled for wednesdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isWednesday();

	/**
	 * Enabled for thursdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isThursday();

	/**
	 * Enabled for fridays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isFriday();

	/**
	 * Enabled for saturdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isSaturday();

	/**
	 * Enabled for sundays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isSunday();

	/**
	 * Start hour of the calendar
	 * 
	 * @return the start hour, valid for every active day
	 */
	int getStartHour();

	/**
	 * Start minute of the calendar
	 * 
	 * @return the start minutes, valid for every active day
	 */
	int getStartMinute();

	/**
	 * End hour of the calendar
	 * 
	 * @return the end hour, valid for every active day
	 */
	int getEndHour();

	/**
	 * End minute of the calendar
	 * 
	 * @return the end minutes, valid for every active day
	 */
	int getEndMinute();

	/**
	 * A free-format calendar typology so that parent beans could retrieve
	 * various kind of calendars registered under different types.
	 * 
	 * @return the calendar type
	 */
	String getCalendarType();

	/**
	 * Provides the {@link ItemKey} of the element to which this calendar is
	 * associated
	 * 
	 * @return the parent element's {@link ItemKey}
	 */
	ItemKey getRelatedItemKey();
}
