package com.videopolis.concurrent.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * Provides utility methods to work with tasks
 * 
 * @author julien
 * 
 */
public final class TaskHelper {

    private static final Log LOGGER = LogFactory.getLog(TaskHelper.class);

    private TaskHelper() {
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
    public static <V> void fireTaskFinished(final Task<V> task,
	    final TaskExecutionContext context, final V result) {
	for (final TaskListener<V> taskListener : task.getTaskListeners()) {
	    taskListener.taskFinished(task, context, result);
	}
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
    public static <V> void fireTaskFailed(final Task<V> task,
	    final TaskExecutionContext context, final Exception exception) {
	for (final TaskListener<V> taskListener : task.getTaskListeners()) {
	    try {
		taskListener.taskFailed(task, context, exception);
	    } catch (RuntimeException e) {
		// We catch runtime exceptions fired by any listener so that we
		// do not interrupt listener notifications and properly indicate
		// an error in our logs
		LOGGER.error("Listener " + taskListener + " on task " + task
			+ " fired unexpected exception: " + e.getMessage(), e);
	    }
	}
    }

    /**
     * Fires taskInterrupted
     * 
     * @param <V>
     *            Task type
     * @param task
     *            Task
     */
    public static <V> void fireTaskInterrupted(final Task<V> task,
	    final TaskExecutionContext context) {
	for (final TaskListener<V> taskListener : task.getTaskListeners()) {
	    taskListener.taskInterrupted(task, context);
	}
    }
}
