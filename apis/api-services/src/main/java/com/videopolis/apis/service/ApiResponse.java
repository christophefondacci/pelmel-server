package com.videopolis.apis.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This interface describes the response of an {@link ApisRequest}. It provides
 * elements returned by the request <b>and</b> some statistics about the search
 * query, should there be any.<br>
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApiResponse extends Serializable {

    /**
     * @return the elements returned by the evaluation of the
     *         {@link ApisRequest}
     */
    Collection<? extends CalmObject> getElements() throws ApisException;

    /**
     * This is a convenience method to retrieve exactly 1 element. Note that
     * this method will raise an exception if the response contains more or less
     * than 1 element so the caller must be sure that exactly one element is
     * returned
     * 
     * @return the single element of the response.
     */
    CalmObject getUniqueElement() throws ApisException;

    /**
     * Retrieves the statistic referenced by its <code>statisticCode</code> for
     * the given {@link ItemKey} unique key of an element returned by this
     * query. This response will only contain statistics for elements returned
     * by the {@link ApiResponse#getElements()} method.
     * 
     * @param key
     *            unique key of the element to retrieve statistics for
     * @param statisticCode
     *            code of the statistic information to retrieve. These codes
     *            should be provided by one of the {@link ApisCriterion} you
     *            used to create your request.
     * @return the corresponding {@link SearchStatistic} or <code>null</code> if
     *         if none.
     */
    SearchStatistic getStatistic(ItemKey key, String statisticCode);

    /**
     * Retrieves pagination information corresponding to the specified model
     * class. This method will only return a non-null result when the
     * <code>modelClass</code> argument corresponds to a <u>paginated request
     * criterion</u>. A paginated request criterion is explicitely asked through
     * {@link ApisRequest#with(Class, int, int)} or
     * {@link SearchRestriction#with(Class, int, int)} methods.
     * 
     * @param modelClass
     *            the model for which you like {@link PaginationInfo}. This
     *            model should has been queried using a paginated criterion in
     *            the {@link ApisRequest}
     * @return a {@link PaginationInfo} bean or <code>null</code> when the given
     *         model class is not a paginated criterion
     */
    PaginationInfo getPaginationInfo(Class<? extends CalmObject> modelClass);

    /**
     * Retrieves the pagination information corresponding to the specified
     * alias. When an APIS criterion is aliased, its corresponding pagination
     * information will be registered through its alias instead of its model
     * class as alias are always globally unique in the scope of an APIS
     * request.
     * 
     * @param alias
     *            the alias to retrieve pagination information for
     * @return the corresponding {@link PaginationInfo} bean, or
     *         <code>null</code> if no pagination information has been
     *         registered for this alias, either due to a timed out task, a
     *         non-paginated result or an unexisting alias
     */
    PaginationInfo getPaginationInfo(String alias);

    /**
     * Provides the last update timestamp for this response. This timestamp
     * corresponds to the last time the response changed, aggregated from all
     * the underlying CAL responses. If any of the CAL responses is
     * <code>null</code> then this method will return <code>null</code> as that
     * would mean that the last update timestamp cannot be computed.
     * 
     * @return the last update timestamp for this response
     */
    Date getLastUpdateTimestamp();

    /**
     * Provides facet information if available.
     * 
     * @return a {@link FacetInformation} bean when the APIS request asked for
     *         facetted search, else <code>null</code>
     * @deprecated use explicit
     *             {@link ApiResponse#getFacetInformation(SearchScope)} method
     *             for support of multi-facetting information
     */
    @Deprecated
    FacetInformation getFacetInformation();

    /**
     * Provides facet information if available.
     * 
     * @param scope
     *            the {@link SearchScope} to get facet information for
     * @return a {@link FacetInformation} bean when the APIS request asked for
     *         facetted search, else <code>null</code>
     */
    FacetInformation getFacetInformation(SearchScope scope);

    /**
     * Informs that this response only contains partial information because all
     * queried content could either not be fetched in time or because one of the
     * box raised an error.
     * 
     * @return <code>true</code> if the response is partial, or
     *         <code>false</code> when the caller could safely assume that this
     *         response contains all queried content
     */
    boolean isPartial();

}
