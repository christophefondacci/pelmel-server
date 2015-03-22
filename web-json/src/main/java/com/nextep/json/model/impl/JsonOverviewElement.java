package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nextep.json.model.IJsonDescripted;

public class JsonOverviewElement extends JsonLiker implements IJsonDescripted {
	private final String key;
	private String city;
	private double lat, lng;
	private String name, address, type;
	private int users;
	private String description;
	private String descriptionKey;
	private String descriptionLanguage;
	private JsonMedia thumb;
	private List<JsonMedia> otherImages = new ArrayList<JsonMedia>();
	private List<JsonFacet> likesFacets = Collections.emptyList();
	private List<JsonFacet> usersFacets = Collections.emptyList();
	private int commentsCount = 0;
	private int closedReportsCount = 0;
	private List<JsonLightUser> inUsers = new ArrayList<JsonLightUser>();
	private List<String> tags = new ArrayList<String>();
	private List<JsonLightEvent> events = new ArrayList<JsonLightEvent>();
	private Collection<JsonHour> hours = new ArrayList<JsonHour>();

	public JsonOverviewElement(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setLikesFacets(List<JsonFacet> likesFacets) {
		this.likesFacets = likesFacets;
	}

	public void setUsersFacets(List<JsonFacet> usersFacets) {
		this.usersFacets = usersFacets;
	}

	public List<JsonFacet> getLikesFacets() {
		return likesFacets;
	}

	public List<JsonFacet> getUsersFacets() {
		return usersFacets;
	}

	public void setUsers(int users) {
		this.users = users;
	}

	public int getUsers() {
		return users;
	}

	public void addTag(String tagCode) {
		tags.add(tagCode);
	}

	public List<String> getTags() {
		return tags;
	}

	public void addInUser(JsonLightUser user) {
		inUsers.add(user);
	}

	public List<JsonLightUser> getInUsers() {
		return inUsers;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<JsonLightEvent> getEvents() {
		return events;
	}

	public void addEvent(JsonLightEvent event) {
		events.add(event);
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLat() {
		return lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLng() {
		return lng;
	}

	public List<JsonMedia> getOtherImages() {
		return otherImages;
	}

	public void addOtherImage(JsonMedia media) {
		otherImages.add(media);
	}

	public JsonMedia getThumb() {
		return thumb;
	}

	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	public void setClosedReportsCount(int closedReportsCount) {
		this.closedReportsCount = closedReportsCount;
	}

	public int getClosedReportsCount() {
		return closedReportsCount;
	}

	@Override
	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public void setDescriptionLanguage(String descriptionLanguage) {
		this.descriptionLanguage = descriptionLanguage;
	}

	@Override
	public String getDescriptionLanguage() {
		return descriptionLanguage;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public void setHours(Collection<JsonHour> hours) {
		this.hours = hours;
	}

	public Collection<JsonHour> getHours() {
		return hours;
	}
}
