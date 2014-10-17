package com.nextep.messages.services;

import java.util.List;

import com.nextep.messages.model.Message;
import com.videopolis.calm.model.ItemKey;

/**
 * Defines specific features for message management.
 * 
 * @author cfondacci
 * 
 */
public interface MessageService {

	/**
	 * Marks the message identified by its unique key as read.
	 * 
	 * @param messageKeys
	 *            the list of {@link ItemKey} of messages to mark read
	 */
	List<? extends Message> markRead(List<ItemKey> messageKeys);
}
