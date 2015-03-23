package com.nextep.json.model.impl;

import com.nextep.json.model.IJsonDescripted;

public class JsonHour implements IJsonDescripted {

	private String key;
	private String name;
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;
	private boolean monday, tuesday, wednesday, thursday, friday, saturday,
			sunday;
	private Long nextStart;
	private Long nextEnd;
	private Integer recurrency;
	private String type;
	private String description;
	private String descriptionKey;
	private String descriptionLanguage;
	private JsonMedia thumb;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	public boolean isMonday() {
		return monday;
	}

	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	public boolean isTuesday() {
		return tuesday;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	public boolean isWednesday() {
		return wednesday;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	public boolean isThursday() {
		return thursday;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	public boolean isFriday() {
		return friday;
	}

	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public boolean isSunday() {
		return sunday;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	public Integer getRecurrency() {
		return recurrency;
	}

	public void setRecurrency(Integer recurrency) {
		this.recurrency = recurrency;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getDescriptionKey() {
		return descriptionKey;
	}

	@Override
	public void setDescriptionKey(String descriptionKey) {
		this.descriptionKey = descriptionKey;
	}

	@Override
	public String getDescriptionLanguage() {
		return descriptionLanguage;
	}

	@Override
	public void setDescriptionLanguage(String descriptionLanguage) {
		this.descriptionLanguage = descriptionLanguage;
	}

	public void setNextStart(Long nextStart) {
		this.nextStart = nextStart;
	}

	public Long getNextStart() {
		return nextStart;
	}

	public void setNextEnd(Long nextEnd) {
		this.nextEnd = nextEnd;
	}

	public Long getNextEnd() {
		return nextEnd;
	}

	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	public JsonMedia getThumb() {
		return thumb;
	}
}
