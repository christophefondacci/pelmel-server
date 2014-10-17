package com.nextep.geo.services.test.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

public class PlaceSolrItem {
	@Field
	private String id;
	@Field
	private String name;

	@Field
	private final List<String> tags = new ArrayList<String>();

	@Field
	private String continentId;
	@Field
	private String countryId;
	@Field
	private String adm1;
	@Field
	private String adm2;
	@Field
	private String cityId;
	@Field
	private String placeType;
	@Field
	private double lat;
	@Field
	private double lng;

	public ItemKey getKey() {
		try {
			return CalmFactory.parseKey(id);
		} catch (CalException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setKey(ItemKey key) {
		this.id = key.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addTag(String tag) {
		this.tags.add(tag);
	}

	public void setContinentId(String continentId) {
		this.continentId = continentId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public void setAdm1(String adm1) {
		this.adm1 = adm1;
	}

	public void setAdm2(String adm2) {
		this.adm2 = adm2;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
