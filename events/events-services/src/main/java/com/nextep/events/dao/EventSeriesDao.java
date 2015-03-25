package com.nextep.events.dao;

import java.util.List;
import java.util.Map;

import com.nextep.cal.util.model.CalDaoExt;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.videopolis.calm.exception.CalException;
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

	/**
	 * Deletes an {@link EventSeries} from the database
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the series to remove
	 */
	void delete(ItemKey itemKey);

	/**
	 * Binds the specified events to the given external item.
	 * 
	 * @param externalItem
	 *            the item to associate with event
	 * @param placeKeys
	 *            the {@link ItemKey} of {@link Event}s to associate
	 * @return the list of associated {@link Event}
	 */
	List<Event> bindEventsSeries(ItemKey externalItem, List<ItemKey> eventKey);

	public Map<ItemKey, List<EventSeries>> findItemEventFor(
			List<ItemKey> externalItems) throws CalException;
}
