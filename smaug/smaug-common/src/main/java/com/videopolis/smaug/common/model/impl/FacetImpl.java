package com.videopolis.smaug.common.model.impl;

import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.base.AbstractFacet;

/**
 * Default facet implementation
 * 
 * @author Christophe Fondacci
 * @author mehdi BEN HAJ ABBES overrode equals and hashCode
 * 
 */
public class FacetImpl extends AbstractFacet {

    private String facetCode;

    @Override
    public String getFacetCode() {
	return facetCode;
    }

    public void setFacetCode(final String facetCode) {
	this.facetCode = facetCode;
    }

    /**
     * more expressive while testing
     * 
     * @author mehdi BEN HAJ ABBES
     * @since 07 Jan 2011
     */
    @Override
    public String toString() {
	return super.toString() + "_" + facetCode;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result
		+ (facetCode == null ? 0 : facetCode.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!super.equals(obj)) {
	    return false;
	}
	final Facet other = (Facet) obj;
	if (facetCode == null) {
	    return other.getFacetCode() == null;
	} else {
	    return facetCode.equals(other.getFacetCode());
	}
    }
}
