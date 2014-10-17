package com.videopolis.concurrent.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.exception.TaskFailedJobExecutionException;
import com.videopolis.concurrent.exception.TaskInterruptedJobExecutionException;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;

/**
 * <p>
 * A jobs which executes a task
 * </p>
 * <p>
 * The TaskJob is responsible for:
 * <ul>
 * <li>Executing the task (by calling {@code execute()} on the task</li>
 * <li>Firing {@code taskFinished()} on the listener, if required</li>
 * </ul>
 * </p>
 *
 * @author julien
 *
 */
public class TaskJob implements InterruptableJob {

    private static final Log LOGGER = LogFactory.getLog(TaskJob.class);

    /** The thread which is running this job */
    private Thread runnerThread;

    /** Set to true if an interruption is requested */
    private volatile boolean interruptRequested = false;

    /** The execution context of the task */
    private MutableTaskExecutionContext taskExecutionContext;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
	// Set the interruption requested flag
	interruptRequested = true;

	// Interrupt the thread
	if (taskExecutionContext != null) {
	    taskExecutionContext.setExecutionInterrupted(true);
	}
	if (runnerThread == null) {
	    LOGGER.warn("Unable to interrupt any thread for this job");
	} else {
	    runnerThread.interrupt();
	}
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(final JobExecutionContext context)
	    throws JobExecutionException {

	// Set the runner thread
	runnerThread = Thread.currentThread();

	try {
	    // Get the task
	    final Task<?> task = (Task<?>) context.getMergedJobDataMap().get(
		    QuartzConstants.TASK);

	    // Get the task context
	    taskExecutionContext = (MutableTaskExecutionContext) context
		    .getMergedJobDataMap().get(QuartzConstants.CONTEXT);

	    try {
		// Execute core task
		final Object result;
		try {
		    result = task.execute(taskExecutionContext);
		} catch (final RuntimeException e) {
		    throw new TaskExecutionException(e.getMessage(), e);
		}
		if (interruptRequested) {
		    throw new TaskInterruptedJobExecutionException();
		}

		// Execute listeners
		for (final TaskListener<?> taskListener : task
			.getTaskListeners()) {
		    if (!interruptRequested) {
			try {
			    ((TaskListener) taskListener).taskFinished(task,
				    taskExecutionContext, result);
			} catch (final RuntimeException e) {
			    LOGGER.error("Listener " + taskListener.toString()
				    + " execution failed: " + e.getMessage(), e);
			}
		    }
		}

	    } catch (final TaskExecutionException e) {
		throw new TaskFailedJobExecutionException(e);
	    } catch (final InterruptedException e) {
		throw new TaskInterruptedJobExecutionException(e);
	    }
	} finally {
	    runnerThread = null;
	}
    }

}
