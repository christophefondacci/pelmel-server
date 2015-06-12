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
	private String extraType;

	@Field
	private String placeKey;

	@Field
	private String userKey;

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

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public void setExtraType(String extraType) {
		this.extraType = extraType;
	}

	public String getExtraType() {
		return extraType;
	}
}
