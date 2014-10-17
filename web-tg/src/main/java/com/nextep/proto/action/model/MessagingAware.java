package com.nextep.proto.action.model;

import com.nextep.proto.blocks.MessagingSupport;

/**
 * Interface implemented by actions which provides support for instant messaging
 * features.
 * 
 * @author cfondacci
 * 
 */
public interface MessagingAware {

	/**
	 * Accessor to the {@link MessagingSupport} implementation.
	 * 
	 * @return the messaging support implementation
	 */
	MessagingSupport getMessagingSupport();

	/**
	 * Injection setter for the {@link MessagingSupport} implementation
	 * 
	 * @param messagingSupport
	 *            the {@link MessagingSupport} implementation
	 */
	void setMessagingSupport(MessagingSupport messagingSupport);
}
