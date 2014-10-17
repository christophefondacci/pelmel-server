package com.videopolis.smaug.exception;

/**
 * An exception thrown when an error prevented a search to be completed
 * 
 * @author julien
 * 
 */
public class SearchException extends RuntimeException {

	/** Serial version UID */
	private static final long serialVersionUID = 8818014155448870591L;

	/**
	 * Default Ctor
	 */
	public SearchException() {
	}

	/**
	 * Ctor with message
	 * 
	 * @param message
	 *            The message of the exception
	 */
	public SearchException(String message) {
		super(message);
	}

	/**
	 * Ctor with throwable cause
	 * 
	 * @param cause
	 *            The cause of the exception
	 */
	public SearchException(Throwable cause) {
		super(cause);
	}

	/**
	 * Ctor with message and the throwable cause
	 * 
	 * @param message
	 *            The message of the exception
	 * @param cause
	 *            The cause of the exception
	 */
	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

}
