package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * This JSON bean represents a single conversation between 2 (and only 2)
 * persons.
 * 
 * @author cfondacci
 * 
 */
public class JsonOneToOneMessageList extends JsonMessagingStatistic {

	private JsonLightUser fromUser;
	private JsonLightUser toUser;

	private List<JsonMessage> messages = new ArrayList<JsonMessage>();

	public void setFromUser(JsonLightUser fromUser) {
		this.fromUser = fromUser;
	}

	public JsonLightUser getFromUser() {
		return fromUser;
	}

	public void setToUser(JsonLightUser toUser) {
		this.toUser = toUser;
	}

	public JsonLightUser getToUser() {
		return toUser;
	}

	public List<JsonMessage> getMessages() {
		return messages;
	}

	public void addMessage(JsonMessage message) {
		messages.add(message);
	}
}
