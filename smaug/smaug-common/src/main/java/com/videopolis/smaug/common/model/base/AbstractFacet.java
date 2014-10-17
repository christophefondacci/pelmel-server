package com.videopolis.smaug.common.model.base;

import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;

/**
 * Base implementation for all {@link Facet} and derived classes.
 *
 * @author julien
 *
 */
public abstract class AbstractFacet implements Facet {

    /** The facet's category */
    private FacetCategory facetCategory;

    /**
     * Sets the facet's category
     *
     * @param facetCategory
     *            Category
     */
    public void setFacetCategory(final FacetCategory facetCategory) {
	this.facetCategory = facetCategory;
    }

    @Override
    public FacetCategory getFacetCategory() {
	return facetCategory;
    }

    @Override
    public String toString() {
	return facetCategory.getCategoryCode();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ (facetCategory == null ? 0 : facetCategory.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final AbstractFacet other = (AbstractFacet) obj;
	if (facetCategory == null) {
	    if (other.facetCategory != null) {
		return false;
	    }
	} else if (!facetCategory.equals(other.facetCategory)) {
	    return false;
	}
	return true;
    }
}
