package com.videopolis.concurrent.exception;

import org.quartz.JobExecutionException;

/**
 * An exception thrown by a job when the task is interrupted
 *
 * @author julien
 *
 */
public class TaskInterruptedJobExecutionException extends JobExecutionException {

    private static final long serialVersionUID = -2817467328033402438L;

    /**
     * Default constructor
     */
    public TaskInterruptedJobExecutionException() {
    }

    /**
     * Constructor with a cause
     *
     * @param cause
     */
    public TaskInterruptedJobExecutionException(final Throwable cause) {
	super(cause);
    }

}
