package com.videopolis.apis.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.videopolis.apis.concurrent.impl.SearchAllTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.SearchCriterion;
import com.videopolis.apis.model.SearchMethod;
import com.videopolis.apis.model.base.AbstractWithCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * Implementation of the {@link SearchCriterion} for a ALL search method
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchAllCriterionImpl extends AbstractWithCriterion implements
	SearchCriterion {

    private final String itemType;
    private final Collection<FacetCategory> facettedCategories = new ArrayList<FacetCategory>();
    private final Collection<Facet> facetFilters = new ArrayList<Facet>();
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
    public SearchAllCriterionImpl(
	    final String itemType,
	    final SearchScope scope,
	    final com.videopolis.smaug.common.model.SearchMethod smaugSearchMethod) {
	this.itemType = itemType;
	this.smaugSearchMethod = smaugSearchMethod;
	searchScope = scope;
    }

    @Override
    public Task<ItemsResponse> getTask(final CriteriaContainer parent,
	    final ApisContext context, final CalmObject... parentObjects)
	    throws ApisException {
	final CalService service = ApisRegistry.getCalService(getType());
	final SearchAllTask searchtask = new SearchAllTask(service, context,
		getType(), getPagination());
	searchtask.setSearchScope(searchScope);
	searchtask.setSearchMethod(smaugSearchMethod);
	searchtask.setCriterion(this);
	searchtask.setFacettedCategories(getFacettedCategories());
	searchtask.setFilteredFacets(getFilters());
	searchtask.setSorters(getSorters());
	return searchtask;
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
	return 0;
    }

    @Override
    public SearchMethod getSearchMethod() {
	return SearchMethod.ALL;
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
	return "searchAllCriterion;" + searchScope;
    }
}
