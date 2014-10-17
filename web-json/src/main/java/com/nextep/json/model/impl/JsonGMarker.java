package com.nextep.json.model.impl;


public class JsonGMarker {

	private final String name;
	private final String url;
	private final String iconType;
	private final double latitude;
	private final double longitude;

	public JsonGMarker(String name, String url, String iconType,
			double latitude, double longitude) {
		this.name = name;
		this.url = url;
		this.iconType = iconType;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getPosition() {
		return "new google.maps.LatLng(" + latitude + "," + longitude + ")";
	}

	public String getTitle() {
		return name;
	}

	public String getMap() {
		return "map";
	}
}
