package com.nextep.events.services.impl;

import java.util.Arrays;
import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.events.dao.EventsDao;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventRequestTypes;
import com.nextep.events.model.impl.EventImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;

public class EventsServiceImpl extends AbstractDaoBasedCalServiceImpl implements
		CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Event.class;
	}

	@Override
	public String getProvidedType() {
		return Event.CAL_ID;
	}

	@Override
	public CalmObject createTransientObject() {
		return new EventImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	
	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context,
			RequestType requestType) throws CalException {
		if(requestType==EventRequestTypes.ALL_EVENTS) {
			final List<Event> events = ((EventsDao)getCalDao()).getAllItemsFor(itemKey);
			final ItemsResponseImpl response = new ItemsResponseImpl();
			response.setItems(events);
			return response;
		} else {
			return getItemsFor(itemKey, context);
		}
	}
	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		Assert.notNull(contributedItemKey,
				"Cannot define Event association with null item key");
		Assert.notNull(internalItemKeys,
				"Cannot define Event association with no events");
		Assert.moreThan(internalItemKeys.length, 0,
				"Cannot define Event association with empty events");
		return ((EventsDao) getCalDao()).bindEvents(contributedItemKey,
				Arrays.asList(internalItemKeys));
	}
}
