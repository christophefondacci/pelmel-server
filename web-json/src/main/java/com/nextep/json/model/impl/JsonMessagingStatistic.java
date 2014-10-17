package com.nextep.json.model.impl;

/**
 * A base bean providing default message statistic info.
 * 
 * @author cfondacci
 * 
 */
public abstract class JsonMessagingStatistic {

	private int unreadMsgCount = 0;

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}
}
