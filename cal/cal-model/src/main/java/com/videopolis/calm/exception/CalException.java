package com.videopolis.calm.exception;

import com.videopolis.calm.model.CalmObject;

/**
 * Exception which might be raised by any {@link CalmObject} or CalService.
 *
 * @author Christophe Fondacci
 *
 */
public class CalException extends Exception {

    /** Serialization UID */
    private static final long serialVersionUID = 6523243617814891570L;

    /**
     * Constructor with a message
     *
     * @param message
     *            Message
     */
    public CalException(final String message) {
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
    public CalException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
