package com.videopolis.concurrent.factory.impl;

import com.videopolis.concurrent.factory.TaskCallableFactory;
import com.videopolis.concurrent.factory.base.AbstractTaskCallableFactory;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskCallable;
import com.videopolis.concurrent.model.impl.TaskCallableImpl;

/**
 * A simple implementation of {@link TaskCallableFactory} which will create new
 * instances of {@link TaskCallable} as required
 *
 * @author julien
 *
 */
public class SimpleTaskCallableFactoryImpl extends AbstractTaskCallableFactory {

    @Override
    public TaskCallable get(final Task<?> task,
	    final MutableTaskExecutionContext context,
	    final TaskCallableFactory factory) {
	final TaskCallable taskCallable = new TaskCallableImpl();
	taskCallable.initialize(task, context, factory);
	return taskCallable;
    }

    @Override
    public void release(final TaskCallable taskCallable) {
	taskCallable.clear();
    }
}
