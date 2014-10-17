package com.nextep.tags.model.impl;

import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;

public class TagRequestTypeFor implements RequestType {

	private static final long serialVersionUID = 3151875080905429995L;

	private final ItemKey taggedItemKey;

	public TagRequestTypeFor(ItemKey taggedItemKey) {
		this.taggedItemKey = taggedItemKey;
	}

	public ItemKey getTaggedItemKey() {
		return taggedItemKey;
	}
}
