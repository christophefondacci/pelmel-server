package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.nextep.json.model.IJsonLightEvent;

public class JsonEvent extends JsonLiker implements IJsonLightEvent {

	private String key;
	private String name;
	private JsonLightPlace place;
	private Long startTime;
	private Long endTime;
	private Collection<JsonMedia> media = new ArrayList<JsonMedia>();
	private String distance;
	private double rawDistance;
	private String description;
	private int participants;
	private int commentsCount;

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
	public JsonLightPlace getPlace() {
		return place;
	}

	@Override
	public void setPlace(JsonLightPlace place) {
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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	@Override
	public void setParticipants(int participants) {
		this.participants = participants;
	}

	@Override
	public int getParticipants() {
		return participants;
	}
}
