package com.nextep.calendar.model;

import com.videopolis.calm.model.ItemKey;

/**
 * Mutable accessors for {@link WeeklyCalendar} information
 * 
 * @author cfondacci
 * 
 */
public interface MutableWeeklyCalendar extends WeeklyCalendar {

	/**
	 * Defines whether this calendar is active on mondays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setMonday(boolean isMonday);

	/**
	 * Defines whether this calendar is active on tuesdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setTuesday(boolean isTuesday);

	/**
	 * Defines whether this calendar is active on wednesdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setWednesday(boolean isWednesday);

	/**
	 * Defines whether this calendar is active on thursdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setThursday(boolean isThursday);

	/**
	 * Defines whether this calendar is active on fridays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setFriday(boolean isFriday);

	/**
	 * Defines whether this calendar is active on saturdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setSaturday(boolean isSaturday);

	/**
	 * Defines whether this calendar is active on sundays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setSunday(boolean isSunday);

	/**
	 * Sets the hour from when this event starts
	 * 
	 * @param startHour
	 *            starting hour for the event
	 */
	void setStartHour(int startHour);

	/**
	 * Sets the minute from when this event starts
	 * 
	 * @param startMinute
	 *            starting minute for the event
	 */
	void setStartMinute(int startMinute);

	/**
	 * Sets the hour till when this event lasts
	 * 
	 * @param startHour
	 *            ending hour for the event
	 */
	void setEndHour(int endHour);

	/**
	 * Sets the minute till when this event lasts
	 * 
	 * @param startMinute
	 *            ending minute for the event
	 */
	void setEndMinute(int endMinute);

	/**
	 * Defines the type of calendar
	 * 
	 * @param type
	 *            the type of calendar
	 */
	void setCalendarType(String type);

	/**
	 * Associates this calendar with the parent element defined by its unique
	 * {@link ItemKey}.
	 * 
	 * @param parentItemKey
	 *            the {@link ItemKey} of the parent element to associate this
	 *            calendar to
	 */
	void setRelatedItemKey(ItemKey parentItemKey);
}
