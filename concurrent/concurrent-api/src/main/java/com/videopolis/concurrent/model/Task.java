package com.videopolis.concurrent.model;

import java.util.Collection;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;

/**
 * <p>
 * A task
 * </p>
 * <p>
 * Implementors should not implement this interface directly, but subclass the
 * existing {@link AbstractTask} abstract class.
 * </p>
 * 
 * @see AbstractTask
 * @see TaskListener
 * @author julien
 * 
 * @param <V>
 *            Type of returned value
 */
public interface Task<V> {

    /**
     * Execute this task
     * 
     * @return Return value
     * @throws TaskExecutionException
     *             If the execution fails
     * @throws InterruptionException
     *             When the execution thread is interrupted
     */
    V execute(TaskExecutionContext context) throws TaskExecutionException,
	    InterruptedException;

    /**
     * <p>
     * Adds a TaskListener to this task.
     * </p>
     * <p>
     * The listener will be notified of events on the task
     * </p>
     * 
     * @param l
     *            Listener
     */
    void addTaskListener(TaskListener<V> l);

    /**
     * Removes a TaskListener from this task
     * 
     * @param l
     *            Listener
     */
    void removeTaskListener(TaskListener<V> l);

    /**
     * Returns all the TaskListener sets for this task
     * 
     * @return TaskListeners
     */
    Collection<TaskListener<V>> getTaskListeners();
}
