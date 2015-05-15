package com.nextep.json.model.impl;

public class JsonBanner {

	private String key;
	private long displayCount, clickCount, targetDisplayCount;
	private double lat, lng, radius;
	private JsonLightPlace targetPlace;
	private JsonLightEvent targetEvent;
	private String targetUrl;
	private JsonMedia bannerImage;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getDisplayCount() {
		return displayCount;
	}

	public void setDisplayCount(long displayCount) {
		this.displayCount = displayCount;
	}

	public long getClickCount() {
		return clickCount;
	}

	public void setClickCount(long clickCount) {
		this.clickCount = clickCount;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setTargetPlace(JsonLightPlace targetPlace) {
		this.targetPlace = targetPlace;
	}

	public void setTargetEvent(JsonLightEvent targetEvent) {
		this.targetEvent = targetEvent;
	}

	public JsonLightPlace getTargetPlace() {
		return targetPlace;
	}

	public JsonLightEvent getTargetEvent() {
		return targetEvent;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public long getTargetDisplayCount() {
		return targetDisplayCount;
	}

	public void setTargetDisplayCount(long targetDisplayCount) {
		this.targetDisplayCount = targetDisplayCount;
	}

	public void setBannerImage(JsonMedia bannerImage) {
		this.bannerImage = bannerImage;
	}

	public JsonMedia getBannerImage() {
		return bannerImage;
	}
}
