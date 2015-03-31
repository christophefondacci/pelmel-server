package com.nextep.events.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.dao.EventsDao;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.impl.ItemEventImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class EventsDaoImpl implements EventsDao {

	private static final Log LOGGER = LogFactory.getLog(EventsDaoImpl.class);

	@PersistenceContext(unitName = "nextep-events")
	private EntityManager entityManager;

	@Override
	public Event getById(long id) {
		return (Event) entityManager.createQuery("from EventImpl where id=:id")
				.setParameter("id", id).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getByIds(final List<Long> idList) {
		List<Event> objList = entityManager
				.createQuery("from EventImpl where id in (:ids)")
				.setParameter("ids", idList).getResultList();
		// Reordering list according to initial input to preserve order (CAL
		// Contract)
		Collections.sort(objList, new Comparator<CalmObject>() {
			@Override
			public int compare(CalmObject o1, CalmObject o2) {
				return idList.indexOf(o1.getKey().getNumericId())
						- idList.indexOf(o2.getKey().getNumericId());
			}
		});
		return objList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getItemsFor(ItemKey key) {
		if ("PLAC".equals(key.getType()) || "CITY".equals(key.getType())) {
			return entityManager
					.createQuery(
							"from EventImpl where placeKey=:placeKey and endDate > CURRENT_TIMESTAMP and isOnline=true order by startDate")
					.setParameter("placeKey", key.toString()).getResultList();
		} else if (EventSeries.SERIES_CAL_ID.equals(key.getType())) {
			return entityManager
					.createQuery(
							"from EventImpl where seriesKey=:seriesKey and endDate > CURRENT_TIMESTAMP and isOnline=true order by startDate")
					.setParameter("seriesKey", key.toString()).getResultList();
		} else {
			final List<ItemEventImpl> itemEvents = findItemEventFor(key, true);
			return buildEvents(itemEvents);
		}
	}

	private List<Event> buildEvents(List<ItemEventImpl> itemEvents) {
		final List<Long> eventIds = new ArrayList<Long>();
		for (ItemEventImpl itemEvent : itemEvents) {
			eventIds.add(Long.valueOf(itemEvent.getItemId()));
		}
		if (!eventIds.isEmpty()) {
			return getByIds(eventIds);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<Event> getAllItemsFor(ItemKey key) {
		final List<ItemEventImpl> itemEvents = findItemEventFor(key, false);
		return buildEvents(itemEvents);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> getItemsFor(ItemKey key, int resultsPerPage,
			int pageOffset) {
		return entityManager
				.createQuery(
						"from EventImpl where placeKey=:placeKey and endDate > CURRENT_TIMESTAMP and isOnline=true order by startDate")
				.setParameter("placeKey", key.toString())
				.setFirstResult(pageOffset * resultsPerPage)
				.setMaxResults(resultsPerPage).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		return entityManager.createQuery("from EventImpl where isOnline=true")
				.getResultList();
	}

	@Override
	public void save(CalmObject event) {
		if (event.getKey() == null) {
			entityManager.persist(event);
		} else {
			entityManager.merge(event);
		}
	}

	@SuppressWarnings("unchecked")
	public List<ItemEventImpl> findItemEventFor(ItemKey externalItem,
			boolean futureEventsOnly) {
		final StringBuilder buf = new StringBuilder();
		buf.append("select itemEvent from ItemEventImpl as itemEvent, EventImpl as event "
				+ "where itemEvent.externalItemKey=:extId and event.id=itemEvent.itemId ");
		if (futureEventsOnly) {
			buf.append("and event.endDate > CURRENT_TIMESTAMP ");
		}
		buf.append("order by event.startDate");
		return entityManager.createQuery(buf.toString())
				.setParameter("extId", externalItem.toString()).getResultList();
	}

	@Override
	public List<Event> bindEvents(ItemKey externalItem, List<ItemKey> eventKeys) {
		// Looking for any pre-existing event definition for this external item
		List<ItemEventImpl> itemEvents = findItemEventFor(externalItem, false);
		// Hashing the found events by event key for fast lookup
		final Map<ItemKey, ItemEventImpl> itemEventsMap = new HashMap<ItemKey, ItemEventImpl>();
		for (ItemEventImpl itemEvent : itemEvents) {
			try {
				final ItemKey eventKey = CalmFactory.createKey(Event.CAL_ID,
						itemEvent.getItemId());
				itemEventsMap.put(eventKey, itemEvent);
			} catch (CalException e) {
				LOGGER.error("Exception: " + e.getMessage(), e);
			}
		}
		// Processing new places
		final List<Long> eventIds = new ArrayList<Long>();
		final List<Event> allEvents = new ArrayList<Event>();
		for (ItemKey eventKey : eventKeys) {
			final ItemEventImpl existingItemEvent = itemEventsMap.get(eventKey);
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
			final List<Event> events = getByIds(eventIds);
			for (Event p : events) {
				// Setting up new association
				final ItemEventImpl itemPlace = new ItemEventImpl(externalItem,
						p.getKey());
				entityManager.persist(itemPlace);
			}
			allEvents.addAll(events);
		}
		return allEvents;
	}

	@Override
	public void delete(ItemKey itemKey) {
		// entityManager.createQuery("delete ItemEventImpl where itemId=:eventId")
		// .setParameter("eventId", itemKey.getNumericId())
		// .executeUpdate();
		entityManager
				.createQuery(
						"update EventImpl set isOnline=false where id=:eventId")
				.setParameter("eventId", itemKey.getNumericId())
				.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Event> listItems(RequestType requestType, Integer pageSize,
			Integer pageOffset) {
		return entityManager
				.createQuery("from EventImpl order by lastUpdateTime desc")
				.setFirstResult(pageOffset).setMaxResults(pageSize)
				.getResultList();
	}

	@Override
	public int getCount() {
		return ((BigInteger) entityManager.createNativeQuery(
				"select count(1) from EVENTS").getSingleResult()).intValue();
	}
}
