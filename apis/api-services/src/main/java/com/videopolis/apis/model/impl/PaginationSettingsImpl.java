package com.videopolis.apis.model.impl;

import com.videopolis.apis.model.PaginationSettings;

/**
 * {@link PaginationSettings} default implementation
 * 
 * @author Christophe Fondacci
 * 
 */
public class PaginationSettingsImpl implements PaginationSettings {
    /** Number of items by page */
    private int itemsByPage;
    /** Page number to retrieve */
    private int pageNumber;

    @Override
    public int getItemsByPage() {
	return itemsByPage;
    }

    @Override
    public int getPageOffset() {
	return pageNumber;
    }

    @Override
    public void setItemsByPage(int itemsByPage) {
	this.itemsByPage = itemsByPage;
    }

    @Override
    public void setPageOffset(int page) {
	this.pageNumber = page;
    }

}
