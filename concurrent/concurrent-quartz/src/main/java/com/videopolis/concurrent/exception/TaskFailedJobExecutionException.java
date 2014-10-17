package com.videopolis.concurrent.exception;

import org.quartz.JobExecutionException;

/**
 * An exeception thrown in a job when a task failed
 *
 * @author julien
 */
public class TaskFailedJobExecutionException extends JobExecutionException {

    private static final long serialVersionUID = -5072734373131121567L;

    /**
     * Constructor with a cause
     *
     * @param cause
     */
    public TaskFailedJobExecutionException(final TaskExecutionException cause) {
	super(cause);
    }

    @Override
    public TaskExecutionException getCause() {
	return (TaskExecutionException) super.getCause();
    }
}
