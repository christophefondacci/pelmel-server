package com.nextep.json.model.impl;

public class JsonSpecialEvent {

	private String key;
	private String type;
	private Long nextStart;
	private Long nextEnd;
	private String description;
	private String name;
	private JsonMedia thumb;
	private int participants;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getNextStart() {
		return nextStart;
	}

	public void setNextStart(Long nextStart) {
		this.nextStart = nextStart;
	}

	public Long getNextEnd() {
		return nextEnd;
	}

	public void setNextEnd(Long nextEnd) {
		this.nextEnd = nextEnd;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JsonMedia getThumb() {
		return thumb;
	}

	public void setThumb(JsonMedia thumb) {
		this.thumb = thumb;
	}

	public void setParticipants(int participants) {
		this.participants = participants;
	}

	public int getParticipants() {
		return participants;
	}
}
