package com.nextep.events.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(value = ItemEventPK.class)
@Table(name = "EVENTS_SERIES_ITEMS")
public class ItemEventSeriesImpl {

	@Id
	@Column(name = "ESERIE_ID", nullable = false)
	private long itemId;

	@Id
	@Column(name = "ITEM_KEY", nullable = false)
	private String externalItemKey;

	public ItemEventSeriesImpl() {
	}

	public ItemEventSeriesImpl(ItemKey externalItemKey, ItemKey eventKey) {
		this.itemId = eventKey.getNumericId();
		this.externalItemKey = externalItemKey.toString();
	}

	public long getItemId() {
		return itemId;
	}

	public String getExternalItemKey() {
		return externalItemKey;
	}
}
