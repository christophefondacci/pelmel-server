package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.nextep.json.model.IJsonLightUser;

/**
 * This JSON bean represents a single conversation between 2 (and only 2)
 * persons.
 * 
 * @author cfondacci
 * 
 */
public class JsonOneToOneMessageList extends JsonMessagingStatistic {

	private IJsonLightUser fromUser;
	private IJsonLightUser toUser;

	private List<JsonMessage> messages = new ArrayList<JsonMessage>();

	public void setFromUser(IJsonLightUser fromUser) {
		this.fromUser = fromUser;
	}

	public IJsonLightUser getFromUser() {
		return fromUser;
	}

	public void setToUser(IJsonLightUser toUser) {
		this.toUser = toUser;
	}

	public IJsonLightUser getToUser() {
		return toUser;
	}

	public List<JsonMessage> getMessages() {
		return messages;
	}

	public void addMessage(JsonMessage message) {
		messages.add(message);
	}
}
