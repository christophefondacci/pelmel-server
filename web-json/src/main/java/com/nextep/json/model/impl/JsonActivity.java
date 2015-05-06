package com.nextep.json.model.impl;

import java.util.Date;

import com.nextep.json.model.IJsonLightPlace;

public class JsonActivity {

	private JsonLightUser user;
	private IJsonLightPlace activityPlace;
	private JsonLightUser activityUser;
	private String message;
	private String activityType;
	private Integer count;
	private Long activityDate;

	public JsonLightUser getUser() {
		return user;
	}

	public void setUser(JsonLightUser user) {
		this.user = user;
	}

	public IJsonLightPlace getActivityPlace() {
		return activityPlace;
	}

	public void setActivityPlace(IJsonLightPlace place) {
		this.activityPlace = place;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setActivityDateValue(Date date) {
		this.activityDate = date == null ? null : (date.getTime() / 1000);
	}

	public Long getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Long activityDate) {
		this.activityDate = activityDate;
	}

	public JsonLightUser getActivityUser() {
		return activityUser;
	}

	public void setActivityUser(JsonLightUser activityUser) {
		this.activityUser = activityUser;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}
}
