package com.videopolis.concurrent.event.base;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A default empty implementation of {@link TaskListener}. All methods do
 * nothing.
 *
 * @inheritDoc
 * @author julien
 *
 * @param <V>
 */
public abstract class AbstractTaskListener<V> implements TaskListener<V> {

    @Override
    public void taskFailed(final Task<V> task,
	    final TaskExecutionContext context, final Exception exception) {
    }

    @Override
    public void taskFinished(final Task<V> task,
	    final TaskExecutionContext context, final V result) {
    }

    @Override
    public void taskInterrupted(final Task<V> task,
	    final TaskExecutionContext context) {
    }
}
