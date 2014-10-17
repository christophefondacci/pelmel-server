package com.videopolis.calm.model.impl;

import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

/**
 * A CAL model test implementation.
 * 
 * @author Christophe Fondacci
 * 
 */
public class TestModelC extends AbstractCalmObject {

    private static final long serialVersionUID = 4353324431097517440L;

    private String code;
    private String locale;
    private TestModelA modelA;

    /**
     * Builds this CAL model
     * 
     * @param key
     */
    public TestModelC(ItemKey key) {
	super(key);
	try {
	    modelA = new TestModelA(CalmFactory.parseKey("MODLAggregationTest"));
	} catch (CalException e) {
	    modelA = null;
	}
    }

    /**
     * Defines the code parameter
     * 
     * @param code
     */
    public void setCode(String code) {
	this.code = code;
    }

    /**
     * Retrieves the code parameter
     * 
     * @return
     */
    public String getCode() {
	return code;
    }

    /**
     * Retrieves the locale parameter
     * 
     * @return
     */
    public String getLocale() {
	return locale;
    }

    /**
     * Defines the locale parameter
     * 
     * @param locale
     */
    public void setLocale(String locale) {
	this.locale = locale;
    }

    public TestModelA getModelA() {
	return modelA;
    }
}
