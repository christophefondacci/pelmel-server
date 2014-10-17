package com.videopolis.calm.exception;

public class CalRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -245075007233382243L;

	public CalRuntimeException(String message) {
		super(message);
	}

	public CalRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
}
