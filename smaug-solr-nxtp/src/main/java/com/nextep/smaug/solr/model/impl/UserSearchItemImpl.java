package com.nextep.smaug.solr.model.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class UserSearchItemImpl extends SearchItemImpl {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	@Field
	private int weight_kg;

	@Field
	private int height_cm;

	@Field
	private int birthyear;

	@Field
	private long onlineTimeout;

	@Field
	private String currentPlace;
	@Field
	private String currentAutoPlace;
	@Field
	private long currentPlaceTimeout;

	@Field
	private int available = 0;

	@Field
	private List<String> places = new ArrayList<String>();

	@Field
	private List<String> events = new ArrayList<String>();

	@Field
	private List<String> users = new ArrayList<String>();

	public void setWeightKg(int weight_kg) {
		this.weight_kg = weight_kg;
	}

	public int getWeightKg() {
		return weight_kg;
	}

	public void setHeightCM(int height_m) {
		this.height_cm = height_m;
	}

	public int getHeightCM() {
		return height_cm;
	}

	public void setBirthyear(int birthyear) {
		this.birthyear = birthyear;
	}

	public int getBirthyear() {
		return birthyear;
	}

	public void addPlace(String placeKey) {
		places.add(placeKey);
	}

	public void addEvent(String eventKey) {
		events.add(eventKey);
	}

	public void addUser(String userKey) {
		users.add(userKey);
	}

	public void setPlaces(List<String> places) {
		this.places = places;
	}

	public List<String> getPlaces() {
		return places;
	}

	public void setEvents(List<String> events) {
		this.events = events;
	}

	public List<String> getEvents() {
		return events;
	}

	public void removeEvent(String event) {
		events.remove(event);
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setOnlineTimeout(Date date) {
		this.onlineTimeout = Long.valueOf(DATE_FORMAT.format(date));
	}

	public void setCurrentPlace(String currentPlace) {
		this.currentPlace = currentPlace;
	}

	public void setCurrentPlaceTimeout(long currentPlaceTimeout) {
		this.currentPlaceTimeout = currentPlaceTimeout;
	}

	public void setAvailable(boolean available) {
		this.available = available ? 1 : 0;
	}

	public boolean isAvailable() {
		return this.available > 0;
	}

	public void setCurrentAutoPlace(String currentAutoPlace) {
		this.currentAutoPlace = currentAutoPlace;
	}
}
