package com.videopolis.smaug.model.impl;

import com.videopolis.smaug.model.SearchWindow;

/**
 * An immutable implementatino of {@link SearchWindow}.
 * 
 * @author julien
 * 
 *         I needed to add setters and for convenience the equals, hashCode and
 *         toString
 * @author mehdi BEN HAJ ABBES
 * 
 */
public class SearchWindowImpl implements SearchWindow {

	/** Count of item per page. */
	private int itemsPerPage;
	/** Number of page. */
	private int pageNumber;

	/**
	 * for spring creation facility
	 * 
	 * @author mehdi BEN HAJ ABBES
	 */
	public SearchWindowImpl() {
		super();
	}

	/**
	 * Build a new SearchWindowImpl instance.
	 * 
	 * @param itemsPerPage
	 *            Items per page
	 * @param pageNumber
	 *            Page number
	 */
	public SearchWindowImpl(int itemsPerPage, int pageNumber) {
		super();
		this.itemsPerPage = itemsPerPage;
		this.pageNumber = pageNumber;
	}

	@Override
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * @author mehdi BEN HAJ ABBES
	 */
	public void setItemsPerPage(final int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	/**
	 * @author mehdi BEN HAJ ABBES
	 */
	public void setPageNumber(final int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * @author mehdi BEN HAJ ABBES
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + itemsPerPage;
		result = prime * result + pageNumber;
		return result;
	}

	/**
	 * @author mehdi BEN HAJ ABBES
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SearchWindowImpl other = (SearchWindowImpl) obj;
		if (itemsPerPage != other.itemsPerPage) {
			return false;
		}
		if (pageNumber != other.pageNumber) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SearchWindowImpl [itemsPerPage=" + itemsPerPage
				+ ", pageNumber=" + pageNumber + "]";
	}

}
