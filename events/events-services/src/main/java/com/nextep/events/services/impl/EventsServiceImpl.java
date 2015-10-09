package com.nextep.events.services.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.events.dao.EventsDao;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventRequestTypes;
import com.nextep.events.model.MutableEvent;
import com.nextep.events.model.impl.EventImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;

public class EventsServiceImpl extends AbstractDaoBasedCalServiceImpl implements CalExtendedPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Event.class;
	}

	@Override
	public String getProvidedType() {
		return Event.CAL_ID;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Arrays.asList(Event.CAL_ID, Event.CAL_ID_FB);
	}

	@Override
	public CalmObject createTransientObject() {
		return new EventImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		((MutableEvent) object).setLastUpdateTime(new Date());
		getCalDao().save(object);
	}

	@Override
	public void delete(ItemKey objectKey) {
		((EventsDao) getCalDao()).delete(objectKey);
	}

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context) throws CalException {
		if (ids != null && !ids.isEmpty() && Event.CAL_ID_FB.equals(ids.get(0).getType())) {
			final List<Event> events = ((EventsDao) getCalDao()).getEventsFromFacebook(ids);
			final ItemsResponseImpl response = new ItemsResponseImpl();
			response.setItems(events);
			return response;
		} else {
			return super.getItems(ids, context);
		}
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context, RequestType requestType)
			throws CalException {
		if (requestType == EventRequestTypes.ALL_EVENTS) {
			final List<Event> events = ((EventsDao) getCalDao()).getAllItemsFor(itemKey);
			final ItemsResponseImpl response = new ItemsResponseImpl();
			response.setItems(events);
			return response;
		} else {
			return getItemsFor(itemKey, context);
		}
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey, ItemKey... internalItemKeys)
			throws CalException {
		Assert.notNull(contributedItemKey, "Cannot define Event association with null item key");
		Assert.notNull(internalItemKeys, "Cannot define Event association with no events");
		Assert.moreThan(internalItemKeys.length, 0, "Cannot define Event association with empty events");
		return ((EventsDao) getCalDao()).bindEvents(contributedItemKey, Arrays.asList(internalItemKeys));
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey, String connectionType,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException("setItemFor(ItemKey, connectionType, ItemKey...) is not implemented");
	}

	@Override
	public boolean deleteItemFor(ItemKey contributedItemKey, String connectionType, ItemKey internalItemKey)
			throws CalException {
		throw new UnsupportedCalServiceException("deleteItemFor not implemented");
	}
}
