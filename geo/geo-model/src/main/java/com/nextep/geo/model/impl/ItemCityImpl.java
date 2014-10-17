package com.nextep.geo.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.nextep.geo.model.City;
import com.videopolis.calm.model.ItemKey;

@Entity
@IdClass(value = ItemCityPK.class)
@Table(name = "GEO_ITEMS_CITIES")
public class ItemCityImpl {

	@Id
	@OneToOne(optional = false, targetEntity = CityImpl.class)
	@JoinColumn(name = "CITY_ID", nullable = false)
	private City city;

	@Id
	@Column(name = "ITEM_KEY")
	private String externalItemKey;

	public ItemCityImpl() {
	}

	public ItemCityImpl(ItemKey itemKey, City city) {
		this.city = city;
		externalItemKey = itemKey.toString();
	}

	public City getCity() {
		return city;
	}
}
