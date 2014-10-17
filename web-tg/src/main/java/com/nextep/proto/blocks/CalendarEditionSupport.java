package com.nextep.proto.blocks;

public interface CalendarEditionSupport {

	/**
	 * Provides the event's end date as displayed in the input text field
	 * 
	 * @return the formatted end date
	 */
	String getEndDate();

	String getEndHour();

	String getEndMinute();

	/**
	 * Provides the event's start date as displayed in the input text field
	 * 
	 * @return the formatted start date
	 */
	String getStartDate();

	String getStartHour();

	String getStartMinute();

	boolean isFriday();

	boolean isMonday();

	boolean isSaturday();

	boolean isSunday();

	boolean isThursday();

	boolean isTuesday();

	boolean isWednesday();

}
