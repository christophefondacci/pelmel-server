package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

public class JsonPlace {

	private String itemKey, name, address, city, description, type;
	private JsonMedia thumb;
	private double lat, lng;
	private String distance;
	private double rawDistance;
	private int likesCount;
	private int usersCount;
	private List<String> tags;
	private List<JsonMedia> otherImages = new ArrayList<JsonMedia>();
	private int boostValue;
	private int closedReportsCount;
	private List<JsonSpecialEvent> specials = new ArrayList<JsonSpecialEvent>();

	public JsonPlace(String name) {
		this.name = name;
		tags = new ArrayList<String>();
	}

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getItemKey() {
		return itemKey;
	}

	public String getDistance() {
		return distance;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void addTag(String tagId) {
		tags.add(tagId);
	}

	public List<String> getTags() {
		return tags;
	}

	public int getLikesCount() {
		return likesCount;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setLikesCount(int likesCount) {
		this.likesCount = likesCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	public JsonMedia getThumb() {
		return thumb;
	}

	public List<JsonMedia> getOtherImages() {
		return otherImages;
	}

	public void addOtherImage(JsonMedia media) {
		otherImages.add(media);
	}

	public double getRawDistance() {
		return rawDistance;
	}

	public void setRawDistance(double rawDistance) {
		this.rawDistance = rawDistance;
	}

	public int getBoostValue() {
		return boostValue;
	}

	public void setBoostValue(int boostValue) {
		this.boostValue = boostValue;
	}

	public List<JsonSpecialEvent> getSpecials() {
		return specials;
	}

	public void addSpecial(JsonSpecialEvent event) {
		specials.add(event);
	}

	public void setClosedReportsCount(int closedReportsCount) {
		this.closedReportsCount = closedReportsCount;
	}

	public int getClosedReportsCount() {
		return closedReportsCount;
	}
}
