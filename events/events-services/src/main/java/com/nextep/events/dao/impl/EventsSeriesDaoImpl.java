package com.nextep.events.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.events.dao.EventSeriesDao;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventRequestTypes;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.impl.EventSeriesImpl;
import com.nextep.events.model.impl.ItemEventSeriesImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class EventsSeriesDaoImpl implements EventSeriesDao {

	private static final Log LOGGER = LogFactory
			.getLog(EventsSeriesDaoImpl.class);

	@PersistenceContext(unitName = "nextep-events")
	private EntityManager entityManager;

	@Override
	public EventSeries getById(long id) {
		return null;
	}

	@Override
	public List<EventSeries> getByIds(List<Long> idList) {
		return entityManager
				.createQuery("from EventSeriesImpl where seriesId in (:ids)")
				.setParameter("ids", idList).getResultList();
	}

	@Override
	public List<EventSeries> getItemsFor(ItemKey key) {
		return Collections.emptyList();
	}

	@Override
	public List<EventSeries> getItemsFor(ItemKey key, int resultsPerPage,
			int pageOffset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventSeries> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		return listItems(requestType, null, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EventSeries> listItems(RequestType requestType,
			Integer pageSize, Integer pageOffset) {
		String queryStr = "from EventSeriesImpl";
		if (requestType == EventRequestTypes.NEWEST_FIRST) {
			queryStr = queryStr + " order by lastUpdateTime desc";
		}
		final Query query = entityManager.createQuery(queryStr);
		if (pageSize != null && pageOffset != null) {
			query.setMaxResults(pageSize);
			query.setFirstResult(pageOffset);
		}
		return query.getResultList();
	}

	@Override
	public int getCount() {
		return ((BigInteger) entityManager.createNativeQuery(
				"select count(1) from EVENTS_SERIES").getSingleResult())
				.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<ItemKey, List<EventSeries>> getEventsSeriesFor(
			List<ItemKey> itemKeys) {
		final Collection<String> itemKeysStr = CalHelper
				.unwrapItemKeys(itemKeys);

		final List<EventSeries> eventSeries = entityManager
				.createQuery(
						"from EventSeriesImpl where placeKey in (:itemKeys)")
				.setParameter("itemKeys", itemKeysStr).getResultList();

		// Hashing result by key
		final Map<ItemKey, List<EventSeries>> seriesMap = new HashMap<ItemKey, List<EventSeries>>();
		for (EventSeries s : eventSeries) {
			final ItemKey locationKey = s.getLocationKey();
			// Retrieving list
			List<EventSeries> itemSeries = seriesMap.get(locationKey);
			// No list ? create one
			if (itemSeries == null) {
				itemSeries = new ArrayList<EventSeries>();
				seriesMap.put(locationKey, itemSeries);
			}
			itemSeries.add(s);
		}
		return seriesMap;
	}

	@Override
	public void save(CalmObject object) {
		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<ItemKey, List<EventSeries>> findItemEventFor(
			List<ItemKey> externalItems) throws CalException {

		final Collection<String> ids = CalHelper.unwrapItemKeys(externalItems);

		final List<Object[]> rows = entityManager
				.createQuery(
						"select itemEvent,event from ItemEventSeriesImpl as itemEvent, EventSeriesImpl as event "
								+ "where itemEvent.externalItemKey in (:extIds) and event.id=itemEvent.itemId ")
				.setParameter("extIds", ids).getResultList();

		Map<ItemKey, List<EventSeries>> eventSeriesMap = new HashMap<ItemKey, List<EventSeries>>();
		for (Object[] row : rows) {
			final ItemEventSeriesImpl itemEvent = (ItemEventSeriesImpl) row[0];
			final EventSeriesImpl eventSeries = (EventSeriesImpl) row[1];

			final ItemKey extItemKey = CalmFactory.parseKey(itemEvent
					.getExternalItemKey());
			List<EventSeries> seriesList = eventSeriesMap.get(extItemKey);
			if (seriesList == null) {
				seriesList = new ArrayList<EventSeries>();

				eventSeriesMap.put(extItemKey, seriesList);
			}
			seriesList.add(eventSeries);
		}
		return eventSeriesMap;
	}

	@SuppressWarnings("unchecked")
	public List<ItemEventSeriesImpl> findItemEventFor(ItemKey externalItem) {
		final StringBuilder buf = new StringBuilder();
		buf.append("select itemEvent from ItemEventSeriesImpl as itemEvent, EventSeriesImpl as event "
				+ "where itemEvent.externalItemKey=:extId and event.id=itemEvent.itemId ");
		return entityManager.createQuery(buf.toString())
				.setParameter("extId", externalItem.toString()).getResultList();
	}

	@Override
	public List<Event> bindEventsSeries(ItemKey externalItem,
			List<ItemKey> eventKeys) {
		// Looking for any pre-existing event definition for this external item
		List<ItemEventSeriesImpl> itemEvents = findItemEventFor(externalItem);
		// Hashing the found events by event key for fast lookup
		final Map<ItemKey, ItemEventSeriesImpl> itemEventsMap = new HashMap<ItemKey, ItemEventSeriesImpl>();
		for (ItemEventSeriesImpl itemEvent : itemEvents) {
			try {
				final ItemKey eventKey = CalmFactory.createKey(
						EventSeries.SERIES_CAL_ID, itemEvent.getItemId());
				itemEventsMap.put(eventKey, itemEvent);
			} catch (CalException e) {
				LOGGER.error("Exception: " + e.getMessage(), e);
			}
		}
		// Processing new places
		final List<Long> eventIds = new ArrayList<Long>();
		final List<Event> allEvents = new ArrayList<Event>();
		for (ItemKey eventKey : eventKeys) {
			final ItemEventSeriesImpl existingItemEvent = itemEventsMap
					.get(eventKey);
			if (existingItemEvent == null) {
				// If not existing we load the event
				eventIds.add(eventKey.getNumericId());
			} else {
				// If existing we remove association
				entityManager.remove(existingItemEvent);
				eventIds.remove(eventKey.getNumericId());
			}
		}
		// Registering new events
		if (!eventIds.isEmpty()) {
			final List<EventSeries> events = getByIds(eventIds);
			for (EventSeries p : events) {
				// Setting up new association
				final ItemEventSeriesImpl itemPlace = new ItemEventSeriesImpl(
						externalItem, p.getKey());
				entityManager.persist(itemPlace);
			}
			allEvents.addAll(events);
		}
		return allEvents;
	}

	@Override
	public void delete(ItemKey itemKey) {
		entityManager
				.createQuery("delete ItemEventSeriesImpl where itemId=:eventId")
				.setParameter("eventId", itemKey.getNumericId())
				.executeUpdate();
		entityManager
				.createQuery("delete EventSeriesImpl where seriesId=:seriesId")
				.setParameter("seriesId", itemKey.getNumericId())
				.executeUpdate();

	}
}
