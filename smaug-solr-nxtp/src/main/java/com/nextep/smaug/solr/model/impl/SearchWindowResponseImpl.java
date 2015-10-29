package com.nextep.smaug.solr.model.impl;

import com.videopolis.smaug.model.SearchWindowResponse;

/**
 * Implementation of {@link SearchWindowResponse} interface
 * 
 * @author Shoun Ichida
 * 
 */
public class SearchWindowResponseImpl implements SearchWindowResponse {
	/** The number of pages in response. */
	private final int pageNumber;
	/** The number of items per page. */
	private final int itemPerPage;
	/** The total number of avalaible items. */
	private final int itemCount;

	/**
	 * Ctor
	 * 
	 * @param pageNumber
	 *            The number of pages to set
	 * @param itemPerPage
	 *            The number of items per page to set
	 * @param itemCount
	 *            The total number of items to set
	 */
	public SearchWindowResponseImpl(int pageNumber, int itemPerPage, int itemCount) {
		this.pageNumber = pageNumber;
		this.itemPerPage = itemPerPage;
		this.itemCount = itemCount;
	}

	@Override
	public int getItemCount() {
		return this.itemCount;
	}

	@Override
	public int getItemsPerPage() {
		return this.itemPerPage;
	}

	@Override
	public int getPageNumber() {
		return this.pageNumber;
	}

}
