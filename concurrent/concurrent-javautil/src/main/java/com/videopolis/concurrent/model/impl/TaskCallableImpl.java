package com.videopolis.concurrent.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.factory.TaskCallableFactory;
import com.videopolis.concurrent.helper.TaskHelper;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskCallable;

/**
 * Default implementation of {@link TaskCallable}
 * 
 * @author julien
 * 
 */
public class TaskCallableImpl implements TaskCallable {

    private static final Log LOGGER = LogFactory.getLog(TaskCallableImpl.class);

    /** The attached task */
    private Task<?> task;

    /** The task context */
    private MutableTaskExecutionContext context;

    /** The factory which created this object */
    private TaskCallableFactory factory;

    /** If set to true, notifies that the task has been interrupted */
    private volatile boolean interruptionRequested = false;

    /** If set to true, notifies that this task can be interrupted */
    private volatile boolean interruptable = false;

    /** Contains the thread which runs this task */
    private volatile Thread runnerThread;

    @Override
    public void initialize(final Task<?> task,
	    final MutableTaskExecutionContext context,
	    final TaskCallableFactory factory) {
	this.task = task;
	this.context = context;
	this.factory = factory;
	interruptionRequested = false;
	interruptable = true;
	runnerThread = null;
    }

    @Override
    public void clear() {
	task = null;
	context = null;
	factory = null;
	interruptionRequested = false;
	interruptable = false;
	runnerThread = null;
    }

    @Override
    public void interrupt() {
	if (interruptable) {
	    context.setExecutionInterrupted(true);
	    interruptionRequested = true;
	    if (runnerThread == null) {
		LOGGER.warn("No thread to interrupt!");
	    } else {
		runnerThread.interrupt();
		TaskHelper.fireTaskInterrupted(task, context);
	    }
	}
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public TaskCallable call() {
	try {
	    // Keep the running thread
	    runnerThread = Thread.currentThread();

	    try {
		// Starting from here the task can be interrupted
		interruptable = true;

		// Execute task body
		final Object result;
		try {
		    result = task.execute(context);
		} catch (final RuntimeException e) {
		    throw new TaskExecutionException(e.getMessage(), e);
		}

		if (Thread.interrupted()) {
		    throw new InterruptedException();
		}

		// Invoke listeners
		for (final TaskListener<?> taskListener : task
			.getTaskListeners()) {
		    if (!interruptionRequested) {
			try {
			    ((TaskListener) taskListener).taskFinished(task,
				    context, result);
			} catch (final RuntimeException e) {
			    LOGGER.error("Listener " + taskListener.toString()
				    + " execution failed: " + e.getMessage(), e);
			}
		    }

		    if (Thread.interrupted()) {
			throw new InterruptedException();
		    }
		}
		// No use to interrupt now
		interruptable = false;

	    } catch (final TaskExecutionException e) {
		// Here the task cannot be interrupted
		interruptable = false;

		// Task execution failed
		TaskHelper.fireTaskFailed(task, context, e);
	    } catch (final InterruptedException e) {
		// Here the task cannot be interrupted
		interruptable = false;

		// Task interrupted
		// TaskHelper.fireTaskInterrupted(task, context);
	    }

	    return this;
	} catch (final Error e) {
	    // Errors thrown during task execution are hidden by the framework.
	    // We don't have any other option to catch them here, log them, then
	    // re-throw them
	    LOGGER.fatal(
		    "Error caught during task execution: " + e.getMessage(), e);
	    throw e;
	} finally {
	    // Reset runner thread
	    runnerThread = null;

	    // Release myself
	    factory.release(this);
	}
    }

}
