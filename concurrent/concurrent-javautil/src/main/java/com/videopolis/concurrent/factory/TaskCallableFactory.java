package com.videopolis.concurrent.factory;

import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskCallable;

/**
 * A factory which provides instances of {@link TaskCallable}
 * 
 * @author julien
 * 
 */
public interface TaskCallableFactory {

    /**
     * Returns a ready to use instance of {@link TaskCallable}
     * 
     * @param task
     *            Task
     * @param context
     *            Context
     * @return TaskCallable instance
     */
    TaskCallable get(Task<?> task, MutableTaskExecutionContext context);

    /**
     * Returns a ready to use instance of {@link TaskCallable}
     * 
     * @param task
     *            Task
     * @param context
     *            Context
     * @param factory
     *            The factory which built this instance
     * @return TaskCallable instance
     */
    TaskCallable get(Task<?> task, MutableTaskExecutionContext context,
	    TaskCallableFactory factory);

    /**
     * Release an instance of TaskCallable when the caller has finished to work
     * with it
     * 
     * @param taskCallable
     *            Instance to release
     */
    void release(TaskCallable taskCallable);
}
