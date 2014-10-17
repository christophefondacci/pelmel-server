package com.videopolis.apis.aql.handler;

import com.videopolis.apis.model.ApisRequest;

/**
 * A specific type of {@link AqlHandler} which produces an {@link ApisRequest}
 * during parsing.
 * 
 * @author julien
 * 
 */
public interface ApisRequestAqlHandler extends AqlHandler {

    /**
     * After the query has been parsed, returns the built {@link ApisRequest}
     * 
     * @return Built request
     */
    ApisRequest getResult();
}
