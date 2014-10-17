package com.videopolis.concurrent.model;

import java.util.concurrent.Callable;

import com.videopolis.concurrent.factory.TaskCallableFactory;

/**
 * A {@link Callable} which wraps a {@link Task}
 * 
 * @author julien
 * 
 */
public interface TaskCallable extends Callable<TaskCallable> {

    /**
     * Clears this instance's setup.
     */
    void clear();

    /**
     * Initializes the instance
     * 
     * @param task
     *            Target task
     * @param context
     *            Task's context
     * @param factory
     *            Factory which built this instance
     */
    void initialize(Task<?> task, MutableTaskExecutionContext context,
	    TaskCallableFactory factory);

    /**
     * Ask the task to interrupt, if possible
     */
    void interrupt();
}
