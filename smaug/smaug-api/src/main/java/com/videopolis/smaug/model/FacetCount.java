package com.videopolis.smaug.model;

import com.videopolis.smaug.common.model.Facet;

/**
 * The interface defines a facet count, which is literally the association
 * between a facet and the number of elements which "have" this facet.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface FacetCount {

    /**
     * Retrieves the facet part of the {@link FacetCount}
     * 
     * @return the {@link Facet} for which this count is returned
     */
    Facet getFacet();

    /**
     * Retrieves the number of elements proposing this facet
     * 
     * @return the number of elements which have this facet
     */
    int getCount();
}
