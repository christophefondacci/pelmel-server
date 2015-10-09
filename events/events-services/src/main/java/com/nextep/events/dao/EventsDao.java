package com.nextep.events.dao;

import java.util.List;

import com.nextep.cal.util.model.CalDaoExt;
import com.nextep.events.model.Event;
import com.videopolis.calm.model.ItemKey;

public interface EventsDao extends CalDaoExt<Event> {

	/**
	 * Binds the specified events to the given external item.
	 * 
	 * @param externalItem
	 *            the item to associate with event
	 * @param placeKeys
	 *            the {@link ItemKey} of {@link Event}s to associate
	 * @return the list of associated {@link Event}
	 */
	List<Event> bindEvents(ItemKey externalItem, List<ItemKey> eventKey);

	List<Event> getAllItemsFor(ItemKey key);

	void delete(ItemKey key);

	List<Event> getEventsFromFacebook(List<ItemKey> facebookKeys);
}
