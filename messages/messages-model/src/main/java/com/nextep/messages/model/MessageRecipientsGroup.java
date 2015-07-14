package com.nextep.messages.model;

import java.util.Collection;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * A message thread is a group of recipients. A message can be attached to a
 * thread so that any response to a message of this thread will be dispatched to
 * everybody in this thread.
 * 
 * @author cfondacci
 *
 */
public interface MessageRecipientsGroup extends CalmObject {

	String CAL_TYPE = "RCPT";

	/**
	 * The list of {@link ItemKey} of the recipients of this message
	 * 
	 * @return the collection of recipients that this group defines
	 */
	Collection<ItemKey> getRecipients();
}
