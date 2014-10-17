package com.nextep.calendar.model;

import java.util.List;

import com.videopolis.calm.model.RequestType;

/**
 * Request type to query specific calendar type(s)
 * 
 * @author cfondacci
 * 
 */
public interface CalendarTypeRequestType extends RequestType {

	List<String> getRequestedCalendarTypes();
}
