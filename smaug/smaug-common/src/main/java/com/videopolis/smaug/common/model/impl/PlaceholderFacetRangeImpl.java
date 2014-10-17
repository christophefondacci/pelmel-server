package com.videopolis.smaug.common.model.impl;

import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.base.AbstractFacet;

public class PlaceholderFacetRangeImpl extends AbstractFacet implements
	FacetRange {

    private static final String DEFAULT_MESSAGE = "undefined range";

    private final String lowerBoundPlaceholder;
    private final String higherBoundPlaceholder;

    public PlaceholderFacetRangeImpl(final String lowerBoundPlaceholder,
	    final String higherBoundPlaceholder) {
	this.lowerBoundPlaceholder = lowerBoundPlaceholder;
	this.higherBoundPlaceholder = higherBoundPlaceholder;
    }

    @Override
    public String getFacetCode() {
	return DEFAULT_MESSAGE;
    }

    @Override
    public long getLowerBound() {
	return 0;
    }

    @Override
    public String getLowerBoundCode() {
	return lowerBoundPlaceholder;
    }

    @Override
    public long getHigherBound() {
	return 0;
    }

    @Override
    public String getHigherBoundCode() {
	return higherBoundPlaceholder;
    }

    @Override
    public String getRangeFormat() {
	return DEFAULT_MESSAGE;
    }
}
