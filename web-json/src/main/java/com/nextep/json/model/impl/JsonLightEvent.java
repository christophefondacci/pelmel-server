package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.IJsonLightPlace;

public class JsonLightEvent implements IJsonLightEvent {

	private String key;
	private String name;
	private IJsonLightPlace place;
	private Long startTime;
	private Long endTime;
	private Collection<JsonMedia> media = new ArrayList<JsonMedia>();
	private String distance;
	private double rawDistance;
	private int participants = 0;
	private String calendarType;

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public IJsonLightPlace getPlace() {
		return place;
	}

	@Override
	public void setPlace(IJsonLightPlace place) {
		this.place = place;
	}

	@Override
	public Long getStartTime() {
		return startTime;
	}

	@Override
	public void setStartTime(Date startTime) {
		this.startTime = startTime == null ? null : startTime.getTime() / 1000;
	}

	@Override
	public Long getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(Date endTime) {
		this.endTime = endTime == null ? null : endTime.getTime() / 1000;
	}

	@Override
	public Collection<JsonMedia> getMedia() {
		return media;
	}

	@Override
	public void addMedia(JsonMedia media) {
		this.media.add(media);
	}

	@Override
	public String getDistance() {
		return distance;
	}

	@Override
	public void setDistance(String distance) {
		this.distance = distance;
	}

	@Override
	public double getRawDistance() {
		return rawDistance;
	}

	@Override
	public void setRawDistance(double rawDistance) {
		this.rawDistance = rawDistance;
	}

	@Override
	public void setParticipants(int participants) {
		this.participants = participants;
	}

	@Override
	public int getParticipants() {
		return participants;
	}

	@Override
	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	@Override
	public String getCalendarType() {
		return calendarType;
	}
}
