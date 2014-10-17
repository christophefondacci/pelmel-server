package com.nextep.json.model.impl;

import java.util.Date;

public class JsonLightUser {
	private String key;
	private String pseudo;
	private JsonMedia thumb;
	private Long lastLocationTime;
	private boolean online;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public JsonMedia getThumb() {
		return thumb;
	}

	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	public Long getLastLocationTime() {
		return lastLocationTime;
	}

	public void setLastLocationTime(Long lastLocationTime) {
		this.lastLocationTime = lastLocationTime;
	}

	public void setLastLocationTimeValue(Date lastLocationDate) {
		this.lastLocationTime = lastLocationDate == null ? null
				: lastLocationDate.getTime() / 1000;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
}
