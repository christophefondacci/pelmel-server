package com.nextep.calendar.services.impl;

import java.util.List;
import java.util.Map;

import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.calendar.dao.CalendarDao;
import com.nextep.calendar.model.CalendarTypeRequestType;
import com.nextep.calendar.model.WeeklyCalendar;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class CalendarServiceImpl extends AbstractDaoBasedCalServiceImpl {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return WeeklyCalendar.class;
	}

	@Override
	public String getProvidedType() {
		return WeeklyCalendar.CAL_ID;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {
		List<String> calendarTypes = null;
		if (requestType instanceof CalendarTypeRequestType) {
			calendarTypes = ((CalendarTypeRequestType) requestType)
					.getRequestedCalendarTypes();
		}

		CalendarDao dao = (CalendarDao) getCalDao();

		// Retrieving calendars
		Map<ItemKey, List<WeeklyCalendar>> calMap = dao.getCalendarsFor(
				itemKeys, calendarTypes);

		// Preparing response
		MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		// Filling response from map
		for (ItemKey itemKey : calMap.keySet()) {
			final List<WeeklyCalendar> calendars = calMap.get(itemKey);
			response.setItemsFor(itemKey, calendars);
		}
		// Returning result
		return response;
	}

}
