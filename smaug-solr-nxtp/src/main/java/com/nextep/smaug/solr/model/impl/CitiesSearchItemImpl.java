package com.nextep.smaug.solr.model.impl;

import org.apache.solr.client.solrj.beans.Field;

public class CitiesSearchItemImpl {

	@Field
	private String id;
	@Field
	private Double lat;
	@Field
	private Double lng;

	public CitiesSearchItemImpl(String id) {
		this.id = id;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}
}
