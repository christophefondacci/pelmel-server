package com.videopolis.smaug.common.exception;

/**
 * Exception for search reference
 * 
 * @author Shoun Ichida
 * 
 */
public class SearchReferenceException extends Exception {

	/** Serial version UID. */
	private static final long serialVersionUID = 4786397258807953937L;

	/**
	 * Ctor with message
	 * 
	 * @param message
	 *            The message of the exception
	 */
	public SearchReferenceException(String message) {
		super(message);
	}

	/**
	 * Ctor with message and cause
	 * 
	 * @param message
	 *            The message
	 * @param cause
	 *            The cause
	 */
	public SearchReferenceException(String message, Exception cause) {
		super(message, cause);
	}
}
