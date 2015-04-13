package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

public class JsonNearbyPlacesResponse {

	private List<JsonPlace> places = new ArrayList<JsonPlace>();
	private List<JsonLightCity> cities = new ArrayList<JsonLightCity>();
	private JsonLightCity localizedCity;
	private List<JsonActivity> nearbyActivities = new ArrayList<JsonActivity>();
	private List<JsonLightUser> nearbyUsers = new ArrayList<JsonLightUser>();
	private List<JsonLightEvent> nearbyEvents = new ArrayList<JsonLightEvent>();
	private int nearbyUsersCount;
	private int nearbyPlacesCount;
	private int nearbyEventsCount;

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

	public List<JsonLightEvent> getNearbyEvents() {
		return nearbyEvents;
	}

	public void setNearbyEvents(List<JsonLightEvent> nearbyEvents) {
		this.nearbyEvents = nearbyEvents;
	}

	public void setNearbyEventsCount(int nearbyEventsCount) {
		this.nearbyEventsCount = nearbyEventsCount;
	}

	public void setNearbyPlacesCount(int nearbyPlacesCount) {
		this.nearbyPlacesCount = nearbyPlacesCount;
	}

	public void setNearbyUsersCount(int nearbyUsersCount) {
		this.nearbyUsersCount = nearbyUsersCount;
	}

	public int getNearbyEventsCount() {
		return nearbyEventsCount;
	}

	public int getNearbyPlacesCount() {
		return nearbyPlacesCount;
	}

	public int getNearbyUsersCount() {
		return nearbyUsersCount;
	}
}
