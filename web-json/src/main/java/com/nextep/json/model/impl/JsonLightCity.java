package com.nextep.json.model.impl;

public class JsonLightCity {

	private String key;
	private String name;
	private String localization;
	private int placesCount;
	private Double latitude;
	private Double longitude;
	private JsonMedia media;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocalization(String localization) {
		this.localization = localization;
	}

	public String getLocalization() {
		return localization;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setPlacesCount(int placesCount) {
		this.placesCount = placesCount;
	}

	public int getPlacesCount() {
		return placesCount;
	}

	public void setMedia(JsonMedia media) {
		this.media = media;
	}

	public JsonMedia getMedia() {
		return media;
	}
}
