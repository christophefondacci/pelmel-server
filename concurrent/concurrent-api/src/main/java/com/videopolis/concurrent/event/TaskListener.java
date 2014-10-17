package com.videopolis.concurrent.event;

import java.util.EventListener;

import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * <p>
 * A listener which listen task events.
 * </p>
 * <p>
 * Task events are dispatched during the lifecycle of the task
 * </p>
 * 
 * @see Task
 * @see TaskRunnerService
 * @author julien
 * 
 * @param <V>
 *            Return type of the listened task
 */
public interface TaskListener<V> extends EventListener {

    /**
     * Fired when a task finished successfully
     * 
     * @param task
     *            Task
     * @param context
     *            The task execution context
     * @param result
     *            This task's result
     */
    void taskFinished(Task<V> task, TaskExecutionContext context, V result);

    /**
     * Fired when a task throwed an exception
     * 
     * @param task
     *            Task
     * @param context
     *            The task execution context
     * @param exception
     *            Exception thrown
     */
    void taskFailed(Task<V> task, TaskExecutionContext context,
	    Exception exception);

    /**
     * Fired when a task was interrupted for any reason
     * 
     * @param task
     *            Task
     * @param context
     *            The task execution context
     */
    void taskInterrupted(Task<V> task, TaskExecutionContext context);
}
