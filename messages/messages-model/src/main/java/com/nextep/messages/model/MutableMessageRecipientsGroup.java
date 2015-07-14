package com.nextep.messages.model;

import java.util.Collection;

import com.videopolis.calm.model.ItemKey;

/**
 * Exposes data modification methods for the {@link MessageRecipientsGroup}
 * interface
 * 
 * @author cfondacci
 *
 */
public interface MutableMessageRecipientsGroup extends MessageRecipientsGroup {

	/**
	 * Resets and redefines the recipients of this group
	 * 
	 * @param recipients
	 *            the new recipients list, identified by their {@link ItemKey}
	 */
	void setRecipients(Collection<ItemKey> recipients);

	/**
	 * Adds a recipient to this group
	 * 
	 * @param recipient
	 *            the recipient's {@link ItemKey}
	 */
	// void addRecipient(ItemKey recipient);
}
