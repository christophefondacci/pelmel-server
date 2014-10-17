package com.videopolis.concurrent.test.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;

/**
 * A stupid task which just wait.
 *
 * @author julien
 *
 */
public class DumbTask extends AbstractTask<String> {

    private static final Log LOGGER = LogFactory.getLog(DumbTask.class);

    private static final int INTERMEDIATE_TIMING = 2500;

    private final long millis;

    /**
     * Default constructor
     *
     * @param millis
     *            How long to wait
     * @param listeners
     *            The listeners
     */
    public DumbTask(final long millis, final TaskListener<String>... listeners) {
	this.millis = millis;
	for (final TaskListener<String> listener : listeners) {
	    addTaskListener(listener);
	}
    }

    @Override
    public String execute(final TaskExecutionContext context)
	    throws TaskExecutionException, InterruptedException {
	try {
	    LOGGER.info("begin execute() with t=" + millis);
	    if (millis == INTERMEDIATE_TIMING) {
		throw new TaskExecutionException("You're fucked");
	    }
	    Thread.sleep(millis);
	    LOGGER.info("end execute() with t=" + millis);

	    return Thread.currentThread().getName() + " woke after " + millis;
	} finally {
	    LOGGER.info("Interrupted? " + context.isExecutionInterrupted());
	}
    }

    @Override
    public String toString() {
	return "sleep:" + millis + "ms";
    }

}
