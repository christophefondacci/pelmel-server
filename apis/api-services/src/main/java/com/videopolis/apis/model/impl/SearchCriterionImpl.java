package com.videopolis.apis.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.videopolis.apis.concurrent.impl.SearchContainedTask;
import com.videopolis.apis.concurrent.impl.SearchNearbyTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.SearchCriterion;
import com.videopolis.apis.model.SearchMethod;
import com.videopolis.apis.model.base.AbstractWithCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * Default implementation of the {@link SearchCriterion}
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchCriterionImpl extends AbstractWithCriterion implements
	SearchCriterion {

    private final String itemType;
    private double radius;
    private final Collection<FacetCategory> facettedCategories = new ArrayList<FacetCategory>();
    private final Collection<Facet> facetFilters = new ArrayList<Facet>();
    private final SearchMethod searchMethod;
    private final com.videopolis.smaug.common.model.SearchMethod smaugSearchMethod;
    private final SearchScope searchScope;

    /**
     * Builds a "contained" search which will search for items of the specified
     * type contained in the parent item of this criterion.
     * 
     * @param itemType
     *            type of CAL items to search for
     * @param scope
     *            The search scope
     * @param smaugSearchMethod
     *            the Smaug search method
     */
    public SearchCriterionImpl(
	    final String itemType,
	    final SearchScope scope,
	    final com.videopolis.smaug.common.model.SearchMethod smaugSearchMethod) {
	this.itemType = itemType;
	searchMethod = SearchMethod.CONTAINED;
	this.smaugSearchMethod = smaugSearchMethod;
	searchScope = scope;
    }

    /**
     * Builds a "nearby" search which will search for items of the specified
     * type using proximity search starting from the parent item localization
     * with the supplied radius.
     * 
     * @param itemType
     *            type of CAL items to search for
     * @param radius
     *            search radius
     * @param scope
     *            The search scope
     * @param smaugSearchMethod
     *            the Smaug search method
     */
    public SearchCriterionImpl(
	    final String itemType,
	    final SearchScope scope,
	    final com.videopolis.smaug.common.model.SearchMethod smaugSearchMethod,
	    final double radius) {
	this.itemType = itemType;
	this.radius = radius;
	searchMethod = SearchMethod.NEARBY;
	this.smaugSearchMethod = smaugSearchMethod;
	searchScope = scope;
    }

    @Override
    public Task<ItemsResponse> getTask(final CriteriaContainer parent,
	    final ApisContext context, final CalmObject... parentObjects)
	    throws ApisException {
	Assert.uniqueElement(
		"Nearest search is not supported for collection of parents, you must have a unique parent to search nearbys from",
		parentObjects);
	final CalmObject parentObject = parentObjects[0];
	final CalService service = ApisRegistry.getCalService(getType());
	switch (searchMethod) {
	case NEARBY:
	    Assert.instanceOf(parentObject, Localized.class,
		    "Cannot perform a nearby search: parent item is not Localized");
	    final SearchNearbyTask task = new SearchNearbyTask(service,
		    context, getType(), (Localized) parentObject, radius,
		    getPagination());
	    task.setCriterion(this);
	    final Collection<FacetCategory> facettedCategories = getFacettedCategories();
	    task.setSearchScope(searchScope);
	    task.setSearchMethod(smaugSearchMethod);
	    task.setFacettedCategories(facettedCategories);
	    task.setFilteredFacets(getFilters());
	    task.setSorters(getSorters());
	    return task;
	case CONTAINED:
	    final SearchContainedTask searchtask = new SearchContainedTask(
		    service, context, getType(), parentObject.getKey(),
		    getPagination());
	    searchtask.setSearchScope(searchScope);
	    searchtask.setSearchMethod(smaugSearchMethod);
	    searchtask.setCriterion(this);
	    searchtask.setFacettedCategories(getFacettedCategories());
	    searchtask.setFilteredFacets(getFilters());
	    searchtask.setSorters(getSorters());
	    return searchtask;
	default:
	    throw new ApisException("Unsupported search");
	}
    }

    @Override
    public String getType() {
	return itemType;
    }

    @Override
    public Collection<FacetCategory> getFacettedCategories() {
	return facettedCategories;
    }

    @Override
    public Collection<Facet> getFilters() {
	return facetFilters;
    }

    @Override
    public double getRadius() {
	return radius;
    }

    @Override
    public SearchMethod getSearchMethod() {
	return searchMethod;
    }

    @Override
    public SearchCriterion facettedBy(
	    final Collection<FacetCategory> facettedCategories) {
	this.facettedCategories.addAll(facettedCategories);
	return this;
    }

    @Override
    public SearchCriterion filteredBy(final Collection<Facet> facetFilters) {
	this.facetFilters.addAll(facetFilters);
	return this;
    }

    @Override
    public String toString() {
	return "search" + searchMethod + "Criterion;" + searchScope;
    }
}
