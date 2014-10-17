package com.videopolis.cals.model;

/**
 * This interface provides methods to access pagination information.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface PaginationInfo {

    /**
     * @return the total number of pages that the query would return
     */
    int getPageCount();

    /**
     * @return the total number of items that the query would return
     */
    int getItemCount();

    /**
     * @return the page number of the elements contained in this response
     */
    int getCurrentPageNumber();

    /**
     * @return the number of items per page
     */
    int getItemCountPerPage();
}
