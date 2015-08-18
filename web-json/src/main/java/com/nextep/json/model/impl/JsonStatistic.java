package com.nextep.json.model.impl;

import java.util.Date;

public class JsonStatistic {

	// private String itemKey;
	private long statDate;
	private String statType;
	private int count;

	// public String getItemKey() {
	// return itemKey;
	// }
	//
	// public void setItemKey(String itemKey) {
	// this.itemKey = itemKey;
	// }

	public long getStatDate() {
		return statDate;
	}

	public void setStatDate(long statDate) {
		this.statDate = statDate;
	}

	public void setStatDateValue(Date date) {
		this.statDate = date.getTime() / 1000;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
