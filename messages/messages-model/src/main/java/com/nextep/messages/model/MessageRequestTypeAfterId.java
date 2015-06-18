package com.nextep.messages.model;

import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;

/**
 * A request type that will fetch messages received after a given date.
 * 
 * @author cfondacci
 *
 */
public class MessageRequestTypeAfterId implements RequestType {

	private static final long serialVersionUID = 1L;
	private ItemKey minMessageItemKey;

	/**
	 * Builds this request type with the minimum date from which messages should
	 * be returned
	 * 
	 * @param minDate
	 */
	public MessageRequestTypeAfterId(ItemKey minMessageItemKey) {
		this.minMessageItemKey = minMessageItemKey;
	}

	public ItemKey getMinMessageItemKey() {
		return minMessageItemKey;
	}
}
