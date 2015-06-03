package com.nextep.json.model.impl;

public class JsonCheckinResponse {

	private String previousPlaceKey;
	private int previousPlaceUsersCount;
	private String newPlaceKey;
	private int newPlaceUsersCount;

	public String getPreviousPlaceKey() {
		return previousPlaceKey;
	}

	public void setPreviousPlaceKey(String previousPlaceKey) {
		this.previousPlaceKey = previousPlaceKey;
	}

	public int getPreviousPlaceUsersCount() {
		return previousPlaceUsersCount;
	}

	public void setPreviousPlaceUsersCount(int previousPlaceUsersCount) {
		this.previousPlaceUsersCount = previousPlaceUsersCount;
	}

	public String getNewPlaceKey() {
		return newPlaceKey;
	}

	public void setNewPlaceKey(String newPlaceKey) {
		this.newPlaceKey = newPlaceKey;
	}

	public int getNewPlaceUsersCount() {
		return newPlaceUsersCount;
	}

	public void setNewPlaceUsersCount(int newPlaceUsersCount) {
		this.newPlaceUsersCount = newPlaceUsersCount;
	}

}
