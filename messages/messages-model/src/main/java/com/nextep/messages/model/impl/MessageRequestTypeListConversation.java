package com.nextep.messages.model.impl;

import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;

public class MessageRequestTypeListConversation implements RequestType {

	private static final long serialVersionUID = -4157953955967684458L;

	private final ItemKey fromKey;
	private final ItemKey toKey;

	public MessageRequestTypeListConversation(ItemKey from, ItemKey to) {
		this.fromKey = from;
		this.toKey = to;
	}

	public ItemKey getFromKey() {
		return fromKey;
	}

	public ItemKey getToKey() {
		return toKey;
	}
}
