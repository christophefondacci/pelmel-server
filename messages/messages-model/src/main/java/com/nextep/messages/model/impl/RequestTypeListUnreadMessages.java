package com.nextep.messages.model.impl;

import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;

public class RequestTypeListUnreadMessages implements RequestType {

	private static final long serialVersionUID = -4845100982598261815L;
	private final ItemKey forItemKey;

	public RequestTypeListUnreadMessages(ItemKey forItemKey) {
		this.forItemKey = forItemKey;
	}

	public ItemKey getForItemKey() {
		return forItemKey;
	}

	@Override
	public String toString() {
		return "UNREAD_MSG;" + forItemKey.toString();
	}
}
