package com.videopolis.apis.concurrent.base.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.apis.concurrent.model.ApisTaskListener;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.base.AbstractTaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A simple helper class which pre-implement void methods to be able to only
 * extend the method we need to make a more compact code when implementors only
 * need to handle one of the listener methods.
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractApisTaskListener extends
	AbstractTaskListener<ItemsResponse> implements ApisTaskListener {

    private static final Log LOGGER_LISTENER = LogFactory
	    .getLog("apis.listener");
    private static final Log LOGGER = LogFactory
	    .getLog(AbstractApisTaskListener.class);

    private volatile Boolean failed = null;
    private volatile boolean interrupted = false;
    private Exception exception = null;

    @Override
    public boolean hasFailed() {
	// Assuming that the "unknown" failure state is a failure
	// Only valid after the execution of the task
	return failed == null ? true : failed;
    }

    @Override
    public boolean isInterrupted() {
	return interrupted;
    }

    /**
     * @return the exception raised by the task this listener listens to. Only
     *         existing when {@link AbstractApisTaskListener#hasFailed()}
     *         returns <code>true</code>
     */
    public Exception getException() {
	return exception;
    }

    @Override
    public void taskFailed(Task<ItemsResponse> task,
	    TaskExecutionContext context, Exception exception) {
	super.taskFailed(task, context, exception);
	setFailed(true);
	this.exception = exception;
	final String taskName = task == null ? "no task" : task.toString();
	LOGGER.error(
		"Apis task ["
			+ taskName
			+ "] failed: "
			+ (exception == null ? "(no exception)" : exception
				.getMessage()), exception);
	LOGGER_LISTENER.error("FAIL;"
		+ taskName
		+ ";"
		+ (exception == null ? "(no exception)" : exception
			.getMessage()));
    }

    @Override
    public void taskInterrupted(Task<ItemsResponse> task,
	    TaskExecutionContext context) {
	super.taskInterrupted(task, context);
	setInterrupted(true);
	LOGGER_LISTENER.error("TIMEOUT;" + task.toString());
    }

    @Override
    public final void taskFinished(Task<ItemsResponse> task,
	    TaskExecutionContext context, ItemsResponse result) {
	long startTime = 0;
	final boolean traceEnabled = LOGGER_LISTENER.isTraceEnabled();
	if (traceEnabled) {
	    startTime = System.currentTimeMillis();
	}
	doTaskFinished(task, context, result);
	if (traceEnabled) {
	    final long endTime = System.currentTimeMillis();
	    LOGGER_LISTENER.trace("TIME;" + (endTime - startTime) + ";"
		    + toString());
	}
	// Assigning the failure state only if not yet defined
	if (this.failed == null) {
	    setFailed(false);
	}
    }

    protected abstract void doTaskFinished(Task<ItemsResponse> task,
	    TaskExecutionContext context, ItemsResponse result);

    protected void setFailed(boolean failed) {
	this.failed = failed;
    }

    protected void setInterrupted(boolean interrupted) {
	this.interrupted = interrupted;
	if (interrupted) {
	    // An interrupted task is not a failure
	    setFailed(false);
	}
    }
}
