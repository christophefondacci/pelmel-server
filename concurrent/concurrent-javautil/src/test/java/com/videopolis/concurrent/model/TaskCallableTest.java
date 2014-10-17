package com.videopolis.concurrent.model;

import org.junit.Before;
import org.junit.Test;

import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.factory.TaskCallableFactory;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.model.impl.MutableTaskExecutionContextImpl;
import com.videopolis.concurrent.model.impl.TaskCallableImpl;

public class TaskCallableTest {

    private TaskCallableFactory factory;

    @Before
    public void setUp() {
	factory = new TaskCallableFactory() {

	    @Override
	    public void release(TaskCallable taskCallable) {
	    }

	    @Override
	    public TaskCallable get(Task<?> task,
		    MutableTaskExecutionContext context,
		    TaskCallableFactory factory) {
		return null;
	    }

	    @Override
	    public TaskCallable get(Task<?> task,
		    MutableTaskExecutionContext context) {
		return null;
	    }
	};
    }

    @Test(expected = Error.class)
    public void testError() throws Exception {
	final TaskCallable taskCallable = new TaskCallableImpl();

	final Task<Object> task = new AbstractTask<Object>() {
	    @Override
	    public Object execute(TaskExecutionContext context)
		    throws TaskExecutionException, InterruptedException {
		throw new Error("Fatal error!!");
	    }
	};
	taskCallable.initialize(task, new MutableTaskExecutionContextImpl(),
		factory);
	taskCallable.call();
    }
}
