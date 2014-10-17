package com.nextep.proto.exceptions;

public class ResourceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3513815996186543002L;

	public ResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceException(String message) {
		super(message);
	}
}
