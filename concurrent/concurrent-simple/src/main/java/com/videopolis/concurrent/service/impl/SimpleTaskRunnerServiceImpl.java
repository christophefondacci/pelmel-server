package com.videopolis.concurrent.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.factory.ConcurrentFactory;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.service.base.AbstractTimeoutTaskRunnerService;

/**
 * A simple implementation of {@link AbstractTimeoutTaskRunnerService} which
 * will fire new threads to execute each task
 *
 * @author julien
 *
 */
public class SimpleTaskRunnerServiceImpl extends
	AbstractTimeoutTaskRunnerService {

    /**
     * A task thread
     *
     * @author julien
     *
     */
    private class TaskThread extends Thread {

	public static final int STATE_NONE = 0;
	public static final int STATE_EXECUTING = 1;
	public static final int STATE_LISTENER_FINISHED = 2;
	public static final int STATE_LISTENER_INTERRUPTED = 3;
	public static final int STATE_LISTENER_FAILED = 4;
	public static final int STATE_COMPLETED = 5;

	private final Task<?> task;
	private volatile boolean interruptRequested = false;
	private volatile int threadState = STATE_NONE;
	private final MutableTaskExecutionContext context;

	/**
	 * Default constructor
	 *
	 * @param group
	 *            The thread group
	 * @param name
	 *            The thread name
	 * @param task
	 *            The thread task
	 */
	public TaskThread(final ThreadGroup group, final String name,
		final Task<?> task) {
	    super(group, name);
	    this.task = task;
	    context = ConcurrentFactory.newMutableTaskExecutionContext();
	}

	/**
	 * Interrupt the execution
	 */
	public void doInterrupt() {
	    context.setExecutionInterrupted(true);
	    interruptRequested = true;
	    interrupt();
	}

	/**
	 * Returns {@code true} if the code is executing
	 *
	 * @return
	 */
	public boolean isExecuting() {
	    return threadState == STATE_EXECUTING;
	}

	/**
	 * Returns {@code true} if the task is finished
	 *
	 * @return
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
		final Object result = task.execute(context);
		LOGGER.debug("run(): Finished execution of task.execute()");
		if (Thread.interrupted() || interruptRequested) {
		    threadState = STATE_LISTENER_INTERRUPTED;
		    // The task has been interrupted or is in timeout
		    fireTaskInterrupted(task, context);
		} else {
		    // The task finished normally
		    threadState = STATE_LISTENER_FINISHED;
		    for (final TaskListener<?> taskListener : task
			    .getTaskListeners()) {
			if (!interruptRequested) {
			    ((TaskListener) taskListener).taskFinished(task,
				    context, result);
			}
		    }
		}
		threadState = STATE_COMPLETED;
	    } catch (final InterruptedException e) {
		LOGGER.debug("run(): task.execute() has been interrupted");
		if (threadState == STATE_EXECUTING) {
		    // The job was executing
		    fireTaskInterrupted(task, context);
		    threadState = STATE_COMPLETED;
		}
	    } catch (final TaskExecutionException e) {
		// The task has failed
		LOGGER.debug("run(): task.execute() has thrown a TaskExecutionException");
		threadState = STATE_LISTENER_FAILED;
		fireTaskFailed(task, context, e);
		threadState = STATE_COMPLETED;
	    }
	}
    }

    private static AtomicInteger groupNumber = new AtomicInteger(0);

    private static final Log LOGGER = LogFactory
	    .getLog(SimpleTaskRunnerServiceImpl.class);

    /**
     * Interrupt jobs still running
     *
     * @param threads
     *            Threads
     */
    private void cleanUpExecutingJobs(
	    final Collection<? extends TaskThread> threads) {
	// Clean up remaining jobs
	for (final TaskThread thread : threads) {
	    if (thread.isAlive()
		    && (thread.isExecuting() || thread.isTaskFinished())) {
		LOGGER.debug("Interrupting thread " + thread.getName());
		thread.doInterrupt();
	    }
	}
    }

    @Override
    public void execute(final Collection<Task<?>> tasks) {
	final Collection<TaskThread> threads = new ArrayList<TaskThread>(
		tasks.size());

	final int thisGroupId = groupNumber.getAndIncrement();
	final ThreadGroup threadGroup = new ThreadGroup("tasks-" + thisGroupId);

	long millis = getTimeout();
	long lastTime = System.currentTimeMillis();

	// Launch the threads
	int taskNumber = 0;
	for (final Task<?> task : tasks) {
	    final TaskThread thread = new TaskThread(threadGroup, "task-"
		    + thisGroupId + "-" + taskNumber++, task);
	    threads.add(thread);
	    thread.start();
	}

	if (hasTimeout()) {
	    LOGGER.debug("Waiting for execution with timeout");
	    // Wait until threads terminate or timeout
	    long now = System.currentTimeMillis();
	    millis -= now - lastTime;
	    LOGGER.debug("Here we have: now=" + now + ", millis=" + millis
		    + ", lastTime=" + lastTime);
	    lastTime = now;

	    final Iterator<TaskThread> i = threads.iterator();
	    try {
		while (i.hasNext() && millis > 0) {

		    // Wait for this thread
		    i.next().join(millis);

		    now = System.currentTimeMillis();
		    millis -= now - lastTime;
		    lastTime = now;
		}
	    } catch (final InterruptedException e) {
		LOGGER.debug("Execution interrupted");
	    } finally {
		// Clean up unfinished jobs
		cleanUpExecutingJobs(threads);
	    }

	} else {
	    try {
		LOGGER.debug("Waiting for execution without timeout");
		for (final TaskThread thread : threads) {
		    thread.join();
		}
	    } catch (final InterruptedException e) {
		LOGGER.debug("Execution interrupted");
	    } finally {
		// Clean up unfinished jobs
		cleanUpExecutingJobs(threads);
	    }
	}
    }

    @Override
    public void shutdown() {
    }

}
