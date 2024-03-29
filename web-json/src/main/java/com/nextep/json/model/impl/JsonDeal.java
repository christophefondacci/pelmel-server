package com.nextep.json.model.impl;

import java.util.Date;

public class JsonDeal {

	private String key;
	private String status;
	private String type;
	private Long startDate;
	private String relatedItemKey;
	private int usedToday;
	private int maxUses;
	private Long lastUsedTime;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public void setStartDateValue(Date date) {
		this.startDate = date == null ? null : date.getTime() / 1000;
	}

	public String getRelatedItemKey() {
		return relatedItemKey;
	}

	public void setRelatedItemKey(String relatedItemKey) {
		this.relatedItemKey = relatedItemKey;
	}

	public void setUsedToday(int usedToday) {
		this.usedToday = usedToday;
	}

	public int getUsedToday() {
		return usedToday;
	}

	public void setMaxUses(int maxUses) {
		this.maxUses = maxUses;
	}

	public int getMaxUses() {
		return maxUses;
	}

	public Long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(Long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public void setLastUsedDate(Date lastUsedDate) {
		this.lastUsedTime = lastUsedDate.getTime() / 1000;
	}
}
