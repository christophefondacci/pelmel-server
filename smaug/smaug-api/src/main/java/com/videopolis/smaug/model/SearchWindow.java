package com.videopolis.smaug.model;

/**
 * <p>
 * A pagination window for a search query.
 * </p>
 * <p>
 * This determines which part of the resulsts are returned
 * </p>
 * 
 * @author julien
 * 
 */
public interface SearchWindow {

    /**
     * Returns the number of items in one result page
     * 
     * @return Number of items
     */
    int getItemsPerPage();

    /**
     * Returns the page number of the current page (0 is first)
     * 
     * @return Page number
     */
    int getPageNumber();
}
