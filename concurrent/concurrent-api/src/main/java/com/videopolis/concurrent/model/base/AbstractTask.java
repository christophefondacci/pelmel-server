package com.videopolis.concurrent.model.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;

/**
 * <p>
 * A base class for a Task
 * </p>
 * <p>
 * All the listener logic is already implemented
 * </p>
 *
 * @see Task
 * @author julien
 *
 * @param <V>
 */
public abstract class AbstractTask<V> implements Task<V> {

    /** The listeners */
    private final List<TaskListener<V>> taskListeners = new ArrayList<TaskListener<V>>();

    protected AbstractTask() {
    }

    /**
     * Convenience setter to inject a single task listener using Spring
     *
     * @param taskListener
     */
    public void setTaskListener(final TaskListener<V> taskListener) {
	taskListeners.add(taskListener);
    }

    /**
     * Convenience setter to inject a collection of listeners using Spring
     *
     * @param taskListeners
     */
    public void setTaskListeners(final Collection<TaskListener<V>> taskListeners) {
	taskListeners.addAll(taskListeners);
    }

    @Override
    public final void addTaskListener(final TaskListener<V> l) {
	taskListeners.add(l);
    }

    @Override
    public final void removeTaskListener(final TaskListener<V> l) {
	taskListeners.remove(l);
    }

    @Override
    public final Collection<TaskListener<V>> getTaskListeners() {
	return taskListeners;
    }
}
