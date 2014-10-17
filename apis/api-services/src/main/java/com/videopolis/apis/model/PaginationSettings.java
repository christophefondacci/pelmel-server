package com.videopolis.apis.model;

/**
 * Pagination settings allows to specify the way results fetched by an
 * {@link ApisRequest} are paginated. It is defined by the number of elements by
 * page and a page number.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface PaginationSettings {

    /**
     * @return the number of elements of a single page of results
     */
    int getItemsByPage();

    /**
     * Defines the number of elements of a single page of result
     * 
     * @param itemsByPage
     *            number of items by page
     */
    void setItemsByPage(int itemsByPage);

    /**
     * @return the page number to retrieve
     */
    int getPageOffset();

    /**
     * Defines the page number to retrieve
     * 
     * @param offset
     *            the offset of the first element that will be retrieved,
     *            starting at 0 as it is an offset
     */
    void setPageOffset(int offset);
}
