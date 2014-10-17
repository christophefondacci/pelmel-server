package com.videopolis.apis.aql.exception;

/**
 * An exception used internally by the parsing mechanisms thrown when the query
 * couldn't be parsed.
 * 
 * It's a subclass of {@link RuntimeException} due to some antlr limitations.
 * 
 * The user will never see this exception, as it will always wrapped to a
 * checked {@link AqlException}
 * 
 * @author julien
 * 
 */
public class QueryParsingException extends RuntimeException {

    private static final long serialVersionUID = -1121298671540915023L;

    public QueryParsingException() {
    }

    public QueryParsingException(String message) {
	super(message);
    }

    public QueryParsingException(Throwable cause) {
	super(cause);
    }

    public QueryParsingException(String message, Throwable cause) {
	super(message, cause);
    }

}
