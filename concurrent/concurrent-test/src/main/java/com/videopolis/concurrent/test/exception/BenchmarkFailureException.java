package com.videopolis.concurrent.test.exception;

/**
 * An exception thrown when the benchmark fails
 *
 * @author julien
 *
 */
public class BenchmarkFailureException extends Exception {

    private static final long serialVersionUID = 3656484412660482196L;

    /**
     * Default constructor
     */
    public BenchmarkFailureException() {
    }

    /**
     * Constructor with a message
     *
     * @param message
     *            Message
     */
    public BenchmarkFailureException(final String message) {
	super(message);
    }

    /**
     * Constructor with a message and a cause
     *
     * @param message
     *            Message
     * @param cause
     *            Cause
     */
    public BenchmarkFailureException(final String message, final Throwable cause) {
	super(message, cause);
    }

    /**
     * Constructor with a cause
     *
     * @param cause
     *            Cause
     */
    public BenchmarkFailureException(final Throwable cause) {
	super(cause);
    }

}
