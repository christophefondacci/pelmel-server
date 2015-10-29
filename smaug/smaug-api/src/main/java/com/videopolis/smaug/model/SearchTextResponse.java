package com.videopolis.smaug.model;

import java.util.List;

/**
 * This interface represents the behavior of response for suggest request.
 * 
 * @author Shoun Ichida
 * @since 11 janv. 2011
 */
public interface SearchTextResponse extends WindowedResponse {

	/**
	 * Returns the list of items this query found
	 * 
	 * @return a list of {@link SearchItem}
	 */
	List<SearchItem> getItems();

	/**
	 * Returns the settings used to issue the request
	 * 
	 * @return the {@link SearchTextSettings}
	 */
	SearchTextSettings getSuggestSettings();

}
