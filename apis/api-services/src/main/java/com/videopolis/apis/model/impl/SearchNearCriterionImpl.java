package com.videopolis.apis.model.impl;

import java.util.Collection;
import java.util.Collections;

import com.videopolis.apis.concurrent.impl.SearchNearbyTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
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

public class SearchNearCriterionImpl extends AbstractWithCriterion implements
		SearchCriterion {

	private Collection<FacetCategory> facettedCategories;
	private Collection<Facet> filters;
	private final double radius;
	private final com.videopolis.smaug.common.model.SearchMethod searchMethod;
	private final SearchScope scope;
	private final String itemType;
	private final double latitude, longitude;

	public SearchNearCriterionImpl(String itemType, SearchScope scope,
			com.videopolis.smaug.common.model.SearchMethod method,
			double latitude, double longitude, double radius) {
		this.itemType = itemType;
		this.scope = scope;
		this.searchMethod = method;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}

	@Override
	public Task<ItemsResponse> getTask(CriteriaContainer parent,
			ApisContext context, CalmObject... parentObjects)
			throws ApisException {
		final CalService service = ApisRegistry.getCalService(getType());
		final SearchNearbyTask task = new SearchNearbyTask(service, context,
				itemType, new Localized() {

					@Override
					public double getLongitude() {
						return longitude;
					}

					@Override
					public double getLatitude() {
						return latitude;
					}
				}, radius, getPagination());
		task.setCriterion(this);
		final Collection<FacetCategory> facettedCategories = getFacettedCategories();
		task.setSearchScope(scope);
		task.setSearchMethod(searchMethod);
		task.setFacettedCategories(facettedCategories);
		task.setFilteredFacets(getFilters());
		task.setSorters(getSorters());
		return task;
	}

	@Override
	public String getType() {
		return itemType;
	}

	@Override
	public Collection<FacetCategory> getFacettedCategories() {
		if (facettedCategories == null) {
			return Collections.emptyList();
		} else {
			return facettedCategories;
		}
	}

	@Override
	public Collection<Facet> getFilters() {
		if (filters == null) {
			return Collections.emptyList();
		} else {
			return filters;
		}
	}

	@Override
	public double getRadius() {
		return radius;
	}

	@Override
	public SearchMethod getSearchMethod() {
		return SearchMethod.NEARBY;
	}

	@Override
	public SearchCriterion facettedBy(
			Collection<FacetCategory> facettedCategories) {
		this.facettedCategories = facettedCategories;
		return this;
	}

	@Override
	public SearchCriterion filteredBy(Collection<Facet> facetFilters) {
		this.filters = facetFilters;
		return this;
	}
}
