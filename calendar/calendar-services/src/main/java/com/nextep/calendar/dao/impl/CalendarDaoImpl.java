package com.nextep.calendar.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.calendar.dao.CalendarDao;
import com.nextep.calendar.model.WeeklyCalendar;
import com.videopolis.calm.model.ItemKey;

public class CalendarDaoImpl extends AbstractCalDao<WeeklyCalendar> implements
		CalendarDao {

	private final static Log LOGGER = LogFactory.getLog(CalendarDaoImpl.class);

	@PersistenceContext(unitName = "nextep-calendar")
	private EntityManager entityManager;

	@Override
	public WeeklyCalendar getById(long id) {
		try {
			return (WeeklyCalendar) entityManager
					.createQuery("from WeeklyCalendarImpl where calendarId=:id")
					.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<WeeklyCalendar> getItemsFor(ItemKey key) {
		try {
			return entityManager
					.createQuery(
							"from WeeklyCalendarImpl where relatedItemKey=:itemKey")
					.setParameter("itemKey", key.toString()).getResultList();
		} catch (RuntimeException e) {
			LOGGER.error("Unable to retrieve calendar getItemsFor() for id "
					+ key.toString() + ": " + e.getMessage(), e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<ItemKey, List<WeeklyCalendar>> getCalendarsFor(
			List<ItemKey> keys, List<String> calendarTypes) {

		// Unwrapping item keys
		final Collection<String> keysStr = CalHelper.unwrapItemKeys(keys);

		// Preparing type restriction if needed
		String querySuffix = "";
		if (calendarTypes != null && !calendarTypes.isEmpty()) {
			querySuffix = " and calendarType in (:calendarTypes)";
		}

		// Building query
		final Query query = entityManager
				.createQuery("from WeeklyCalendarImpl where relatedItemKey in (:itemKeys)"
						+ querySuffix);
		query.setParameter("itemKeys", keysStr);

		if (calendarTypes != null && !calendarTypes.isEmpty()) {
			query.setParameter("calendarTypes", calendarTypes);
		}

		final List<WeeklyCalendar> calendars = query.getResultList();

		final Map<ItemKey, List<WeeklyCalendar>> calMap = new HashMap<ItemKey, List<WeeklyCalendar>>();
		for (WeeklyCalendar cal : calendars) {
			// Getting related item key
			final ItemKey relatedKey = cal.getRelatedItemKey();
			// Have we already got a list of calendar for this element ?
			List<WeeklyCalendar> itemCalendars = calMap.get(relatedKey);
			// If no we init the list
			if (itemCalendars == null) {
				itemCalendars = new ArrayList<WeeklyCalendar>();
				calMap.put(relatedKey, itemCalendars);
			}
			// Appending our calendar
			itemCalendars.add(cal);
		}
		return calMap;
	}

}
