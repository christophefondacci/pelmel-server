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

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.events.dao.EventSeriesDao;
import com.nextep.events.model.EventSeries;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class EventsSeriesDaoImpl implements EventSeriesDao {

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
		final Query query = entityManager.createQuery("from EventSeriesImpl");
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
	public void delete(ItemKey itemKey) {

		entityManager
				.createQuery("delete EventSeriesImpl where seriesId=:seriesId")
				.setParameter("seriesId", itemKey.getNumericId())
				.executeUpdate();

	}
}
