package com.videopolis.smaug.common.model.impl;

import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.base.AbstractFacet;

/**
 * Default implementation of {@link FacetRange}
 *
 * @author julien
 *
 */
public class FacetRangeImpl extends AbstractFacet implements FacetRange {

    /** Lower bound of the range */
    private long lowerBound;

    /** Higher bound of the range */
    private long higherBound;

    /**
     * Format of the code, used to generate the facet's code given the two
     * bounds
     */
    private String rangeFormat;

    /**
     * @param lowerBound
     *            the lowerBound to set
     */
    public void setLowerBound(final long lowerBound) {
	this.lowerBound = lowerBound;
    }

    /**
     * @param higherBound
     *            the higherBound to set
     */
    public void setHigherBound(final long higherBound) {
	this.higherBound = higherBound;
    }

    @Override
    public String getRangeFormat() {
	return rangeFormat;
    }

    /**
     * @param rangeFormat
     *            the rangeFormat to set
     */
    public void setRangeFormat(final String rangeFormat) {
	this.rangeFormat = rangeFormat;
    }

    @Override
    public String getFacetCode() {
	return String.format(rangeFormat, lowerBound, higherBound + 1);
    }

    @Override
    public long getLowerBound() {
	return lowerBound;
    }

    @Override
    public long getHigherBound() {
	return higherBound;
    }

    @Override
    public String getLowerBoundCode() {
	return String.valueOf(lowerBound);
    }

    @Override
    public String getHigherBoundCode() {
	return String.valueOf(higherBound);
    }

    @Override
    public String toString() {
	return super.toString() + " range from " + lowerBound + " to "
		+ higherBound + " (" + getFacetCode() + ")";
    }
}
