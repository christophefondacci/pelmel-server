package com.nextep.events.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.events.dao.EventSeriesDao;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.impl.EventSeriesImpl;
import com.videopolis.calm.exception.CalException;
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
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		return Collections.emptyList();
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {

		// Querying event series for given list of keys
		final Map<ItemKey, List<EventSeries>> seriesMap = ((EventSeriesDao) getCalDao())
				.getEventsSeriesFor(itemKeys);

		// Preparing response
		MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (ItemKey key : seriesMap.keySet()) {
			final List<EventSeries> seriesList = seriesMap.get(key);
			response.setItemsFor(key, seriesList);
		}

		// Response is ready
		return response;
	}

	@Override
	public void delete(ItemKey itemKey) {
		((EventSeriesDao) getCalDao()).delete(itemKey);
	}
}
