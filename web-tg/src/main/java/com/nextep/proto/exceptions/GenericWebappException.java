package com.nextep.proto.exceptions;

public class GenericWebappException extends Exception {

	private static final long serialVersionUID = 8593522044032370418L;

	public GenericWebappException(String message) {
		super(message);
	}

	public GenericWebappException(String message, Throwable cause) {
		super(message, cause);
	}
}
