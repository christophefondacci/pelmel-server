package com.videopolis.apis.model;

import java.io.Serializable;

/**
 * This interface defines a statistic information resulting from a search query.
 * For example, when a caller performs a geographic "nearby" query, the distance
 * of every returned elements from the requested input point will be returned as
 * a search statistic.<br>
 * <br>
 * For now, this information only defines a numeric value. We may need to
 * augment it with more data, such as the unit in which this value is measured,
 * etc.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface SearchStatistic extends Serializable {

    String DISTANCE = "distance";
    String COUNT = "count";
    String MATCHED_TEXT = "textMatch";

    /**
     * Retrieves the numeric value of this statistic. This method should only be
     * called when the caller knows that the value will be numeric.
     * 
     * @return the statistic's numeric value. Note that a default Double
     *         conversion may be performed when no numeric value has been set.
     * @throws NumberFormatException
     *             when the statistic value cannot be formatted as a number
     */
    Number getNumericValue();

    /**
     * Retrieves the value of this statistic as a string.
     * 
     * @return the string value of this statistic information
     */
    String getStringValue();

    /**
     * @return the code for this statistic information
     */
    String getCode();
}
