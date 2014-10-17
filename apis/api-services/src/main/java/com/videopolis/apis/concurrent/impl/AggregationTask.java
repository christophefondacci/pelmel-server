package com.videopolis.apis.concurrent.impl;

import java.util.Arrays;

import com.videopolis.apis.calm.impl.CalmObjectAggregator;
import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.service.ApiMutableCompositeResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * This task does not call any remote service and only provides a
 * {@link CalmObjectAggregator} as its unique response item.
 * 
 * @author Christophe Fondacci
 * 
 */
public class AggregationTask extends AbstractApisTask {
    private final ApisContext context;

    public AggregationTask(ApisContext context) {
	this.context = context;
    }

    @Override
    protected ItemsResponse doExecute(TaskExecutionContext c)
	    throws TaskExecutionException, InterruptedException {
	final CalmObject o = new CalmObjectAggregator();
	ItemsResponseImpl response = new ItemsResponseImpl();
	response.addItem(o);
	// Explicitly setting the aggregation in the response for custom
	// adapters
	// which may need the response before it is injected
	final ApiResponse apiResp = context.getApiResponse();
	if (apiResp instanceof ApiMutableCompositeResponse) {
	    ((ApiMutableCompositeResponse) apiResp).setElements(Arrays
		    .asList(o));
	}
	return response;
    }

    @Override
    public String toString() {
	return "aggregation";
    }
}
