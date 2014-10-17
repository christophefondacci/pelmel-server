package com.videopolis.concurrent.factory.base;

import com.videopolis.concurrent.factory.TaskCallableFactory;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskCallable;

/**
 * A base implementation for {@link TaskCallableFactory}
 *
 * @author julien
 *
 */
public abstract class AbstractTaskCallableFactory implements
	TaskCallableFactory {

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation just calls {@code get(task, context, this)}
     * </p>
     */
    @Override
    public TaskCallable get(final Task<?> task,
	    final MutableTaskExecutionContext context) {
	return get(task, context, this);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation does nothing
     * </p>
     */
    @Override
    public void release(final TaskCallable taskCallable) {
    }
}
