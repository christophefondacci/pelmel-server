package com.nextep.smaug.solr.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class PlaceSearchItemImpl extends SearchItemImpl {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	@Field
	private String placeType;
	@Field
	private Double lat;
	@Field
	private Double lng;
	@Field
	private final List<String> amenities = new ArrayList<String>();
	@Field
	private int seoIndexed;
	@Field
	private int adBoostValue;
	@Field
	private long adBoostEndDate;
	@Field
	private long rating;

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public void addAmenity(String amenity) {
		amenities.add(amenity);
	}

	public void setSeoIndexed(int seoIndexed) {
		this.seoIndexed = seoIndexed;
	}

	public int getSeoIndexed() {
		return seoIndexed;
	}

	public void setAdBoostEndDate(Date adBoostEndDate) {
		this.adBoostEndDate = Long.valueOf(DATE_FORMAT.format(adBoostEndDate));
	}

	public void setAdBoostValue(int adBoostValue) {
		this.adBoostValue = adBoostValue;
	}

	public void setRating(long rating) {
		this.rating = rating;
	}
}
