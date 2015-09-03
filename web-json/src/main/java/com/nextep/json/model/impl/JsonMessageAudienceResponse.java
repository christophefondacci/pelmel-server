package com.nextep.json.model.impl;

import java.util.Date;

public class JsonMessageAudienceResponse {

	private boolean messageSent;
	private int usersReached;
	private Long nextAnnouncementTime;
	private boolean ownershipError;

	public boolean isMessageSent() {
		return messageSent;
	}

	public void setMessageSent(boolean messageSent) {
		this.messageSent = messageSent;
	}

	public int getUsersReached() {
		return usersReached;
	}

	public void setUsersReached(int usersReached) {
		this.usersReached = usersReached;
	}

	public void setNextAnnouncementTime(long nextAnnouncementTime) {
		this.nextAnnouncementTime = nextAnnouncementTime;
	}

	public long getNextAnnouncementTime() {
		return nextAnnouncementTime == null ? 0 : nextAnnouncementTime.longValue();
	}

	public void setNextAnnouncementDate(Date nextAnnouncementDate) {
		this.nextAnnouncementTime = nextAnnouncementDate == null ? null : nextAnnouncementDate.getTime();
	}

	public void setOwnershipError(boolean errorNotOwner) {
		this.ownershipError = errorNotOwner;
	}

	public boolean isOwnershipError() {
		return ownershipError;
	}
}
