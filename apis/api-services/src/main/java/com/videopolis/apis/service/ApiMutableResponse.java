package com.videopolis.apis.service;

import java.util.Collection;
import java.util.Date;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * Provides access to setters of an {@link ApiResponse}.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApiMutableResponse extends ApiResponse {

    /**
     * Defines the elements to return in this response.
     * 
     * @param elements
     *            returned elements
     */
    void setElements(final Collection<? extends CalmObject> elements);

    /**
     * Defines the facet information. Note that this method was designed for
     * single facetted search per request. Setting facet information a second
     * time through this method will result in an {@link ApisException}. Proper
     * management of multi-facetting could be done through
     * {@link ApiMutableResponse#setFacetInformation(SearchScope,FacetInformation)}
     * 
     * @param facetInformation
     *            the {@link FacetInformation}
     * @deprecated please use the
     *             {@link ApiMutableResponse#setFacetInformation(SearchScope, FacetInformation)}
     *             for proper compatibility with multi-facetting
     */
    @Deprecated
    void setFacetInformation(FacetInformation facetInformation)
	    throws ApisException;

    /**
     * Defines the facet information for the specified scope. This method allows
     * APIS users to handle mutli-facetting because {@link FacetInformation}
     * will be registered per scope.
     * 
     * @param scope
     *            the {@link SearchScope} for which {@link FacetInformation}
     *            will be registered
     * @param facetInformation
     *            the {@link FacetInformation}
     * 
     * @since 0.0.5
     */
    void setFacetInformation(SearchScope scope,
	    FacetInformation facetInformation) throws ApisException;

    /**
     * Defines the last time this response has been updated. This timestamp
     * should be computed from the highest timestamp of every underlying CAL
     * service responses.<br>
     * This method is called internally by the framework and should generally
     * not be called directly by APIS framework clients.
     * 
     * @param lastUpdateTimestamp
     *            last update timestamp of this response
     */
    void setLastUpdateTimestamp(Date lastUpdateTimestamp);

    /**
     * Defines the pagination information for this model class.
     * 
     * @param calType
     *            CAL item type to attach the pagination information to
     * @param paginationInfo
     *            a {@link PaginationInfo} bean
     */
    void setPaginationInfo(String calType, PaginationInfo paginationInfo);

    /**
     * Registers the specified pagination information under the given alias.
     * 
     * @param alias
     *            the alias to register the pagination information with
     * @param paginationInfo
     *            the {@link PaginationInfo} bean to register
     */
    void setAliasedPaginationInfo(String alias, PaginationInfo paginationInfo);

    /**
     * Adds a {@link SearchStatistic} to this response attached to the specified
     * element.
     * 
     * @param key
     *            unique key of the element to attach this statistic value to
     * @param stat
     *            statistic value
     */
    void setStatistic(ItemKey key, SearchStatistic stat);

    /**
     * Indicates whether this response contains partial or full requested data
     * 
     * @param partial
     *            <code>true</code> to indicate that this response contains
     *            partial data, or <code>false</code> if all data has
     *            successfully been fetched
     */
    void setPartial(boolean partial);
}
