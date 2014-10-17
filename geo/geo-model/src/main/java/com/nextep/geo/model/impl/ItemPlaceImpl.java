package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(value = ItemPlacePK.class)
@Table(name = "GEO_ITEMS_PLACES")
public class ItemPlaceImpl {

	@Id
	@Column(name = "PLACE_ID", nullable = false)
	private String placeId;

	@Id
	@Column(name = "ITEM_KEY")
	private String externalItemKey;

	public ItemPlaceImpl() {
	}

	public ItemPlaceImpl(ItemKey itemKey, ItemKey placeKey) {
		this.placeId = placeKey.getId();
		externalItemKey = itemKey.toString();
	}

	public String getPlaceId() {
		return placeId;
	}

	public String getExternalItemKey() {
		return externalItemKey;
	}
}
