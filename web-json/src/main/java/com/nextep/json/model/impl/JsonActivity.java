package com.nextep.json.model.impl;

import java.util.Date;

public class JsonActivity {

	private JsonLightUser user;
	private JsonLightPlace activityPlace;
	private JsonLightUser activityUser;
	private String message;
	private Long activityDate;

	public JsonLightUser getUser() {
		return user;
	}

	public void setUser(JsonLightUser user) {
		this.user = user;
	}

	public JsonLightPlace getActivityPlace() {
		return activityPlace;
	}

	public void setActivityPlace(JsonLightPlace place) {
		this.activityPlace = place;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setActivityDateValue(Date date) {
		this.activityDate = date.getTime() / 1000;
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
}
