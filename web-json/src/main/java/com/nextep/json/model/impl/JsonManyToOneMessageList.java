package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a JSON structure which wraps the definition of all
 * messages sent to current user along with the description of every referenced
 * user.
 * 
 * @author cfondacci
 * 
 */
public class JsonManyToOneMessageList extends JsonMessagingStatistic {

	private List<JsonLightUser> users = new ArrayList<JsonLightUser>();
	private List<JsonMessage> messages = new ArrayList<JsonMessage>();
	private List<JsonRecipientsGroup> recipientsGroups = new ArrayList<JsonRecipientsGroup>();

	public void addUser(JsonLightUser user) {
		users.add(user);
	}

	public List<JsonLightUser> getUsers() {
		return users;
	}

	public void addMessage(JsonMessage message) {
		messages.add(message);
	}

	public List<JsonMessage> getMessages() {
		return messages;
	}

	public List<JsonRecipientsGroup> getRecipientsGroups() {
		return recipientsGroups;
	}

	public void setRecipientsGroups(List<JsonRecipientsGroup> recipientsGroups) {
		this.recipientsGroups = recipientsGroups;
	}

	public void addRecipientsGroup(JsonRecipientsGroup recipientGroup) {
		this.recipientsGroups.add(recipientGroup);
	}
}
