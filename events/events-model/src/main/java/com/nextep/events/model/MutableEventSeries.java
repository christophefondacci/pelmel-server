package com.nextep.events.model;

public interface MutableEventSeries extends MutableEvent, EventSeries {

	/**
	 * Sets the offset that can position the event in a month.
	 * 
	 * 
	 * @param offset
	 */
	void setWeekOfMonthOffset(Integer offset);

	/**
	 * Defines whether this series takes place on mondays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setMonday(boolean isMonday);

	/**
	 * Defines whether this series takes place on tuesdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setTuesday(boolean isTuesday);

	/**
	 * Defines whether this series takes place on wednesdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setWednesday(boolean isWednesday);

	/**
	 * Defines whether this series takes place on thursdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setThursday(boolean isThursday);

	/**
	 * Defines whether this series takes place on fridays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setFriday(boolean isFriday);

	/**
	 * Defines whether this series takes place on saturdays
	 * 
	 * @param isMonday
	 *            <code>true</code> if it does, else <code>false</code>
	 */
	void setSaturday(boolean isSaturday);

	/**
	 * Defines whether this series takes place on sundays
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
	 * @param calendarType
	 */
	void setCalendarType(CalendarType calendarType);
}
