package com.videopolis.apis.concurrent.impl;

import com.videopolis.apis.concurrent.base.AbstractApisCriterionTask;
import com.videopolis.apis.concurrent.base.listener.AbstractApisTaskListener;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A special extension of the {@link AbstractApisCriterionTask}, which executes
 * nested {@link ApisCriterion} contained in an {@link ApisRequest}. The main
 * difference between {@link ApisCriterionTask} is that it adds a specific
 * {@link TaskListener} which forwards failure and interruption of the child
 * criterion to the listeners of this task so that the API service could know
 * about the status of execution of the root criterion.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisRequestTask extends AbstractApisCriterionTask {

    /**
     * Creates this task.
     * 
     * @param request
     *            the parent {@link ApisRequest} whose {@link ApisCriterion}
     *            contents should be processed
     * @param context
     *            the {@link ApisContext} to use
     */
    public ApisRequestTask(ApisRequest request, ApisContext context) {
	super(request, context);
    }

    @Override
    protected void registerTaskListeners(Task<ItemsResponse> task,
	    ApisCriterion criterion) throws ApisException {
	super.registerTaskListeners(task, criterion);
	// Adds a forwarding task listener
	task.addTaskListener(new AbstractApisTaskListener() {

	    @Override
	    protected void doTaskFinished(Task<ItemsResponse> task,
		    TaskExecutionContext context, ItemsResponse result) {
	    }

	    @Override
	    public void taskFailed(Task<ItemsResponse> task,
		    TaskExecutionContext context, Exception exception) {
		// Forwarding failures to parent listener
		for (TaskListener<ItemsResponse> l : getTaskListeners()) {
		    l.taskFailed(task, context, exception);
		}
	    }

	    @Override
	    public void taskInterrupted(Task<ItemsResponse> task,
		    TaskExecutionContext context) {
		// Forwarding interruptions to parent listener
		for (TaskListener<ItemsResponse> l : getTaskListeners()) {
		    l.taskInterrupted(task, context);
		}
	    }
	});
    }
}
