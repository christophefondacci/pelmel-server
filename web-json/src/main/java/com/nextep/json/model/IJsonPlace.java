package com.nextep.json.model;

import java.util.List;

import com.nextep.json.model.impl.JsonMedia;

public interface IJsonPlace extends IJsonLightPlace {
	String getAddress();

	void setAddress(String address);

	String getCity();

	void setCity(String city);

	String getDescription();

	void setDescription(String description);

	double getLat();

	double getLng();

	void setLat(double lat);

	void setLng(double lng);

	List<String> getTags();

	void addTag(String tagId);

	List<JsonMedia> getOtherImages();

	void addOtherImage(JsonMedia media);
}
