package com.videopolis.apis.concurrent.base;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.apis.concurrent.base.listener.AbstractApisTaskListener;
import com.videopolis.apis.concurrent.impl.listener.AggregationTaskListener;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.service.TaskRunnerService;

/**
 * A generic {@link Task} able to execute underlying tasks of
 * {@link ApisCriterion} contained in an {@link ApisRequest}.<br>
 * Any ApisCriterion has an corresponding task representing the way to literally
 * execute an action (generally data retrieval).<br>
 * <br>
 * Since an {@link ApisRequest} is the root of criteria processing, callers
 * willing to process a request should generally simply wrap it under this class
 * for processing it. This will lead to the following constructions :<br>
 * <code>new ApisCriterionTask(request, context).execute();</code><br>
 * <br>
 * All the logic of criteria processing, parallelizing, etc. will be handled
 * properly through a recursive "task" processing pipeline started by this
 * class.
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractApisCriterionTask extends AbstractApisTask {

    private static final Log LOGGER_LISTENER = LogFactory
	    .getLog("apis.listener");
    private CriteriaContainer container;
    private ApisContext context;
    private CalmObject[] parentObjects;
    private ItemsResponse response = null;
    private TaskRunnerService taskRunnerService;

    /**
     * Creates this task.
     * 
     * @param container
     *            the parent container whose {@link ApisCriterion} contents
     *            should be processed
     * @param context
     *            the {@link ApisContext} to use
     * @param parentObjects
     *            parent objects to aggregate results into
     */
    public AbstractApisCriterionTask(CriteriaContainer container,
	    ApisContext context, CalmObject... parentObjects) {
	this.container = container;
	this.context = context;
	this.parentObjects = parentObjects;
    }

    @Override
    public ItemsResponse doExecute(TaskExecutionContext taskExecutionContext)
	    throws TaskExecutionException {
	final boolean traceEnabled = LOGGER_LISTENER.isTraceEnabled();
	long startTime = 0;
	if (traceEnabled) {
	    startTime = System.currentTimeMillis();
	}
	final Collection<Task<?>> tasks = new ArrayList<Task<?>>();

	for (final ApisCriterion criterion : container.getApisCriteria()) {
	    try {
		final Task<ItemsResponse> task = criterion.getTask(container,
			context, parentObjects);
		// Ensuring criterion registration against an APIS task
		if (task instanceof ApisTask<?>) {
		    ((ApisTask<?>) task).setCriterion(criterion);
		}
		// Building tasks to execute in parallel
		tasks.add(task);
		// Registering task listeners
		registerTaskListeners(task, criterion);

	    } catch (ApisException e) {
		throw new TaskExecutionException(
			"Problems while building task for criterion ["
				+ criterion.toString() + "]", e);
	    }
	}
	if (traceEnabled) {
	    final long endTime = System.currentTimeMillis();
	    LOGGER_LISTENER.trace("TIME;" + (endTime - startTime)
		    + ";CRITERION_TASK;" + toString());
	}
	// whole task execution in parallel
	getTaskRunnerService().execute(tasks);
	if (traceEnabled) {
	    final long endTime = System.currentTimeMillis();
	    LOGGER_LISTENER.trace("TIME;" + (endTime - startTime)
		    + ";CRITERION_TASK_END;" + toString());
	}
	return response;

    }

    /**
     * Registers listeners to the given task. This method could be overridden to
     * register more listeners. Anyway this method should be always be called
     * when extending or unexpected behavior could happen.
     * 
     * @param task
     *            the task to add {@link TaskListener} to
     * @param criterion
     *            the parent {@link ApisCriterion} which spawned this task
     * @throws ApisException
     *             whenever an exception occurred
     */
    protected void registerTaskListeners(Task<ItemsResponse> task,
	    ApisCriterion criterion) throws ApisException {
	// Adding our end of task listener which will execute our sub
	// tasks
	task.addTaskListener(new AbstractApisTaskListener() {
	    @Override
	    protected void doTaskFinished(Task<ItemsResponse> task,
		    TaskExecutionContext context, ItemsResponse result) {
		if (response == null) {
		    response = result;
		}
	    }

	    @Override
	    public String toString() {
		return "CRIT_RESPONSE";
	    }
	});
	task.addTaskListener(new AggregationTaskListener(context, parentObjects));
	task.addTaskListener(criterion.getSubprocessingListener(criterion,
		context, getTaskRunnerService()));
    }

    public TaskRunnerService getTaskRunnerService() {
	return taskRunnerService;
    }

    /**
     * Defines the task runner implementation
     * 
     * @param taskRunner
     */
    public void setTaskRunnerService(TaskRunnerService taskRunnerService) {
	this.taskRunnerService = taskRunnerService;
    }

    public ItemsResponse getResponse() {
	return response;
    }

    @Override
    public String toString() {
	return container != null ? container.toString() : super.toString();
    }

}
