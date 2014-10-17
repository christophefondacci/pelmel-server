package com.videopolis.cals.model;

/**
 * Interface which defines a pagination request settings, which are a specific
 * case of {@link RequestSettings} which add support to pagination.
 *
 * @author julien
 *
 */
public interface PaginationRequestSettings extends RequestSettings {

    /**
     * Returns the number of page to be returned
     *
     * @return Page number
     */
    int getPageNumber();

    /**
     * Returns the number of results to be included in one page
     *
     * @return Page size
     */
    int getResultsPerPage();
}
