package com.videopolis.apis.model;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.exception.ApisNoSuchElementException;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This interface represents an aggregated proxy integration request. This
 * request allows callers to define :<br>
 * - The type of items to retrieve<br>
 * - The type of information attached to this item<br>
 * - Search criterias<br>
 * - Sorting criterias<br>
 * <br>
 * <br>
 * This class also provides helper methods to easily build a request using a
 * "pass-through" {@link ApisRequest} approach.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApisRequest extends CriteriaContainer {

    /**
     * Retrieves an element through its unique key.
     * 
     * @param id
     *            unique id of the item
     * @return the request
     */
    ApisRequest uniqueKey(Long id) throws ApisException;

    /**
     * Retrieves an element through its unique key. Alternative method for
     * string-based unique identifiers
     * 
     * @param id
     *            unique character-based identifier of the item
     * @return the request
     */
    ApisRequest uniqueKey(String id) throws ApisException;

    /**
     * Searches for all elements of the given type using the provided search
     * scope.
     * 
     * @param type
     *            the object type to look for
     * @param scope
     *            the {@link SearchScope} to use
     * @param pageSize
     *            the number of elements on one page of result
     * @param pageOffset
     *            page offset to start element retrieval from
     * @return this {@link ApisRequest}
     */
    ApisRequest searchAll(Class<? extends CalmObject> type, SearchScope scope,
	    int pageSize, int pageOffset) throws ApisException;

    /**
     * Searches for all elements of the given type using the provided search
     * scope and a customized search method.
     * 
     * @param type
     *            the object type to look for
     * @param scope
     *            the {@link SearchScope} to use
     * @param method
     *            the {@link SearchMethod} to use with SMAUG search service
     * @param pageSize
     *            the number of elements on one page of result
     * @param pageOffset
     *            page offset to start element retrieval from
     * @return this {@link ApisRequest}
     */
    ApisRequest searchAll(Class<? extends CalmObject> type, SearchScope scope,
	    SearchMethod method, int pageSize, int pageOffset)
	    throws ApisException;

    /**
     * Adds a connection criteria which restricts the search to items which are
     * connected to the one specified.
     * 
     * @param type
     *            type of the item to which searched items are connected
     * @param id
     *            unique identifier of the item to which the searched items are
     *            connected
     * @return the request
     */
    ApisRequest connectedTo(Class<? extends CalmObject> type, Long id)
	    throws ApisException;

    /**
     * Retrieves an element through its alternate key. An alternate key is a key
     * whose type differs from the provided type of the element but which can be
     * used to retrieve a unique item. <br>
     * For example, a page name (=the human-readable text which appears in the
     * url) of an hotel is an alternate key for this hotel.
     * 
     * @param alternateKeyType
     *            type of this key information
     * @param keyValue
     *            value of the alternate key
     * @return the request
     */
    ApisRequest alternateKey(String alternateKeyType, String keyValue)
	    throws ApisException;

    /**
     * Retrieves a list of elements of a specific type with a particular request
     * type. The exact behavior of this method will depend on the underlying CAL
     * service.
     * 
     * @param type
     *            Type
     * @param requestType
     *            Request type
     * @return the request
     * @throws ApisException
     */
    ApisRequest list(Class<? extends CalmObject> type, RequestType requestType)
	    throws ApisException;

    /**
     * Retrieves the root criterion of this APIS request
     * 
     * @return the root criterion
     */
    ApisCriterion getRootCriterion();

    /**
     * Retrieves the APIS response class to instantiate when building response
     * 
     * @return an {@link ApiMutableResponse} concrete class which provides an
     *         empty constructor
     */
    Class<? extends ApiMutableResponse> getResponseClass();

    /**
     * Indicates that this APIS request can consider valid a response without
     * any resulting object. Calling this method on your APIS request will
     * prevent the ApiService from throwing an
     * {@link ApisNoSuchElementException} when no root element is provided in
     * the response. Be careful on doing this because that will mean that the
     * detection of a 404 will be the responsibility of the caller since APIS
     * will have no way of detecting it.
     * 
     * @return this request for convenience constructions
     */
    ApisRequest allowEmptyResults();

    /**
     * Returns whether this request allows empty responses, that is to say
     * responses without any resulting object.
     * 
     * @return <code>true</code> if APIS can return an {@link ApiResponse}
     *         containing no elements, or <code>false</code> (the default) to
     *         tell the framework to raise a {@link ApisNoSuchElementException}.
     */
    boolean isEmptyResultsAllowed();
}
