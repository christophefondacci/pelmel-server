package com.nextep.proto.exceptions;

public class UserLoginTimeoutException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5903726848919638204L;

	public UserLoginTimeoutException(String message) {
		super(message);
	}

}
