package com.videopolis.apis.model;

import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.service.SearchService;

/**
 * This interface describes an APIS request context and is designed to store
 * information that will pass through every underlying task of
 * {@link ApisCriterion}.<br>
 * It typically wraps the {@link CalContext} and adds APIS-specific information.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApisContext {

    /**
     * Retrieves the {@link CalContext} to use when calling {@link CalService}
     * 
     * @return the {@link CalContext} to pass to {@link CalService} calls
     */
    CalContext getCalContext();

    /**
     * Retrieves the {@link ApiResponse} which had been initialized to
     * <i>hold</i> all items which will be return to the caller when the
     * processing will be finished.<br>
     * It typically allows criterion tasks to fill meta-information in the
     * response since the returned interface is mutable and therefore provides
     * access to response setters.
     * 
     * @return the {@link ApiMutableResponse} which will be returned to the
     *         client
     */
    ApiMutableResponse getApiResponse();

    /**
     * Retrieves the {@link SearchService} to use when needed.
     * 
     * @return the {@link SearchService} implementation
     */
    SearchService getSearchService();
}
