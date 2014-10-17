package com.videopolis.smaug.common.model;

/**
 * A grouping strategy informs about how facet filters could be combined
 * together. For example a OR strategy will perform a OR combination of any
 * filter. Specifying 2 filters in a same {@link FacetCategory} that has a OR
 * strategy would then return elements that match the first filter <i>OR</i> the
 * second.<br>
 * At the opposite, a {@link FacetCategory} which has a AND strategy would
 * return the elements matching the first filter <i>AND</i> the second.<br>
 * 
 * 
 * @author Christophe Fondacci
 * 
 */
public enum GroupingStrategy {
    /** All facet filters are processed by a logical AND */
    AND,
    /** All facet filters are processed by a logical OR */
    OR,
    /** All facet filters are facet ranges AND processed by a logical OR */
    RANGE
}
