package com.videopolis.smaug.common.model;

/**
 * A possible value for a {@code FacetCategory}, like a {@link Facet}; the
 * difference between {@link Facet} and {@code FacetRange} is that the value of
 * a {@code FacetRange} is a range between two integers
 *
 * @author julien
 *
 */
public interface FacetRange extends Facet {

    /**
     * Returns the lower bound of the range
     *
     * @return Lower bound
     */
    long getLowerBound();

    /**
     * Returns the code for the lower bound of the range
     *
     * @return Lower bound code
     */
    String getLowerBoundCode();

    /**
     * Returns the higher bound of the ranger
     *
     * @return Higher bound
     */
    long getHigherBound();

    /**
     * Returns the code for the higher bound of the range
     *
     * @return Higher bound code
     */
    String getHigherBoundCode();

    /**
     * @return the rangeFormat, used to generate the facet's code using the
     *         lower & higher bounds
     */
    String getRangeFormat();
}
