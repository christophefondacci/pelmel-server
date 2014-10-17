package com.nextep.calendar.dao;

import java.util.List;
import java.util.Map;

import com.nextep.cal.util.model.CalDao;
import com.nextep.calendar.model.WeeklyCalendar;
import com.videopolis.calm.model.ItemKey;

public interface CalendarDao extends CalDao<WeeklyCalendar> {

	Map<ItemKey, List<WeeklyCalendar>> getCalendarsFor(List<ItemKey> keys,
			List<String> calendarTypes);
}
