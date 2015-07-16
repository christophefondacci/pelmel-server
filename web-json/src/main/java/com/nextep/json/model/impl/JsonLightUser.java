package com.nextep.json.model.impl;

import java.util.Date;

import com.nextep.json.model.IJsonLightPlace;
import com.nextep.json.model.IJsonLightUser;

public class JsonLightUser implements IJsonLightUser {
	private String key;
	private String pseudo;
	private JsonMedia thumb;
	private Long lastLocationTime;
	private boolean online;
	private double rawDistance;
	private IJsonLightPlace lastLocation;

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getPseudo() {
		return pseudo;
	}

	@Override
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public JsonMedia getThumb() {
		return thumb;
	}
 
	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	@Override
	public Long getLastLocationTime() {
		return lastLocationTime;
	}

	@Override
	public void setLastLocationTime(Long lastLocationTime) {
		this.lastLocationTime = lastLocationTime;
	}

	@Override
	public void setLastLocationTimeValue(Date lastLocationDate) {
		this.lastLocationTime = lastLocationDate == null ? null : lastLocationDate.getTime() / 1000;
	}

	@Override
	public boolean isOnline() {
		return online;
	}

	@Override
	public void setOnline(boolean online) {
		this.online = online;
	}

	@Override
	public void setRawDistanceMeters(double rawDistance) {
		this.rawDistance = rawDistance;
	}

	@Override
	public double getRawDistanceMeters() {
		return rawDistance;
	}

	@Override
	public void setLastLocation(IJsonLightPlace lastLocation) {
		this.lastLocation = lastLocation;
	}

	@Override
	public IJsonLightPlace getLastLocation() {
		return lastLocation;
	}
}
