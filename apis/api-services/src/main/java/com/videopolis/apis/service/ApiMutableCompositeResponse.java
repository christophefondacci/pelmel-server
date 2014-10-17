package com.videopolis.apis.service;

import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.calm.model.CalmObject;

/**
 * Provides {@link ApiCompositeResponse}-specific setters.
 * 
 * @author cfondacci
 * 
 */
public interface ApiMutableCompositeResponse extends ApiCompositeResponse,
	ApiMutableResponse {

    /**
     * Defines the elements to return in this response, aliased properly for
     * composite response.
     * 
     * @param alias
     *            the alis under which elements should be registered
     * @param elements
     *            elements to install in the response
     */
    void setElements(String alias, final List<? extends CalmObject> elements)
	    throws ApisException;
}
