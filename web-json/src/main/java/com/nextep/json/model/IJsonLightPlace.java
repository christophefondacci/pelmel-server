package com.nextep.json.model;

import com.nextep.json.model.impl.JsonMedia;

public interface IJsonLightPlace {

	String getKey();

	void setKey(String key);

	String getName();

	void setName(String name);

	JsonMedia getThumb();

	void setThumb(JsonMedia thumb);

	void setTimezoneId(String timezoneId);

	String getTimezoneId();

	void setType(String type);

	String getType();

}