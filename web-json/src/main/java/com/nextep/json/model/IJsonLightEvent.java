package com.nextep.json.model;

import java.util.Collection;
import java.util.Date;

import com.nextep.json.model.impl.JsonMedia;

public interface IJsonLightEvent extends IJsonWithParticipants {

	String getKey();

	void setKey(String key);

	String getName();

	void setName(String name);

	IJsonLightPlace getPlace();

	void setPlace(IJsonLightPlace place);

	Long getStartTime();

	void setStartTime(Date startTime);

	Long getEndTime();

	void setEndTime(Date endTime);

	Collection<JsonMedia> getMedia();

	void addMedia(JsonMedia media);

	String getDistance();

	void setDistance(String distance);

	double getRawDistance();

	void setRawDistance(double rawDistance);

	String getCalendarType();

	void setCalendarType(String calendarType);
}