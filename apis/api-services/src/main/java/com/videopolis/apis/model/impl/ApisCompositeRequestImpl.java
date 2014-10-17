package com.videopolis.apis.model.impl;

import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.apis.service.impl.ApiCompositeResponseImpl;

/**
 * A composite request does not need any root item and can aggregate multiple
 * CAL objects from different types at the root level.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisCompositeRequestImpl extends ApisRequestImpl {

    public ApisCompositeRequestImpl() {
	setRootCriterion(new AggregationCriterionImpl());
    }

    @Override
    public Class<? extends ApiMutableResponse> getResponseClass() {
	return ApiCompositeResponseImpl.class;
    }
}
