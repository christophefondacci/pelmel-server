package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

public class JsonNearbyPlacesResponse {

	private List<JsonPlace> places = new ArrayList<JsonPlace>();
	private List<JsonLightCity> cities = new ArrayList<JsonLightCity>();
	private JsonLightCity localizedCity;
	private List<JsonActivity> nearbyActivities = new ArrayList<JsonActivity>();
	private List<JsonLightUser> nearbyUsers = new ArrayList<JsonLightUser>();

	public void setPlaces(List<JsonPlace> places) {
		this.places = places;
	}

	public List<JsonPlace> getPlaces() {
		return places;
	}

	public void setCities(List<JsonLightCity> cities) {
		this.cities = cities;
	}

	public List<JsonLightCity> getCities() {
		return cities;
	}

	public JsonLightCity getLocalizedCity() {
		return localizedCity;
	}

	public void setLocalizedCity(JsonLightCity localizedCity) {
		this.localizedCity = localizedCity;
	}

	public List<JsonActivity> getNearbyActivities() {
		return nearbyActivities;
	}

	public void addNearbyActivity(JsonActivity activity) {
		nearbyActivities.add(activity);
	}

	public void addNearbyUser(JsonLightUser user) {
		nearbyUsers.add(user);
	}

	public void setNearbyActivities(List<JsonActivity> nearbyActivities) {
		this.nearbyActivities = nearbyActivities;
	}

	public void setNearbyUsers(List<JsonLightUser> nearbyUsers) {
		this.nearbyUsers = nearbyUsers;
	}

	public List<JsonLightUser> getNearbyUsers() {
		return nearbyUsers;
	}
}
