package com.videopolis.smaug.common.helper;

import com.videopolis.smaug.common.exception.SearchReferenceException;

/**
 * This class defines method to check object
 * 
 */
public final class Assert {

	/**
	 * Non visible Ctor for final class
	 */
	private Assert() {
	}

	/**
	 * This method throw an exception with the given message if the given object
	 * is null.
	 * 
	 * @param o
	 *            The object to test
	 * @param message
	 *            The message to throw
	 * @throws SearchReferenceException
	 */
	public static void notNull(Object o, String message)
			throws SearchReferenceException {
		if (o == null) {
			throw new SearchReferenceException(message);
		}
	}
}
