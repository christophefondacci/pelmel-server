package com.videopolis.concurrent.exception;

/**
 * An exception thrown when a task fails
 *
 * @author julien
 *
 */
public class TaskExecutionException extends Exception {

    private static final long serialVersionUID = -8459593006968118835L;

    /**
     * Default constructor
     */
    public TaskExecutionException() {
    }

    /**
     * Constructor with a message
     *
     * @param message
     */
    public TaskExecutionException(final String message) {
	super(message);
    }

    /**
     * Consturctor with a message and a cause
     *
     * @param message
     *            Message
     * @param cause
     *            Cause
     */
    public TaskExecutionException(final String message, final Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructor with a cause
     *
     * @param cause
     */
    public TaskExecutionException(final Throwable cause) {
	super(cause);
    }

}
