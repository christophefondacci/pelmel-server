package com.videopolis.calm.model.impl;

import com.videopolis.calm.base.AbstractCalmObject;
import com.videopolis.calm.model.ITestModelB;
import com.videopolis.calm.model.ItemKey;

/**
 * A CAL model test implementation.
 * 
 * @author Christophe Fondacci
 * 
 */
public class TestModelB extends AbstractCalmObject implements ITestModelB {

    private static final long serialVersionUID = -238171595730315599L;

    private String name;

    /**
     * Builds this CAL model object
     * 
     * @param key
     */
    public TestModelB(ItemKey key) {
	super(key);
    }

    /**
     * Defines the name parameter
     * 
     * @param name
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Retrieves the name parameter
     * 
     * @return
     */
    public String getName() {
	return name;
    }
}
