package com.videopolis.apis.service;

import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.calm.model.CalmObject;

/**
 * A composite APIS response is a response that can hold several set of root
 * elements.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApiCompositeResponse extends ApiResponse {

    /**
     * Retrieves the unique root element specified by its class name and the
     * alias with which it has been registered.
     * 
     * @param className
     *            the class of the unique element to retrieve
     * @param alias
     *            the alias (=name) with which this single element has been
     *            registered in the APIS query
     * @return the corresponding {@link CalmObject} or <code>null</code> if none
     * @throws ApisException
     *             if there is more than 1 matching element
     */
    <T extends CalmObject> T getUniqueElement(Class<T> className, String alias)
	    throws ApisException;

    /**
     * Retrieves the root element specified by its class name and the alias with
     * which it has been registered.
     * 
     * @param className
     *            the class of the elements to retrieve
     * @param alias
     *            the alias (=name) with which this set of elements has been
     *            registered in the APIS query
     * @return the list of corresponding {@link CalmObject}
     */
    <T extends CalmObject> List<? extends T> getElements(Class<T> className,
	    String alias) throws ApisException;
}
