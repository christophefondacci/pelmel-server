package com.nextep.messages.services.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.messages.dao.MessageDao;
import com.nextep.messages.model.MessageRecipientsGroup;
import com.nextep.messages.model.impl.MessageRecipientsGroupImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class MessageRecipientsGroupServiceImpl extends AbstractCalService
		implements CalPersistenceService {

	private MessageDao messageDao;

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return MessageRecipientsGroup.class;
	}

	@Override
	public String getProvidedType() {
		return MessageRecipientsGroup.CAL_TYPE;
	}

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {
		final List<MessageRecipientsGroup> groups = messageDao
				.getMessageGroups(ids);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(groups);
		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		throw new UnsupportedCalServiceException(
				"MessageRecipientsGroupServiceImpl.getPaginatedItemsFor() not supported");
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Arrays.asList(MessageRecipientsGroup.CAL_TYPE);
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		throw new UnsupportedCalServiceException(
				"MessageRecipientsGroupServiceImpl.getItemsFor() not supported");
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	@Override
	public CalmObject createTransientObject() {
		return new MessageRecipientsGroupImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		messageDao.save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException(
				"MessageRecipientsGroupServiceImpl.setItemsFor() not supported");
	}
}
