package com.videopolis.apis.model.impl;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.base.AbstractApisCalmObjectAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.impl.TestModelC;

/**
 * Adapts modelC to modelA for adapter test
 * 
 * @author Christophe Fondacci
 * 
 */
public class ModelCModelAAdapter extends AbstractApisCalmObjectAdapter {

    @Override
    public CalmObject doAdapt(CalmObject element) throws ApisException {
	Assert.notNull(element, "Cannot adpat a null model");
	Assert.instanceOf(element, TestModelC.class,
		"Expected TestModelC, was " + element.getClass());

	final TestModelC modelC = (TestModelC) element;
	return modelC.getModelA();
    }

}
