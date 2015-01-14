package com.nextep.smaug.solr.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class ActivitySearchItemImpl extends SearchItemImpl {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	@Field
	private long activityDate;

	@Field
	private String targetType;

	@Field
	private String placeKey;

	@Field
	private String userKey;

	@Field
	private Double lat;

	@Field
	private Double lng;

	@Field
	private String activityType;

	public void setActivityDate(Date activityDate) {
		this.activityDate = Long.valueOf(DATE_FORMAT.format(activityDate));
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getPlaceKey() {
		return placeKey;
	}

	public void setPlaceKey(String placeKey) {
		this.placeKey = placeKey;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}
}
