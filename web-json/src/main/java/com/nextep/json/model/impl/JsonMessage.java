package com.nextep.json.model.impl;

import java.util.Date;

public class JsonMessage {
	private long time;
	private String key;
	private String fromKey;
	private String toKey;
	private String recipientsGroupKey;
	private String message;
	private JsonMedia media;
	private boolean unread;

	public long getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time.getTime() / 1000;
	}

	public String getFromKey() {
		return fromKey;
	}

	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}

	public String getToKey() {
		return toKey;
	}

	public void setToKey(String toKey) {
		this.toKey = toKey.toString();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public JsonMedia getMedia() {
		return media;
	}

	public void setMedia(JsonMedia media) {
		this.media = media;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setRecipientsGroupKey(String recipientsGroupKey) {
		this.recipientsGroupKey = recipientsGroupKey;
	}

	public String getRecipientsGroupKey() {
		return recipientsGroupKey;
	}
}
