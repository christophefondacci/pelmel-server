package com.nextep.messages.services.impl;

import java.util.Collections;
import java.util.List;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.messages.dao.MessageDao;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageRequestTypeAfterId;
import com.nextep.messages.model.MessageRequestTypeUnread;
import com.nextep.messages.model.impl.MessageImpl;
import com.nextep.messages.model.impl.MessageRequestTypeListConversation;
import com.nextep.messages.model.impl.RequestTypeListUnreadMessages;
import com.nextep.messages.services.MessageService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;

public class MessageServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService, MessageService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Message.class;
	}

	@Override
	public String getProvidedType() {
		return Message.CAL_TYPE;
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException(
				"setItemsFor not supported by MessageService");
	}

	@Override
	public CalmObject createTransientObject() {
		return new MessageImpl();
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
				resultsPerPage, pageNumber);
		@SuppressWarnings("unchecked")
		final List<Message> messages = (List<Message>) getCalDao().getItemsFor(
				itemKey, resultsPerPage, pageNumber);
		final int count = ((MessageDao) getCalDao()).getMessageCount(itemKey,
				false);
		response.setItemCount(count);
		int pages = count / resultsPerPage;
		int pagesMod = count % resultsPerPage;
		response.setPageCount(pages + (pagesMod > 0 ? 1 : 0));
		response.setItems(messages);
		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber,
			RequestType requestType) throws CalException {
		if (requestType == null) {
			return getPaginatedItemsFor(itemKey, context, resultsPerPage,
					pageNumber);
		} else if (requestType instanceof MessageRequestTypeAfterId) {
			// Extracting messages after this date
			final MessageRequestTypeAfterId dateRequestType = (MessageRequestTypeAfterId) requestType;
			final MessageDao dao = (MessageDao) getCalDao();
			final List<Message> messages = dao.getMessagesForAfterId(itemKey,
					pageNumber, resultsPerPage,
					dateRequestType.getMinMessageItemKey());

			// Querying total count
			final int messagesCount = dao.getMessageCountAfterId(itemKey,
					dateRequestType.getMinMessageItemKey());

			// Building response
			final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
					resultsPerPage, pageNumber);
			response.setItems(messages);
			response.setPageCount(CalHelper.getPageCount(resultsPerPage,
					messagesCount));
			response.setItemCount(messagesCount);
			return response;

		}
		// Defaulting to super implementation
		return super.getPaginatedItemsFor(itemKey, context, resultsPerPage,
				pageNumber, requestType);
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType requestType,
			RequestSettings requestSettings) throws CalException {
		if (requestType instanceof RequestTypeListUnreadMessages) {
			final ItemKey forItemKey = ((RequestTypeListUnreadMessages) requestType)
					.getForItemKey();
			return getUnreadMessagesFor(forItemKey);
		} else if (requestType instanceof MessageRequestTypeListConversation) {
			final MessageRequestTypeListConversation conversationReqType = (MessageRequestTypeListConversation) requestType;
			final ItemKey fromKey = conversationReqType.getFromKey();
			final ItemKey toKey = conversationReqType.getToKey();
			// Retrieving requested pagination window
			final PaginationRequestSettings paginationSettings = (PaginationRequestSettings) requestSettings;
			final int pageOffset = paginationSettings.getPageNumber();
			final int pageSize = paginationSettings.getResultsPerPage();
			// Fetching requested window of messages
			final List<Message> messages = ((MessageDao) getCalDao())
					.getConversation(fromKey, toKey, pageOffset * pageSize,
							pageSize);
			// Gathering statistics for response
			final int messageCount = ((MessageDao) getCalDao())
					.getConversationMessageCount(fromKey, toKey);
			// Building response
			final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
					pageSize, pageOffset);
			// Defining statistics for pagination
			response.setItemCount(messageCount);
			int pages = messageCount / pageSize;
			int pagesMod = messageCount % pageSize;
			response.setPageCount(pages + (pagesMod > 0 ? 1 : 0));
			response.setItems(messages);
			return response;

		}
		return super.listItems(context, requestType, requestSettings);
	}

	private PaginatedItemsResponse getUnreadMessagesFor(ItemKey itemKey) {
		final MessageDao dao = (MessageDao) getCalDao();
		final List<Message> unreadMessages = dao.getUnreadMessages(itemKey);
		final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
				unreadMessages.size(), 0);
		response.setItems(unreadMessages);
		response.setItemCount(unreadMessages.size());
		return response;
	}

	@Override
	public List<? extends Message> markRead(List<ItemKey> messageKeys) {
		final MessageDao dao = (MessageDao) getCalDao();
		if (messageKeys != null && !messageKeys.isEmpty()) {
			return dao.markRead(messageKeys);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context,
			RequestType requestType) throws CalException {
		if (requestType instanceof MessageRequestTypeUnread) {
			return getUnreadMessagesFor(itemKey);
		} else {
			return super.getItemsFor(itemKey, context, requestType);
		}
	}
}
