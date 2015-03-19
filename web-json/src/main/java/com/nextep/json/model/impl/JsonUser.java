package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.nextep.json.model.IJsonLightEvent;

public class JsonUser extends JsonLiker {

	private String key;
	private String nxtpUserToken;
	private String pseudo;
	private String city;
	private Integer heightInCm;
	private Integer weightInKg;
	private Long birthDate;
	private boolean online;
	private Collection<JsonDescription> descriptions = new ArrayList<JsonDescription>();
	// private String description;
	private Collection<JsonMedia> medias = new ArrayList<JsonMedia>();
	private Collection<String> tags = new ArrayList<String>();
	private List<JsonLightPlace> likedPlaces = new ArrayList<JsonLightPlace>();
	private int likedPlacesCount;
	private List<JsonLightPlace> checkedInPlaces = new ArrayList<JsonLightPlace>();
	private List<IJsonLightEvent> events = new ArrayList<IJsonLightEvent>();
	private int checkedInPlacesCount;
	private JsonLightPlace lastLocation;
	private Long lastLocationTime;
	private boolean newUser;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNxtpUserToken() {
		return nxtpUserToken;
	}

	public void setNxtpUserToken(String nxtpUserToken) {
		this.nxtpUserToken = nxtpUserToken;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public Integer getHeightInCm() {
		return heightInCm;
	}

	public void setHeightInCm(Integer heightInCm) {
		this.heightInCm = heightInCm;
	}

	public Integer getWeightInKg() {
		return weightInKg;
	}

	public void setWeightInKg(Integer weightInKg) {
		this.weightInKg = weightInKg;
	}

	public Long getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Long birthDateTime) {
		this.birthDate = birthDateTime;
	}

	public void setBirthDateValue(Date birthDate) {
		this.birthDate = birthDate.getTime() / 1000;
	}

	public Collection<JsonDescription> getDescriptions() {
		return descriptions;
	}

	public void addDescriptions(JsonDescription description) {
		this.descriptions.add(description);
	}

	// public void setDescription(String description) {
	// this.description = description;
	// }
	//
	// public String getDescription() {
	// return description;
	// }

	public Collection<JsonMedia> getMedias() {
		return medias;
	}

	public void addMedia(JsonMedia m) {
		medias.add(m);
	}

	public Collection<String> getTags() {
		return tags;
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public boolean isOnline() {
		return online;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public List<JsonLightPlace> getLikedPlaces() {
		return likedPlaces;
	}

	public void addLikedPlace(JsonLightPlace place) {
		likedPlaces.add(place);
	}

	public void setLikedPlacesCount(int likedPlacesCount) {
		this.likedPlacesCount = likedPlacesCount;
	}

	public int getLikedPlacesCount() {
		return likedPlacesCount;
	}

	public void setLastLocation(JsonLightPlace lastLocation) {
		this.lastLocation = lastLocation;
	}

	public JsonLightPlace getLastLocation() {
		return lastLocation;
	}

	public Long getLastLocationTime() {
		return lastLocationTime;
	}

	public void setLastLocationTime(Long lastLocationTime) {
		this.lastLocationTime = lastLocationTime;
	}

	public void setLastLocationTimeValue(Date lastLocationTime) {
		this.lastLocationTime = lastLocationTime.getTime() / 1000;
	}

	public void setCheckedInPlacesCount(int checkedInPlacesCount) {
		this.checkedInPlacesCount = checkedInPlacesCount;
	}

	public int getCheckedInPlacesCount() {
		return checkedInPlacesCount;
	}

	public List<JsonLightPlace> getCheckedInPlaces() {
		return checkedInPlaces;
	}

	public void setCheckedInPlaces(List<JsonLightPlace> checkedInPlaces) {
		this.checkedInPlaces = checkedInPlaces;
	}

	public void addCheckedInPlace(JsonLightPlace checkedInPlace) {
		this.checkedInPlaces.add(checkedInPlace);
	}

	public boolean isNewUser() {
		return newUser;
	}

	public void setNewUser(boolean newUser) {
		this.newUser = newUser;
	}

	public void setEvents(List<IJsonLightEvent> events) {
		this.events = events;
	}

	public List<IJsonLightEvent> getEvents() {
		return events;
	}

	public void addEvent(IJsonLightEvent event) {
		events.add(event);
	}
}
