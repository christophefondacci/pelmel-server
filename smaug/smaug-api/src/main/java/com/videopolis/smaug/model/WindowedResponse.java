package com.videopolis.smaug.model;

/**
 * A generic base interface for every response exposing a search window. Please
 * extend if you can.
 * 
 * @author cfondacci
 *
 */
public interface WindowedResponse {
	/**
	 * Returns information about pagination of the results
	 * 
	 * @return Pagination window {@link SearchWindowResponse}
	 */
	SearchWindowResponse getSearchWindow();
}
