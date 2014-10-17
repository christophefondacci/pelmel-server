package com.videopolis.apis.calm.impl;

import com.videopolis.calm.base.AbstractCalmObject;

/**
 * This class acts as an aggregator of CAL model objects. It does not define any
 * module and is simply the root object of APIS requests which need to fetch
 * multiple CAL-types at the root level.
 * 
 * @author Christophe Fondacci
 * 
 */
public final class CalmObjectAggregator extends AbstractCalmObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1346006872273672868L;

    /**
     * Instantiates a new aggregator.
     * 
     * @noinstantiate as it is internally instantiated by APIS when needed
     */
    public CalmObjectAggregator() {
	super(null);
    }
}
