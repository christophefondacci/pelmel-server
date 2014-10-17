package com.nextep.events.model;

import java.util.Date;

/**
 * An event series is the definition of recurring events with the rules that
 * should be used to compute every event date.
 * 
 * @author cfondacci
 * 
 */
public interface EventSeries extends Event {

	String SERIES_CAL_ID = "SERI";

	/**
	 * When this series should start to generate events
	 * 
	 * @return the date when this series is enabled
	 */
	@Override
	Date getStartDate();

	/**
	 * The date of the last event of the series. Beyond this date, no further
	 * events will be created from this series.
	 * 
	 * @return the date of last event of this series
	 */
	@Override
	Date getEndDate();

	/**
	 * A week offset allowing to define rules like
	 * "every 1st saturday of month". This information should be null for rules
	 * like "every saturday". If set, then the event could only take place once
	 * a month.
	 * 
	 * @return the week offset in the month to use for in-month positioning
	 */
	Integer getWeekOfMonthOffset();

	/**
	 * Series events takes place on mondays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isMonday();

	/**
	 * Series events takes place on tuesdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isTuesday();

	/**
	 * Series events takes place on wednesdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isWednesday();

	/**
	 * Series events takes place on thursdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isThursday();

	/**
	 * Series events takes place on fridays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isFriday();

	/**
	 * Series events takes place on saturdays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isSaturday();

	/**
	 * Series events takes place on sundays ?
	 * 
	 * @return <code>true</code> if it does
	 */
	boolean isSunday();

	/**
	 * Start hour of the event
	 * 
	 * @return the event's start hour
	 */
	int getStartHour();

	/**
	 * Start minute of the event
	 * 
	 * @return the event's start minute
	 */
	int getStartMinute();

	/**
	 * End hour of the event
	 * 
	 * @return the event's end hour
	 */
	int getEndHour();

	/**
	 * End minute of the event
	 * 
	 * @return the event's end minute
	 */
	int getEndMinute();

	/**
	 * Provides the type of event
	 * 
	 * @return
	 */
	CalendarType getCalendarType();
}
