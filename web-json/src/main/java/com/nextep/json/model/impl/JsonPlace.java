package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.nextep.json.model.IJsonPlace;

public class JsonPlace implements IJsonPlace {

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
	private String timezoneId;
	private List<JsonSpecialEvent> specials = new ArrayList<JsonSpecialEvent>();

	public JsonPlace(String name) {
		this.name = name;
		tags = new ArrayList<String>();
	}

	/**
	 * @deprecated here for compatibility with pre 2.1, use getKey / setKey
	 * @param itemKey
	 */
	@Deprecated
	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	/**
	 * @deprecated here for compatibility with pre 2.1, use getKey / setKey
	 * @return itemKey
	 */
	@Deprecated
	public String getItemKey() {
		return itemKey;
	}

	public String getDistance() {
		return distance;
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
	public String getCity() {
		return city;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	@Override
	public void addTag(String tagId) {
		tags.add(tagId);
	}

	@Override
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

	@Override
	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	@Override
	public JsonMedia getThumb() {
		return thumb;
	}

	@Override
	public List<JsonMedia> getOtherImages() {
		return otherImages;
	}

	@Override
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

	@Override
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}

	@Override
	public String getTimezoneId() {
		return timezoneId;
	}

	@Override
	public String getKey() {
		return itemKey;
	}

	@Override
	public void setKey(String key) {
		this.itemKey = key;
	}
}
