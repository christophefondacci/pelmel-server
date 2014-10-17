package com.videopolis.apis.model;

/**
 * This interface could be implemented by criterion which supports pagination.
 * It simply gives access to pagination information.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface Paginable<T> {

    /**
     * Retrieves the pagination information
     * 
     * @return the {@link PaginationSettings} defined
     */
    PaginationSettings getPagination();

    /**
     * Defines the pagination information
     * 
     * @param pagination
     *            the {@link PaginationSettings} information to define
     */
    void setPagination(PaginationSettings pagination);

    /**
     * Adds the specified pagination settings to this paginable element. This is
     * a convenience method which builds the {@link PaginationSettings} bean
     * from provided information
     * 
     * @param pageSize
     *            the size of one page of results (= the number of elements to
     *            fetch)
     * @param pageOffset
     *            the page offset to start from
     */
    T paginatedBy(int pageSize, int pageOffset);

}
