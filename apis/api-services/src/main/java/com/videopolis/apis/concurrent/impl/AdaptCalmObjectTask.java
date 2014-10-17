package com.videopolis.apis.concurrent.impl;

import java.util.List;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

public class AdaptCalmObjectTask extends AbstractApisTask {

    private ApisCalmObjectAdapter adapter;
    private CalmObject[] parentObjects;
    // Keeping the context everywhere in case
    @SuppressWarnings("unused")
    private ApisContext context;

    public AdaptCalmObjectTask(ApisContext context,
	    ApisCalmObjectAdapter adapter, CalmObject... parentObjects) {
	this.context = context;
	this.adapter = adapter;
	this.parentObjects = parentObjects;
    }

    @Override
    protected ItemsResponse doExecute(TaskExecutionContext context)
	    throws TaskExecutionException, InterruptedException {
	final ItemsResponseImpl response = new ItemsResponseImpl();
	try {
	    Assert.notNull(parentObjects, "APIS cannot adapt a null array");
	    // We are simulating an item response from the result of our
	    // adaptation
	    for (CalmObject parent : parentObjects) {
		final List<? extends CalmObject> adaptedObjects = adapter
			.adapt(parent);
		for (CalmObject o : adaptedObjects) {
		    response.addItem(o);
		}
	    }
	} catch (ApisException e) {
	    throw new TaskExecutionException(e.getMessage(), e);
	}
	return response;
    }

    @Override
    public String toString() {
	return "adaptTask";
    }
}
