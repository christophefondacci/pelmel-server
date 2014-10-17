package com.nextep.json.model.impl;

public class JsonSuggest {

	private final String label;
	private final String userLabel;
	private final String value;
	private final String url;
	private String mainText;
	private String suffix;
	private String imageUrl;
	private double lat, lng;
	private int count;

	public JsonSuggest(String label, String userLabel, String value, String url) {
		this.label = label;
		this.userLabel = userLabel;
		this.value = value;
		this.url = url;
	}

	public String getLabel() {
		return label;
	}

	public String getUserLabel() {
		return userLabel;
	}

	public String getValue() {
		return value;
	}

	public String getUrl() {
		return url;
	}

	public void setMainText(String mainText) {
		this.mainText = mainText;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getMainText() {
		return mainText;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
