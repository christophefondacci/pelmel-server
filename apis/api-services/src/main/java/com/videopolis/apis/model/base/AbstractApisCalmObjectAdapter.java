package com.videopolis.apis.model.base;

import java.util.Arrays;
import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.calm.model.CalmObject;

/**
 * A simple base class allowing to simply adapt a single element (1 to 1
 * cardinality).
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractApisCalmObjectAdapter implements
	ApisCalmObjectAdapter {

    @Override
    public List<CalmObject> adapt(CalmObject element) throws ApisException {
	return Arrays.asList(doAdapt(element));
    }

    protected abstract CalmObject doAdapt(CalmObject element)
	    throws ApisException;
}
