package com.nextep.proto.blocks;

import java.util.List;

public interface PaginationSupport {

	/**
	 * Retrieves the list of pages that should be displayed for search results
	 * page navigation
	 * 
	 * @return a list of integer corresponding to the page numbers to display
	 */
	List<Integer> getPagesList();

	/**
	 * Indicates the current page number
	 * 
	 * @return the current page number
	 */
	Integer getCurrentPage();

	/**
	 * Returns the current search URL for the specified page. Returned URL will
	 * contain the exact same search arguments (facetting, filters, etc.) except
	 * that it will set the page number specified
	 * 
	 * @param page
	 *            the page number to set in the search request
	 * @return the search URL
	 */
	String getPageUrl(int page);

}