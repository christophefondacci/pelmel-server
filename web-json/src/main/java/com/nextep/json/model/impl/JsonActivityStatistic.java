package com.nextep.json.model.impl;

public class JsonActivityStatistic {

	/** Total number of elements */
	private int totalCount;
	/**
	 * partial count when listed as "(...) and 2 others", 2 is the partial count
	 */
	private int partialCount;
	/**
	 * names when partial listing "Hunger, Hiro and 2 others", Hunger, Hiro are
	 * the partial names
	 */
	private String partialNames;
	private String activityType;
	private long lastId;
	private JsonMedia media;

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPartialCount() {
		return partialCount;
	}

	public void setPartialCount(int partialCount) {
		this.partialCount = partialCount;
	}

	public String getPartialNames() {
		return partialNames;
	}

	public void setPartialNames(String partialNames) {
		this.partialNames = partialNames;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public JsonMedia getMedia() {
		return media;
	}

	public void setMedia(JsonMedia media) {
		this.media = media;
	}

	public void setLastId(long lastId) {
		this.lastId = lastId;
	}

	public long getLastId() {
		return lastId;
	}
}
