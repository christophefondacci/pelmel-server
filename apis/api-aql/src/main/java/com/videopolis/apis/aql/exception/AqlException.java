package com.videopolis.apis.aql.exception;

/**
 * An exception throws if an AQL query couldn't be parsed
 * 
 * @author julien
 * 
 */
public class AqlException extends Exception {

    private static final long serialVersionUID = -5781900265520657119L;

    public AqlException() {
    }

    public AqlException(String message) {
	super(message);
    }

    public AqlException(Throwable cause) {
	super(cause);
    }

    public AqlException(String message, Throwable cause) {
	super(message, cause);
    }
}
