package com.videopolis.cals.exception;

import com.videopolis.calm.exception.CalException;
import com.videopolis.cals.service.CalService;

/**
 * This specific exception should be raised by any {@link CalService} which does
 * not implement a given operation.<br>
 * This will inform the caller that he made an erroneous APIS request call
 * instead of doing nothing silently.
 *
 * @author Christophe Fondacci
 *
 */
public class UnsupportedCalServiceException extends CalException {

    /** Serialization UID */
    private static final long serialVersionUID = -6397826028107211288L;

    /**
     * Constructor with a message
     *
     * @param message
     *            Message
     */
    public UnsupportedCalServiceException(final String message) {
	super(message);
    }
}
