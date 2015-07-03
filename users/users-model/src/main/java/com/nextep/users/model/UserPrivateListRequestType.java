package com.nextep.users.model;

import com.videopolis.calm.model.RequestType;

public class UserPrivateListRequestType implements RequestType {

	public static final String LIST_LIKER = "LIKER";
	public static final String LIST_PRIVATE_NETWORK = "PRIV_1";
	public static final String LIST_REQUESTED = "REQUEST";
	public static final String LIST_PENDING_APPROVAL = "PENDING_APPROVAL";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String listName;

	public UserPrivateListRequestType(String listName) {
		this.listName = listName;
	}

	public String getListName() {
		return listName;
	}

}
