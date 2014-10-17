package com.videopolis.apis.model.impl;

import com.videopolis.apis.calm.impl.CalmObjectAggregator;
import com.videopolis.apis.concurrent.impl.AggregationTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.model.Task;

/**
 * An aggregation criterion is used internally by the APIS framework to publish
 * a root {@link CalmObjectAggregator} object at the root of the APIS response
 * so that elements could be aggregated in it. <br>
 * It typically allows to transparently simulate a composite APIS request which
 * can query multiple different item types in a single APIS call.
 * 
 * @author Christophe Fondacci
 * 
 */
public class AggregationCriterionImpl extends AbstractApisCriterion {

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects)
	    throws ApisException {
	return new AggregationTask(context);
    }

    @Override
    public String getType() {
	return null;
    }

}
