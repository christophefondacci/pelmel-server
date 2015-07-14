package com.nextep.messages.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableMessage extends Message {

	void setFromKey(ItemKey fromKey);

	void setToKey(ItemKey toKey);

	void setSourceMessage(Message sourceMessage);

	void setMessageDate(Date date);

	void setMessage(String message);

	void setUnread(boolean unread);

	/**
	 * Sets the unique {@link ItemKey} of a recipients group for this message.
	 * Such messages will be threaded differently based solely on their
	 * recipients group.
	 * 
	 * @param key
	 *            the {@link MessageRecipientsGroup} item key
	 */
	void setRecipientsGroupKey(ItemKey key);

	/**
	 * Sets the message type of this message.
	 * 
	 * @param messageType
	 *            the {@link MessageType}
	 */
	void setMessageType(MessageType messageType);
}
