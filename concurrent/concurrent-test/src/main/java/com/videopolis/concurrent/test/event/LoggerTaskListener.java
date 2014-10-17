package com.videopolis.concurrent.test.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A simple task listener which will just log events.
 *
 * @author julien
 *
 * @param <T>
 *            Task type
 */
public class LoggerTaskListener<T> implements TaskListener<T> {

    private static final Log LOGGER = LogFactory
	    .getLog(LoggerTaskListener.class);

    @Override
    public void taskFailed(final Task<T> task,
	    final TaskExecutionContext context, final Exception exception) {
	LOGGER.info("[" + task + "] FAILED ("
		+ exception.getClass().getCanonicalName() + "): "
		+ exception.getMessage());
    }

    @Override
    public void taskFinished(final Task<T> task,
	    final TaskExecutionContext context, final T result) {
	LOGGER.info("[" + task + "] SUCCESS, returned " + result);
    }

    @Override
    public void taskInterrupted(final Task<T> task,
	    final TaskExecutionContext context) {
	LOGGER.info("[" + task + "] INTERRUPTED");
    }
}
