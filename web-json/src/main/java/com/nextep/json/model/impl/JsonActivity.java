package com.nextep.json.model.impl;

import java.util.Date;

import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.IJsonLightPlace;
import com.nextep.json.model.IJsonLightUser;

public class JsonActivity {

	private String key;
	private IJsonLightUser user;
	private IJsonLightPlace activityPlace;
	private IJsonLightEvent activityEvent;
	private IJsonLightUser activityUser;
	private IJsonLightEvent extraEvent;
	private JsonMedia extraMedia;
	private String message;
	private String activityType;
	private Integer count;
	private Long activityDate;

	public IJsonLightUser getUser() {
		return user;
	}

	public void setUser(IJsonLightUser user) {
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

	public IJsonLightUser getActivityUser() {
		return activityUser;
	}

	public void setActivityUser(IJsonLightUser activityUser) {
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

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setExtraEvent(IJsonLightEvent extraEvent) {
		this.extraEvent = extraEvent;
	}

	public IJsonLightEvent getExtraEvent() {
		return extraEvent;
	}

	public void setExtraMedia(JsonMedia extraMedia) {
		this.extraMedia = extraMedia;
	}

	public JsonMedia getExtraMedia() {
		return extraMedia;
	}

	public void setActivityEvent(IJsonLightEvent activityEvent) {
		this.activityEvent = activityEvent;
	}

	public IJsonLightEvent getActivityEvent() {
		return activityEvent;
	}
}
