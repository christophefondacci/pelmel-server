package com.videopolis.concurrent.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.factory.ConcurrentFactory;
import com.videopolis.concurrent.helper.TaskHelper;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;

/**
 * A task implemented as a thread
 *
 * @author julien
 *
 */
public class ThreadTask extends Thread {

    /** Initial state, the thread is not running */
    public static final int STATE_NONE = 0;

    /** The task is currently executing */
    public static final int STATE_EXECUTING = 1;

    /** The taskFinished() method is being called on the listeners */
    public static final int STATE_LISTENER_FINISHED = 2;

    /** The taskInterrupted() method is being called on the listeners */
    public static final int STATE_LISTENER_INTERRUPTED = 3;

    /** The taskFailed() method is being called on the listeners */
    public static final int STATE_LISTENER_FAILED = 4;

    /** The task + listener have completed their execution */
    public static final int STATE_COMPLETED = 5;

    private static final Log LOGGER = LogFactory.getLog(ThreadTask.class);

    private static final String DEFAULT_NAME = "available";

    private Task<?> task;
    private volatile boolean interruptRequested = false;
    private volatile int threadState = STATE_NONE;
    private MutableTaskExecutionContext context;

    /**
     * Default constructor
     */
    public ThreadTask() {
	clear();
    }

    /**
     * Resets the thread, and make it available to execute another task
     */
    public final void clear() {
	setName(DEFAULT_NAME);
	task = null;
	context = null;
	interruptRequested = false;
	threadState = STATE_NONE;
    }

    /**
     * Interrupt the running task
     */
    public void doInterrupt() {
	context.setExecutionInterrupted(true);
	interruptRequested = true;
	interrupt();
    }

    /**
     * Initializes the thread with a task
     *
     * @param name
     *            Thread name
     * @param task
     *            Task to run
     */
    public void initialize(final String name, final Task<?> task) {
	setName(name);
	this.task = task;
	context = ConcurrentFactory.newMutableTaskExecutionContext();
	interruptRequested = false;
	threadState = STATE_NONE;
    }

    /**
     * Returns whether or not the task is currently executing
     *
     * @return {@code true} or {@code false}
     */
    public boolean isExecuting() {
	return threadState == STATE_EXECUTING;
    }

    /**
     * Returns whether or not the task is finished
     *
     * @return {@code true} or {@code false}
     */
    public boolean isTaskFinished() {
	return threadState == STATE_LISTENER_FINISHED;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void run() {
	try {
	    LOGGER.debug("run(): Starting execution of task.execute()");
	    threadState = STATE_EXECUTING;
	    final Object result;
	    try {
		result = task.execute(context);
	    } catch (final RuntimeException e) {
		throw new TaskExecutionException(e.getMessage(), e);
	    }
	    LOGGER.debug("run(): Finished execution of task.execute()");
	    if (Thread.interrupted() || interruptRequested) {
		threadState = STATE_LISTENER_INTERRUPTED;
		// The task has been interrupted or is in timeout
		TaskHelper.fireTaskInterrupted(task, context);
	    } else {
		// The task finished normally
		threadState = STATE_LISTENER_FINISHED;
		for (final TaskListener<?> taskListener : task
			.getTaskListeners()) {
		    if (!interruptRequested) {
			try {
			    ((TaskListener) taskListener).taskFinished(task,
				    context, result);
			} catch (final RuntimeException e) {
			    LOGGER.error("Listener " + taskListener.toString()
				    + " execution failed: " + e.getMessage(), e);
			}
		    }
		}
	    }
	    threadState = STATE_COMPLETED;
	} catch (final InterruptedException e) {
	    LOGGER.debug("run(): task.execute() has been interrupted");
	    if (threadState == STATE_EXECUTING) {
		// The job was executing
		TaskHelper.fireTaskInterrupted(task, context);
		threadState = STATE_COMPLETED;
	    }
	} catch (final TaskExecutionException e) {
	    // The task has failed
	    LOGGER.debug("run(): task.execute() has thrown a TaskExecutionException");
	    threadState = STATE_LISTENER_FAILED;
	    TaskHelper.fireTaskFailed(task, context, e);
	    threadState = STATE_COMPLETED;
	}
    }
}
