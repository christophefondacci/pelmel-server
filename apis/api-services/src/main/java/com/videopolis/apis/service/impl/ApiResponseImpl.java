package com.videopolis.apis.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * Default implementation of the {@link ApiResponse}. This class has not be made
 * final but should normally not be extended.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApiResponseImpl implements ApiMutableResponse {

    /** Serialization unique ID */
    private static final long serialVersionUID = 1L;
    private Collection<? extends CalmObject> elements;
    private final Map<MultiKey, SearchStatistic> statisticsMap;
    private final Map<String, PaginationInfo> paginationMap;
    private final Map<String, PaginationInfo> paginationAliasMap;
    private Date lastUpdateTimestamp = null;
    private final Map<SearchScope, FacetInformation> facetInfoMap;
    private boolean isPartial = false;

    /**
     * Default empty constructor
     */
    public ApiResponseImpl() {
	statisticsMap = Collections
		.synchronizedMap(new HashMap<MultiKey, SearchStatistic>());
	paginationMap = Collections
		.synchronizedMap(new HashMap<String, PaginationInfo>());
	paginationAliasMap = Collections
		.synchronizedMap(new HashMap<String, PaginationInfo>());
	facetInfoMap = Collections
		.synchronizedMap(new HashMap<SearchScope, FacetInformation>());
	elements = Collections.emptyList();
	lastUpdateTimestamp = new Date();
    }

    @Override
    public Collection<? extends CalmObject> getElements() {
	return elements;
    }

    @Override
    public SearchStatistic getStatistic(final ItemKey key,
	    final String statisticCode) {
	return statisticsMap.get(new MultiKey(key, statisticCode));
    }

    @Override
    public void setElements(final Collection<? extends CalmObject> elements) {
	this.elements = elements;
    }

    @Override
    public void setStatistic(final ItemKey key, final SearchStatistic stat) {
	statisticsMap.put(new MultiKey(key, stat.getCode()), stat);
    }

    @Override
    public CalmObject getUniqueElement() throws ApisException {
	Assert.uniqueElement(elements,
		"You cannot get a unique element from a non unique collection");
	if (elements.isEmpty()) {
	    return null;
	}
	return elements.iterator().next();
    }

    @Override
    public PaginationInfo getPaginationInfo(
	    final Class<? extends CalmObject> modelClass) {
	return paginationMap.get(ApisRegistry.getTypeFromModel(modelClass));
    }

    @Override
    public PaginationInfo getPaginationInfo(String alias) {
	return paginationAliasMap.get(alias);
    }

    @Override
    public void setPaginationInfo(final String calType,
	    final PaginationInfo paginationInfo) {
	paginationMap.put(calType, paginationInfo);
    }

    @Override
    public void setAliasedPaginationInfo(String alias,
	    PaginationInfo paginationInfo) {
	paginationAliasMap.put(alias, paginationInfo);
    }

    @Override
    public Date getLastUpdateTimestamp() {
	return lastUpdateTimestamp;
    }

    @Override
    public void setLastUpdateTimestamp(final Date lastUpdateTimestamp) {
	this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    @Override
    public FacetInformation getFacetInformation() {
	final FacetInformation facetInfo = facetInfoMap.get(null);
	// Preserving compatibility
	if (facetInfo == null && facetInfoMap.entrySet().size() == 1) {
	    return facetInfoMap.values().iterator().next();
	} else {
	    return facetInfo;
	}
    }

    @Override
    public void setFacetInformation(FacetInformation facetInformation)
	    throws ApisException {
	facetInfoMap.put(null, facetInformation);
    }

    @Override
    public void setFacetInformation(SearchScope scope,
	    FacetInformation facetInformation) throws ApisException {
	final FacetInformation previousFacetInfo = facetInfoMap.get(scope);
	Assert.isNull(
		previousFacetInfo,
		"Attempt to define several facetting information for a same scope: this is not supported by APIS yet");
	facetInfoMap.put(scope, facetInformation);
    }

    @Override
    public FacetInformation getFacetInformation(SearchScope scope) {
	return facetInfoMap.get(scope);
    }

    @Override
    public boolean isPartial() {
	return isPartial;
    }

    @Override
    public void setPartial(boolean isPartial) {
	this.isPartial = isPartial;
    }
}
