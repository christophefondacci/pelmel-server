package com.videopolis.smaug.common.model;

import com.videopolis.calm.model.Sorter;

/**
 * This interface describes the specific information about Smaug sorters, in
 * addition to the standard sorting information
 * 
 * @author Christophe Fondacci
 * 
 */
public interface SmaugSorter extends Sorter {

    /**
     * @deprecated Use {@link #getCriterion()} instead
     */
    @Deprecated
    String getSortField();

    /**
     * @deprecated Use {@link #getOrder()} instead
     */
    @Deprecated
    boolean isAscending();

    /**
     * Retrieves the URL code of this sorter.
     * 
     * @return the URL code
     */
    String getUrlCode();

    /**
     * Indicates whether this sorter is sticky. A sticky sorter means that it
     * should always be kept unless explicitly removed. All non-sticky sorters
     * exclude themselves, meaning that adding a non-sticky sorter should remove
     * any other non-sticky sorter.
     * 
     * @return <code>true</code> if this sorter is sticky, else
     *         <code>false</code>
     */
    boolean isSticky();
}
