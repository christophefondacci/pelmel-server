package com.nextep.json.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.nextep.json.model.IJsonLightUser;

public class JsonRecipientsGroup {

	private String key;
	private Collection<IJsonLightUser> users = new ArrayList<IJsonLightUser>();

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setUsers(Collection<IJsonLightUser> users) {
		this.users = users;
	}

	public Collection<IJsonLightUser> getUsers() {
		return users;
	}

	public void addUser(IJsonLightUser user) {
		this.users.add(user);
	}
}
