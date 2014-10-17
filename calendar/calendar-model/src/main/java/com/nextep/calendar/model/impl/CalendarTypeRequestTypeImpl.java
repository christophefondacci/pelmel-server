package com.nextep.calendar.model.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.nextep.calendar.model.CalendarTypeRequestType;

public class CalendarTypeRequestTypeImpl implements CalendarTypeRequestType {

	private static final long serialVersionUID = -4008444624025277321L;
	private List<String> calendarTypes = Collections.emptyList();

	public CalendarTypeRequestTypeImpl(String... calendarTypes) {
		this.calendarTypes = Arrays.asList(calendarTypes);
	}

	@Override
	public List<String> getRequestedCalendarTypes() {
		return calendarTypes;
	}

}
