package com.videopolis.concurrent.service.base;

import java.util.Arrays;

import com.videopolis.concurrent.helper.TaskHelper;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.service.TaskRunnerService;

/**
 * A base class for {@link TaskRunnerService} which implements utility methods
 *
 * @author julien
 *
 */
public abstract class AbstractTaskRunnerService implements TaskRunnerService {

    protected AbstractTaskRunnerService() {
    }

    /**
     * {@inheritDoc}
     * <p>
     * This default implementation wraps the tasks into a collection and calls
     * {@code execute(Collection tasks)}
     * </p>
     */
    @Override
    public void execute(final Task<?>... tasks) {
	execute(Arrays.asList(tasks));
    }

    /**
     * Fires taskFinished event
     *
     * @param <V>
     *            Task type
     * @param task
     *            Task
     * @param result
     *            Result
     */
    protected final <V> void fireTaskFinished(final Task<V> task,
	    final TaskExecutionContext context, final V result) {
	TaskHelper.fireTaskFinished(task, context, result);
    }

    /**
     * Fires taskFailed
     *
     * @param <V>
     *            Task type
     * @param task
     *            Task
     * @param exception
     *            Exception
     */
    protected final <V> void fireTaskFailed(final Task<V> task,
	    final TaskExecutionContext context, final Exception exception) {
	TaskHelper.fireTaskFailed(task, context, exception);
    }

    /**
     * Fires taskInterrupted
     *
     * @param <V>
     *            Task type
     * @param task
     *            Task
     */
    protected final <V> void fireTaskInterrupted(final Task<V> task,
	    final TaskExecutionContext context) {
	TaskHelper.fireTaskInterrupted(task, context);
    }
}
