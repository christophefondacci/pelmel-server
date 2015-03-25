package com.nextep.events.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.events.dao.EventSeriesDao;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.MutableEventSeries;
import com.nextep.events.model.impl.EventSeriesImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class EventSeriesServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalExtendedPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return EventSeries.class;
	}

	@Override
	public String getProvidedType() {
		return EventSeries.SERIES_CAL_ID;
	}

	@Override
	public CalmObject createTransientObject() {
		return new EventSeriesImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		((MutableEventSeries) object).setLastUpdateTime(new Date());
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		Assert.notNull(contributedItemKey,
				"Cannot define EventSeries association with null item key");
		Assert.notNull(internalItemKeys,
				"Cannot define EventSeries association with no events");
		Assert.moreThan(internalItemKeys.length, 0,
				"Cannot define EventSeries association with empty events");
		return ((EventSeriesDao) getCalDao()).bindEventsSeries(
				contributedItemKey, Arrays.asList(internalItemKeys));
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {

		// Extracting user keys
		final List<ItemKey> userKeys = new ArrayList<ItemKey>();
		final List<ItemKey> otherKeys = new ArrayList<ItemKey>(itemKeys);
		for (ItemKey itemKey : itemKeys) {
			if ("USER".equals(itemKey.getType())) {
				userKeys.add(itemKey);
				otherKeys.remove(itemKey);
			}
		}

		// Preparing response
		MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		final EventSeriesDao dao = (EventSeriesDao) getCalDao();

		// Internal GEO keys
		if (!otherKeys.isEmpty()) {
			// Querying event series for given list of keys
			final Map<ItemKey, List<EventSeries>> seriesMap = dao
					.getEventsSeriesFor(otherKeys);

			// Filling response
			for (ItemKey key : seriesMap.keySet()) {
				final List<EventSeries> seriesList = seriesMap.get(key);
				response.setItemsFor(key, seriesList);
			}
		}
		// Querying users
		if (!userKeys.isEmpty()) {
			final Map<ItemKey, List<EventSeries>> userSeriesMap = dao
					.findItemEventFor(userKeys);
			for (ItemKey key : userSeriesMap.keySet()) {
				final List<EventSeries> seriesList = userSeriesMap.get(key);
				response.setItemsFor(key, seriesList);
			}
		}
		// Response is ready
		return response;
	}

	@Override
	public void delete(ItemKey itemKey) {
		((EventSeriesDao) getCalDao()).delete(itemKey);
	}
}
