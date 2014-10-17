package com.videopolis.apis.cals.impl;

import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.model.SearchWindowResponse;

/**
 * This class adapts a {@link SearchWindowResponse} into a
 * {@link PaginationInfo}.
 * 
 * @author Christophe Fondacci
 * 
 */
public class PaginationInfoAdapter implements PaginationInfo {

    private final SearchWindowResponse window;

    public PaginationInfoAdapter(final SearchWindowResponse window) {
	this.window = window;
    }

    @Override
    public int getPageCount() {
	if (window.getItemCount() == 0) {
	    return 0;
	} else if (window.getItemsPerPage() == 0) {
	    // Safety precaution to avoid division by 0
	    return 0;
	} else {
	    int pageCount = window.getItemCount() / window.getItemsPerPage();
	    if (window.getItemCount() % window.getItemsPerPage() != 0) {
		pageCount++;
	    }
	    return pageCount;
	}
    }

    @Override
    public int getItemCount() {
	return window.getItemCount();
    }

    @Override
    public int getCurrentPageNumber() {
	return window.getPageNumber();
    }

    @Override
    public int getItemCountPerPage() {
	return window.getItemsPerPage();
    }

}
