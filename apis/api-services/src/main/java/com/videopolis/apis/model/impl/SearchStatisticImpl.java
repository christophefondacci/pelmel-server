package com.videopolis.apis.model.impl;

import com.videopolis.apis.model.SearchStatistic;

/**
 * Default implementation of a {@link SearchStatistic}.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchStatisticImpl implements SearchStatistic {

    /** Serialization unique id */
    private static final long serialVersionUID = 1L;
    /** Code of this statistic information */
    private String code;
    /** Numeric value of this statistic */
    private Number numericValue;
    /** String value of this statistic */
    private String stringValue;

    /**
     * Creates a new {@link SearchStatistic} for the given code with the
     * specified value.
     * 
     * @param code
     *            statistic code
     * @param value
     *            statistic value
     */
    public SearchStatisticImpl(String code, Number value) {
	this.code = code;
	this.numericValue = value;
    }

    public SearchStatisticImpl(String code, String value) {
	this.code = code;
	this.stringValue = value;
    }

    @Override
    public String getCode() {
	return code;
    }

    @Override
    public Number getNumericValue() {
	if (numericValue != null) {
	    return numericValue;
	} else if (stringValue != null) {
	    return Double.parseDouble(stringValue);
	} else {
	    return null;
	}
    }

    @Override
    public String getStringValue() {
	if (stringValue != null) {
	    return stringValue;
	} else if (numericValue != null) {
	    return String.valueOf(numericValue);
	} else {
	    return null;
	}
    }

}
