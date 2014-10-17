package com.videopolis.concurrent.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskFailedJobExecutionException;
import com.videopolis.concurrent.exception.TaskInterruptedJobExecutionException;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * <p>
 * A JobListener fired when a {@link TaskJob} finished.
 * </p>
 * <p>
 * This listener is responsible for:
 * <ul>
 * <li>Notifying the {@code QuartzTaskRunnerServiceImpl} the execution is done</li>
 * <li>Firing {@code taskFailed()} on listeners if required</li>
 * <li>Firing {@code taskInterrupted()} on listeners if required</li>
 * </ul>
 * </p>
 *
 * @author julien
 *
 */
public class TaskJobListener implements JobListener {

    private static final Log LOGGER = LogFactory.getLog(TaskJobListener.class);

    @Override
    public String getName() {
	return getClass().getCanonicalName();
    }

    @Override
    public void jobExecutionVetoed(final JobExecutionContext context) {
	// Nothing to do
    }

    @Override
    public void jobToBeExecuted(final JobExecutionContext context) {
	// Nothing to do
    }

    @Override
    @SuppressWarnings("unchecked")
    public void jobWasExecuted(final JobExecutionContext context,
	    final JobExecutionException jobException) {

	LOGGER.debug("Starting listener");

	// Get the task
	final Task<?> task = (Task<?>) context.getMergedJobDataMap().get(
		QuartzConstants.TASK);

	// Get the lock
	final Object lock = context.getMergedJobDataMap().get(
		QuartzConstants.LOCK);

	// Get the task execution context
	final TaskExecutionContext taskExecutionContext = (TaskExecutionContext) context
		.getMergedJobDataMap().get(QuartzConstants.CONTEXT);

	// Before anything, notify the job finished
	synchronized (lock) {
	    lock.notify();
	}

	// Check the execution result
	if (jobException != null) {
	    // Execution FAILED (success case already handled)

	    if (jobException instanceof TaskInterruptedJobExecutionException) {
		// The task has been interrupted. Notify listeners
		for (final TaskListener<?> taskListener : task
			.getTaskListeners()) {
		    ((TaskListener) taskListener).taskInterrupted(task,
			    taskExecutionContext);
		}
	    }

	    if (jobException instanceof TaskFailedJobExecutionException) {
		// The task failed. Notify listeners

		final Exception cause = ((TaskFailedJobExecutionException) jobException)
			.getCause();

		for (final TaskListener<?> taskListener : task
			.getTaskListeners()) {
		    ((TaskListener) taskListener).taskFailed(task,
			    taskExecutionContext, cause);
		}
	    }

	}
    }
}
