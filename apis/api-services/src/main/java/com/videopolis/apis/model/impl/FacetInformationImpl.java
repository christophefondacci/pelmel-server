package com.videopolis.apis.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.videopolis.apis.model.FacetInformation;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.model.FacetCount;

/**
 * Default implementation of the {@link FacetInformation} interface
 * 
 * @author Christophe Fondacci
 * 
 */
public class FacetInformationImpl implements FacetInformation {

    private Map<FacetCategory, List<FacetCount>> facetCountsCategoriesMap;
    private Map<String, FacetCount> facetsMap;
    private Collection<Facet> facetFilters = Collections.emptyList();

    /**
     * Builds a new facet information bean which wraps the SMAUG facet counts
     * map
     * 
     * @param facetCountsCategoriesMap
     *            a map of all facet counts hashed by the {@link FacetCategory}
     *            they belong to
     */
    public FacetInformationImpl(
	    Map<FacetCategory, List<FacetCount>> facetCountsCategoriesMap) {
	this.facetCountsCategoriesMap = facetCountsCategoriesMap;
	// Building the facets index map
	facetsMap = new HashMap<String, FacetCount>();
	for (List<FacetCount> facetCounts : facetCountsCategoriesMap.values()) {
	    for (FacetCount facetCount : facetCounts) {
		facetsMap.put(facetCount.getFacet().getFacetCode(), facetCount);
	    }
	}
    }

    @Override
    public Collection<FacetCategory> getFacettedCategories() {
	return facetCountsCategoriesMap.keySet();
    }

    @Override
    public Map<FacetCategory, List<FacetCount>> getFacetCountsMap() {
	return facetCountsCategoriesMap;
    }

    @Override
    public List<FacetCount> getFacetCounts(FacetCategory category) {
	final List<FacetCount> facetCounts = facetCountsCategoriesMap
		.get(category);
	if (facetCounts == null) {
	    return Collections.emptyList();
	} else {
	    return facetCounts;
	}
    }

    @Override
    public int getFacetCount(Facet facet) {
	final FacetCount facetCount = facetsMap.get(facet.getFacetCode());
	if (facetCount != null) {
	    return facetCount.getCount();
	} else {
	    return 0;
	}
    }

    @Override
    public Collection<Facet> getFacetFilters() {
	return facetFilters;
    }

    public void setFacetFilters(Collection<Facet> facetFilters) {
	this.facetFilters = facetFilters;
    }

}
