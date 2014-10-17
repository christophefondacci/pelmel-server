package com.videopolis.smaug.common.model;

/**
 * A facet is a possible value of a {@link FacetCategory}.<br>
 * 
 * 
 * @author Christophe Fondacci
 */
public interface Facet {

    /**
     * Informs about the technical code of this facet.
     * 
     * @return the facet's code
     */
    String getFacetCode();

    /**
     * Informs about the category for which this facet exists.
     * 
     * @return the parent {@link FacetCategory} to which this {@link Facet}
     *         belongs
     */
    FacetCategory getFacetCategory();

}
