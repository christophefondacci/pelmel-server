package com.nextep.json.model.impl;

import com.nextep.json.model.IJsonLightPlace;

public class JsonLightPlace implements IJsonLightPlace {
	private String key;
	private String name;
	private JsonMedia thumb;
	private String timezoneId;

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
	public JsonMedia getThumb() {
		return thumb;
	}

	@Override
	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	@Override
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	@Override
	public String getTimezoneId() {
		return timezoneId;
	}
}
