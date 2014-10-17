package com.videopolis.apis.aql.handler;

/**
 * A specific type of {@link AqlHandler} which produces a String during parsing.
 * 
 * @author julien
 * 
 */
public interface StringAqlHandler extends AqlHandler {

    /**
     * After the query has been parsed, returns the built String
     * 
     * @return Built string
     */
    String getResult();
}
