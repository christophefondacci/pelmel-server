package com.videopolis.calm.model.impl;

import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * A CAL model test implementation.
 * 
 * @author Christophe Fondacci
 * 
 */
public class TestModelA extends AbstractCalmObject {

    private static final long serialVersionUID = 177282131638886188L;

    private String info;

    /**
     * Builds this test model
     * 
     * @param key
     */
    public TestModelA(ItemKey key) {
	super(key);
    }

    /**
     * Sets the info parameter
     * 
     * @param info
     */
    public void setInfo(String info) {
	this.info = info;
    }

    /**
     * Retrieves the info parameter
     * 
     * @return
     */
    public String getInfo() {
	return info;
    }
}
