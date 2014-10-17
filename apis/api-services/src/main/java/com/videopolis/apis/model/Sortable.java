package com.videopolis.apis.model;

import java.util.List;

import com.videopolis.calm.model.Sorter;

/**
 * This interface defines criterion which could be sorted. It simply gives
 * access to sort information
 * 
 * @author Christophe Fondacci
 * 
 */
public interface Sortable<T> {

    /**
     * Retrieves the list of sorters defined for this Sortable element
     * 
     * @return a list of all defined {@link Sorter} (might be empty)
     */
    List<Sorter> getSorters();

    /**
     * Adds the specified sorters to this {@link Sortable} element. This method
     * provides a vararg entrypoint for callers convenience.
     * 
     * @param sorters
     *            a vararg array of {@link Sorter}
     */
    <U extends Sorter> T sortBy(U... sorters);

    /**
     * Adds the specified list of sorters to this sortable element.
     * 
     * @param sorters
     *            the list of {@link Sorter} to use
     * @return the {@link Sortable} element
     */
    <U extends Sorter> T sortBy(List<U> sorters);
}
