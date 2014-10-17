package com.videopolis.cals.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginatedItemsResponse;

/**
 * Default implementation of a {@link PaginatedItemsResponse}. It enforces the
 * presence of all required data through an explicit constructor.
 * 
 * @see PaginatedItemsResponse
 * @author Christophe Fondacci
 */
public final class PaginatedItemsResponseImpl implements PaginatedItemsResponse {

    /** Serialization UID */
    private static final long serialVersionUID = -7706414325658545513L;

    private int currentPage;
    private int itemCount;
    private int pageCount;
    private int itemsPerPage;
    private List<CalmObject> items;
    private Date lastUpdateTimestamp = null;

    /**
     * Enforcing current page and items per page in this constructor. Callers
     * should pass the requested page and the requested number of items per page
     * so that the framework can assume this information will always remain
     * available.
     * 
     * @param currentPage
     *            current page number
     * @param itemsPerPage
     *            number of items per page
     */
    public PaginatedItemsResponseImpl(int itemsPerPage, int currentPage) {
	this.currentPage = currentPage;
	this.itemsPerPage = itemsPerPage;
	this.itemCount = 0;
	this.pageCount = 0;
	items = new ArrayList<CalmObject>();
    }

    /**
     * Defines the total number of items available.
     * 
     * @param itemCount
     *            total item count
     */
    public void setItemCount(int itemCount) {
	this.itemCount = itemCount;
    }

    /**
     * Defines the total number of pages needed to display all items.
     * 
     * @param pageCount
     *            total page count
     */
    public void setPageCount(int pageCount) {
	this.pageCount = pageCount;
    }

    /**
     * Defines all items of this response
     * 
     * @param items
     *            items of this response
     * @throws IllegalArgumentException
     *             when the collection is null
     */
    @SuppressWarnings("unchecked")
    public void setItems(List<? extends CalmObject> items) {
	if (items == null) {
	    throw new IllegalArgumentException("Argument must not be null");
	}
	// Because of Eclipse compiler bug, we cannot have Collection<? extends
	// CalmObject> as our class variable, javac rocks, eclipse compiler
	// sucks
	this.items = (List<CalmObject>) items;
    }

    /**
     * Adds an item to the existing set of items of this response. This is a
     * helper method.
     * 
     * @param item
     *            item to add to the current result
     */
    public void addItem(CalmObject item) {
	items.add(item);
    }

    @Override
    public int getCurrentPageNumber() {
	return currentPage;
    }

    @Override
    public List<? extends CalmObject> getItems() {
	return items;
    }

    @Override
    public int getItemCount() {
	return itemCount;
    }

    @Override
    public int getItemCountPerPage() {
	return itemsPerPage;
    }

    @Override
    public int getPageCount() {
	return pageCount;
    }

    @Override
    public Date getLastUpdateTimestamp() {
	return lastUpdateTimestamp;
    }

    @Override
    public void setLastUpdateTimestamp(Date lastUpdateTimestamp) {
	this.lastUpdateTimestamp = lastUpdateTimestamp;
    }
}
