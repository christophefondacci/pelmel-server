package com.videopolis.apis.service;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.CalContext;

/**
 * Aggregated proxy integration service definition. It defines the entry point
 * for performing an {@link ApisRequest}.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApiService {

    /**
     * Executes the provided request and returns a collection of model objects.
     * 
     * @param request
     *            the {@link ApisRequest} to execute
     * @return a collection of {@link CalmObject} representing the elements that
     *         matched the given {@link ApisRequest}.
     */
    ApiResponse execute(ApisRequest request, CalContext context)
	    throws ApisException;
}
