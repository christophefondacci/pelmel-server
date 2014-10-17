package com.videopolis.apis.concurrent.impl.listener;

import com.videopolis.apis.concurrent.base.listener.AbstractApisTaskListener;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * Root listener whose only purpose is to give access to the root
 * {@link ItemsResponse} of a APIS request execution.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisResponseTaskListener extends AbstractApisTaskListener {

    private volatile ItemsResponse response;

    @Override
    protected final void doTaskFinished(Task<ItemsResponse> task,
	    TaskExecutionContext context, ItemsResponse result) {
	this.response = result;
    }

    public ItemsResponse getResponse() {
	return response;
    }

    @Override
    public String toString() {
	return "RESPONSE";
    }
}
