package com.videopolis.apis.exception;

/**
 * A specific APIS exception indicating that the root element of the request
 * does not exist, thus causing the whole APIS request to fail.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisNoSuchElementException extends ApisException {

    /**
     * 
     */
    private static final long serialVersionUID = 4541665398680478851L;

    /**
     * Builds the exception with a single message and no root cause.
     * 
     * @param message
     *            the exception message
     */
    public ApisNoSuchElementException(String message) {
	super(message);
    }

    /**
     * Builds the exception with a message and a root cause.
     * 
     * @param message
     *            the exception message
     * @param cause
     *            the root cause
     */
    public ApisNoSuchElementException(String message, Exception cause) {
	super(message, cause);
    }
}
