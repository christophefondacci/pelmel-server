package com.videopolis.apis.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiMutableCompositeResponse;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * A composite APIS response which hides the presence of a CalmObjectAggregator
 * at the root of the APIS response so that the response will provide all root
 * child as the root elements. <br>
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApiCompositeResponseImpl implements ApiMutableCompositeResponse {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ApiMutableResponse wrappedResponse;

    public ApiCompositeResponseImpl() {
	wrappedResponse = new ApiResponseImpl();
    }

    @Override
    public void setElements(final Collection<? extends CalmObject> elements) {
	wrappedResponse.setElements(elements);
    }

    @Override
    public void setFacetInformation(final FacetInformation facetInformation)
	    throws ApisException {
	wrappedResponse.setFacetInformation(facetInformation);
    }

    @Override
    public Collection<? extends CalmObject> getElements() throws ApisException {
	// A composite response will always have one unique element which acts
	// as an aggregator of child elements.
	final CalmObject rootObject = wrappedResponse.getUniqueElement();
	return rootObject.getConnectedObjects();
    }

    @Override
    public CalmObject getUniqueElement() throws ApisException {
	final Collection<? extends CalmObject> elements = getElements();
	Assert.uniqueElement(
		elements,
		"getUnique() can only be called on results containing 1 and only 1 element, was "
			+ elements.size());
	return elements.iterator().next();
    }

    @Override
    public <T extends CalmObject> T getUniqueElement(Class<T> className,
	    String alias) throws ApisException {
	final List<? extends T> elements = getElements(className, alias);
	Assert.uniqueElement(
		elements,
		"getUnique() can only be called on results containing 1 and only 1 element, was "
			+ elements.size());
	if (elements.isEmpty()) {
	    return null;
	} else {
	    return elements.iterator().next();
	}
    }

    @Override
    public void setLastUpdateTimestamp(final Date lastUpdateTimestamp) {
	wrappedResponse.setLastUpdateTimestamp(lastUpdateTimestamp);
    }

    @Override
    public SearchStatistic getStatistic(final ItemKey key,
	    final String statisticCode) {
	return wrappedResponse.getStatistic(key, statisticCode);
    }

    @Override
    public void setPaginationInfo(final String calType,
	    final PaginationInfo paginationInfo) {
	wrappedResponse.setPaginationInfo(calType, paginationInfo);
    }

    @Override
    public void setStatistic(final ItemKey key, final SearchStatistic stat) {
	wrappedResponse.setStatistic(key, stat);
    }

    @Override
    public PaginationInfo getPaginationInfo(
	    final Class<? extends CalmObject> modelClass) {
	return wrappedResponse.getPaginationInfo(modelClass);
    }

    @Override
    public Date getLastUpdateTimestamp() {
	return wrappedResponse.getLastUpdateTimestamp();
    }

    @Override
    public FacetInformation getFacetInformation() {
	return wrappedResponse.getFacetInformation();
    }

    @Override
    public void setElements(String alias, List<? extends CalmObject> elements)
	    throws ApisException {
	final CalmObject rootObject = wrappedResponse.getUniqueElement();
	Assert.notNull(rootObject,
		"Root object not found for composite response");
	rootObject.addAll(alias, elements);
    }

    @Override
    public <T extends CalmObject> List<? extends T> getElements(
	    Class<T> className, String alias) throws ApisException {
	final CalmObject rootObject = wrappedResponse.getUniqueElement();
	return rootObject.get(className, alias);
    }

    @Override
    public PaginationInfo getPaginationInfo(String alias) {
	return wrappedResponse.getPaginationInfo(alias);
    }

    @Override
    public void setAliasedPaginationInfo(String alias,
	    PaginationInfo paginationInfo) {
	wrappedResponse.setAliasedPaginationInfo(alias, paginationInfo);
    }

    @Override
    public FacetInformation getFacetInformation(SearchScope scope) {
	return wrappedResponse.getFacetInformation(scope);
    }

    @Override
    public void setFacetInformation(SearchScope scope,
	    FacetInformation facetInformation) throws ApisException {
	wrappedResponse.setFacetInformation(scope, facetInformation);
    }

    @Override
    public boolean isPartial() {
	return wrappedResponse.isPartial();
    }

    @Override
    public void setPartial(boolean partial) {
	wrappedResponse.setPartial(partial);
    }
}
