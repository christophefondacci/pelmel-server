package com.videopolis.concurrent.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.factory.ConcurrentFactory;
import com.videopolis.concurrent.factory.TaskCallableFactory;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskCallable;
import com.videopolis.concurrent.service.base.AbstractTimeoutTaskRunnerService;

/**
 * An implementation of {@link AbstractTimeoutTaskRunnerService} which uses an
 * {@link ExecutorService} to call tasks.
 *
 * @author julien
 *
 */
public class ExecutorServiceTaskRunnerServiceImpl extends
	AbstractTimeoutTaskRunnerService {

    private static final Log LOGGER = LogFactory
	    .getLog(ExecutorServiceTaskRunnerServiceImpl.class);

    private ExecutorService executorService;
    private TaskCallableFactory taskCallableFactory;

    @Override
    public void execute(final Collection<Task<?>> tasks) {

	// Create the tasks
	final List<TaskCallable> callables = new ArrayList<TaskCallable>(
		tasks.size());
	for (final Task<?> task : tasks) {
	    callables.add(taskCallableFactory.get(task,
		    ConcurrentFactory.newMutableTaskExecutionContext()));
	}

	try {
	    // Run them
	    if (hasTimeout()) {
		executorService.invokeAll(callables, getTimeout(),
			TimeUnit.MILLISECONDS);
	    } else {
		executorService.invokeAll(callables);
	    }
	} catch (final InterruptedException e) {
	    LOGGER.debug("Execution interrupted");
	} finally {
	    // Interrupt jobs
	    interruptAll(callables);
	}
    }

    /**
     * Interrupts all tasks
     *
     * @param callables
     */
    private void interruptAll(final List<TaskCallable> callables) {
	for (final TaskCallable callable : callables) {
	    // Request interrupt. The task will be actually interrupty only
	    // if it's necessary
	    callable.interrupt();
	}
    }

    /**
     * Sets the {@link ExecutorService} instance to use
     *
     * @param executorService
     */
    public void setExecutorService(final ExecutorService executorService) {
	this.executorService = executorService;
    }

    /**
     * Sets the {@link TaskCallableFactory} instance to use
     *
     * @param taskCallableFactory
     */
    public void setTaskCallableFactory(
	    final TaskCallableFactory taskCallableFactory) {
	this.taskCallableFactory = taskCallableFactory;
    }

    @Override
    public void shutdown() {
	executorService.shutdown();
    }
}
