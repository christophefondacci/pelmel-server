package com.nextep.json.model.impl;

import java.util.Date;

public class JsonMessage {
	private long time;
	private String fromKey;
	private String toKey;
	private String message;

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

}
