package com.nextep.messages.dao;

import java.util.List;

import com.nextep.cal.util.model.CalDao;
import com.nextep.messages.model.Message;
import com.videopolis.calm.model.ItemKey;

public interface MessageDao extends CalDao<Message> {

	List<Message> getUnreadMessages(ItemKey userKey);

	List<Message> getUnreadMessages(ItemKey userKey, int maxResults);

	int getMessageCount(ItemKey userKey, boolean unreadOnly);

	List<? extends Message> markRead(List<ItemKey> messageKeys);

	/**
	 * Lists messages from the specified conversation between the 2 persons.
	 * "From" and "To" elements can be exchanged as messages of both directions
	 * will be returned.
	 * 
	 * @param from
	 *            {@link ItemKey} of one bound of the conversation
	 * @param to
	 *            {@link ItemKey} of the other bound of the conversation
	 * @param offset
	 *            the offset at which we start listing messages
	 * @param count
	 *            the number of messages to return
	 * @return the list of corresponding {@link Message}
	 */
	List<Message> getConversation(ItemKey from, ItemKey to, int offset,
			int count);

	/**
	 * Provides the number of messages between the 2 elements.
	 * 
	 * @param from
	 * @param to
	 * @return total number of exchanged messages
	 */
	int getConversationMessageCount(ItemKey from, ItemKey to);
}
