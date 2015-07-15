package com.nextep.json.model.impl;

/**
 * A base bean providing default message statistic info.
 * 
 * @author cfondacci
 * 
 */
public class JsonMessagingStatistic {

	private int unreadMsgCount = 0;
	private int unreadNetworkNotificationsCount = 0;
	private long maxActivityId = 0;
	private int page = 0;
	private int pageSize = 0;
	private int totalMsgCount = 0;

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadNetworkNotificationsCount(int unreadNetworkNotificationsCount) {
		this.unreadNetworkNotificationsCount = unreadNetworkNotificationsCount;
	}

	public int getUnreadNetworkNotificationsCount() {
		return unreadNetworkNotificationsCount;
	}

	public void setTotalMsgCount(int totalMsgCount) {
		this.totalMsgCount = totalMsgCount;
	}

	public int getTotalMsgCount() {
		return totalMsgCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setMaxActivityId(long maxActivityId) {
		this.maxActivityId = maxActivityId;
	}

	public long getMaxActivityId() {
		return maxActivityId;
	}
}
