package com.videopolis.apis.exception;

/**
 * An exception raised by the APIS component. Any exception raised by the
 * Aggregated Proxy Integration Services derives from this base exception.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisException extends Exception {

    /** Serialization unique ID */
    private static final long serialVersionUID = 3002611686116803376L;

    /**
     * Creates an APIS exception with the given message.
     * 
     * @param message
     *            exception message
     */
    public ApisException(String message) {
	super(message);
    }

    /**
     * Creates an APIS exception with the given message and cause.
     * 
     * @param message
     *            message to embed in the exception
     * @param cause
     *            cause of the exception
     */
    public ApisException(String message, Throwable cause) {
	super(message, cause);
    }
}
