package com.nextep.messages.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface represents a message.
 * 
 * @author cfondacci
 * 
 */
public interface Message extends CalmObject {

	String CAL_TYPE = "MESG";

	/**
	 * Identifier of the message sender (from)
	 * 
	 * @return the {@link ItemKey} of the element which sent this message
	 */
	ItemKey getFromKey();

	/**
	 * Identifier of the message receiver (to)
	 * 
	 * @return the {@link ItemKey} of the element which received this message
	 */
	ItemKey getToKey();

	/**
	 * The identifier of the source message. If this message is a response to a
	 * previous message, this method will provide the identifier of the previous
	 * message. If this message is brand new, this method will return
	 * <code>null</code>
	 * 
	 * @return the {@link ItemKey} of the initial message to which this message
	 *         is a response, or <code>null</code>
	 */
	Message getSourceMessage();

	/**
	 * The date of this message
	 * 
	 * @return the date of this message
	 */
	Date getMessageDate();

	/**
	 * The message contents.
	 * 
	 * @return the message text
	 */
	String getMessage();

	/**
	 * Indicates whether this message is unread or not.
	 * 
	 * @return <code>true</code> when this message has not yet been read,
	 *         <code>true</code> after the user has opened this message
	 */
	boolean isUnread();

	/**
	 * Provides the {@link ItemKey} of the recipients group of this message when
	 * this message is a group message
	 * 
	 * @return the {@link ItemKey} of a {@link MessageRecipientsGroup} instance,
	 *         or <code>null</code> for one to one messages
	 */
	ItemKey getRecipientsGroupKey();

	/**
	 * Provides the message type for this message.
	 * 
	 * @return the {@link MessageType} of this message
	 */
	MessageType getMessageType();
}
