package com.videopolis.concurrent.model.base;

import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * <p>
 * An abstract implementation of a stateful task.
 * </p>
 * <p>
 * The {@code execute(TaskExecutionContext context)} is replaced by
 * {@code execute()}. The context can be obtained using {@code getContext()}
 * </p>
 * <p>
 * The new {@code execute()} method does not return any value. Instead, the task
 * must call the {@code setResult(V result)} method.
 * </p>
 *
 * @author julien
 *
 * @param <V>
 */
public abstract class AbstractStatefulTask<V> extends AbstractTask<V> {

    private TaskExecutionContext context;
    private V result;

    @Override
    public final V execute(final TaskExecutionContext context)
	    throws TaskExecutionException, InterruptedException {
	this.context = context;
	try {
	    execute();
	    return result;
	} finally {
	    // Reset everything anyway
	    this.context = null;
	    this.result = null;
	}
    }

    /**
     * Returns the execution context of the task
     *
     * @return Execution context
     */
    protected TaskExecutionContext getContext() {
	return context;
    }

    /**
     * Sets the result of the execution
     *
     * @param result
     */
    protected void setResult(final V result) {
	this.result = result;
    }

    /**
     * Executes the task
     *
     * @return Return value
     * @throws TaskExecutionException
     *             If the execution fails
     * @throws InterruptionException
     *             When the execution thread is interrupted
     */
    protected abstract void execute() throws TaskExecutionException,
	    InterruptedException;
}
