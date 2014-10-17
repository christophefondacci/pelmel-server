package com.videopolis.apis.concurrent.base;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;

/**
 * An abstract APIS task which wraps every execution of an APIS taks with timers
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractApisTask extends AbstractTask<ItemsResponse>
	implements ApisTask<ItemsResponse> {

    private static final Log LOGGER = LogFactory.getLog("apis.performance");

    private long startTime;
    private ApisCriterion parentCriterion;

    /**
     * Starts our timer for execution time
     */
    protected void start() {
	if (LOGGER.isTraceEnabled()) {
	    startTime = new Date().getTime();
	}
    }

    /**
     * Stops the timer for execution time and logs a trace message to indicate
     * execution time
     */
    protected void stop() {
	if (LOGGER.isTraceEnabled()) {
	    long callTime = new Date().getTime() - startTime;
	    LOGGER.trace("ApisTask;" + callTime + ";" + toString());
	}
    }

    @Override
    public final ItemsResponse execute(TaskExecutionContext context)
	    throws TaskExecutionException, InterruptedException {
	start();
	try {
	    return doExecute(context);
	} finally {
	    stop();
	}
    }

    /**
     * Performs the <b>real</b> execution of this task as the
     * {@link AbstractApisTask#execute(TaskExecutionContext)} delegates
     * execution to this method because it wraps the call with a log timer.
     * 
     * @param context
     *            task execution context provided by the task manager
     * @return the {@link ItemsResponse} of this APIS task
     * @throws TaskExecutionException
     * @throws InterruptedException
     * @see AbstractApisTask#execute(TaskExecutionContext)
     */
    protected abstract ItemsResponse doExecute(TaskExecutionContext context)
	    throws TaskExecutionException, InterruptedException;

    @Override
    public final ApisCriterion getCriterion() {
	return parentCriterion;
    }

    @Override
    public final void setCriterion(ApisCriterion parentCriterion) {
	this.parentCriterion = parentCriterion;
    }
}
