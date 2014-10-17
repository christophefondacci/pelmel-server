package com.nextep.events.dao;

import java.util.List;
import java.util.Map;

import com.nextep.cal.util.model.CalDaoExt;
import com.nextep.events.model.EventSeries;
import com.videopolis.calm.model.ItemKey;

public interface EventSeriesDao extends CalDaoExt<EventSeries> {

	/**
	 * Fetches all event series for the given item keys in one single pass
	 * 
	 * @param itemKeys
	 *            list of {@link ItemKey} to get series for
	 * @return a map of the list of {@link EventSeries} hashed by their
	 *         corresponding keys
	 */
	Map<ItemKey, List<EventSeries>> getEventsSeriesFor(List<ItemKey> itemKeys);
}
