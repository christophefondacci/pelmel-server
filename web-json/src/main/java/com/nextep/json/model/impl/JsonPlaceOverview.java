package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nextep.json.model.IJsonDescripted;
import com.nextep.json.model.IJsonPlace;

public class JsonPlaceOverview extends JsonLiker implements IJsonDescripted,
		IJsonPlace {
	private String key;
	private String city;
	private String timezoneId;
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
	private Collection<JsonProperty> properties = new ArrayList<JsonProperty>();

	public JsonPlaceOverview(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
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

	@Override
	public void addTag(String tagCode) {
		tags.add(tagCode);
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	public void addInUser(JsonLightUser user) {
		inUsers.add(user);
	}

	public List<JsonLightUser> getInUsers() {
		return inUsers;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
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

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public double getLng() {
		return lng;
	}

	@Override
	public List<JsonMedia> getOtherImages() {
		return otherImages;
	}

	@Override
	public void addOtherImage(JsonMedia media) {
		otherImages.add(media);
	}

	@Override
	public JsonMedia getThumb() {
		return thumb;
	}

	@Override
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

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String getCity() {
		return city;
	}

	public void setHours(Collection<JsonHour> hours) {
		this.hours = hours;
	}

	public Collection<JsonHour> getHours() {
		return hours;
	}

	@Override
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	@Override
	public String getTimezoneId() {
		return timezoneId;
	}

	public Collection<JsonProperty> getProperties() {
		return properties;
	}

	public void addProperty(JsonProperty property) {
		properties.add(property);
	}
}
