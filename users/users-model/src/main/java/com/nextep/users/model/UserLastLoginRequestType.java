package com.nextep.users.model;

import com.videopolis.calm.model.RequestType;

/**
 * This request type allows to query users based on the number of days they
 * haven't logged in. The results can be sorted to return oldest first or newest
 * first
 * 
 * @author cfondacci
 *
 */
public final class UserLastLoginRequestType implements RequestType {

	private static final long serialVersionUID = 1L;
	private int daysWithoutLogin;
	private boolean oldestFirst;

	public UserLastLoginRequestType(int daysWithoutLogin, boolean oldestFirst) {
		this.daysWithoutLogin = daysWithoutLogin;
		this.oldestFirst = oldestFirst;
	}

	public int getDaysWithoutLogin() {
		return daysWithoutLogin;
	}

	public boolean isOldestFirst() {
		return oldestFirst;
	}
}
