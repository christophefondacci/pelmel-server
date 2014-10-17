package com.videopolis.concurrent.factory.impl;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.factory.TaskCallableFactory;
import com.videopolis.concurrent.factory.base.AbstractTaskCallableFactory;
import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskCallable;

/**
 * <p>
 * An implementation of {@link TaskCallableFactory} which will maintain a pool
 * of {@link TaskCallable} instances and reuse them.
 * </p>
 * <p>
 * Actual instances of {@link TaskCallable} are obtained using a wrapped
 * {@link TaskCallableFactory}.
 * </p>
 * <p>
 * The pool has a limited size, with a default of {@code DEFAULT_POOL_SIZE}. If
 * more instances than available in the pool are required, the instances will be
 * returned but not pushed back in the pool for future use.
 * </p>
 * <p>
 * If the debug log level is activated for this class, it will collect some
 * statistics about the pool usage which will be printed when the instance is
 * released.
 * </p>
 *
 * @author julien
 *
 */
public class PoolTaskCallableFactoryImpl extends AbstractTaskCallableFactory {

    private static final Log LOGGER = LogFactory
	    .getLog(PoolTaskCallableFactoryImpl.class);

    private static final int DEFAULT_POOL_SIZE = 25;

    /** The pool */
    private Queue<TaskCallable> pool;

    /** Size of the pool */
    private int poolSize;

    /** Wrapped factory used to get actual instances */
    private TaskCallableFactory wrappedFactory;

    // Statistics

    /** The number of calls to get() */
    private volatile int callablesReturned = 0;

    /** The number of instances obtained from the wrapped factory */
    private volatile int callablesCreated = 0;

    /** The number of instances we were unable to push back in the pool */
    private volatile int callablesDestroyed = 0;

    public void setPool(final Queue<TaskCallable> pool) {
	this.pool = pool;
    }

    public void setWrappedFactory(final TaskCallableFactory wrappedFactory) {
	this.wrappedFactory = wrappedFactory;
    }

    /**
     * Called when the instance if initialized
     */
    public void init() {
	if (poolSize == 0) {
	    LOGGER.warn("No pool size set - Using default value "
		    + DEFAULT_POOL_SIZE);
	    poolSize = DEFAULT_POOL_SIZE;
	}

	// Creating pool
	pool = new ArrayBlockingQueue<TaskCallable>(poolSize);
    }

    /**
     * Called when the instance is discarded
     */
    public void dispose() {
	pool.clear();
	if (LOGGER.isDebugEnabled()) {
	    // Show some statistics
	    LOGGER.debug("Pool statistics: " + poolSize
		    + " slots available in pool, " + callablesReturned
		    + " calls to get(), " + callablesCreated
		    + " callable instances created, " + callablesDestroyed
		    + " discarded instances");
	}
    }

    @Override
    public TaskCallable get(final Task<?> task,
	    final MutableTaskExecutionContext context,
	    final TaskCallableFactory factory) {
	// Try to get an instance from the pool
	TaskCallable taskCallable = pool.poll();
	if (taskCallable == null) {
	    // The pool is empty, get a instance from another way
	    taskCallable = wrappedFactory.get(task, context, this);
	    if (LOGGER.isDebugEnabled()) {
		callablesCreated++;
	    }
	} else {
	    // Initialize the instance
	    taskCallable.initialize(task, context, this);
	}

	if (LOGGER.isDebugEnabled()) {
	    callablesReturned++;
	}

	return taskCallable;
    }

    @Override
    public void release(final TaskCallable taskCallable) {
	// Clear the instance
	taskCallable.clear();

	// Put the instance back in the pool, if possible
	if (!pool.offer(taskCallable)) {
	    // Discard instance
	    wrappedFactory.release(taskCallable);

	    if (LOGGER.isDebugEnabled()) {
		callablesDestroyed++;
	    }
	}
    }
}
