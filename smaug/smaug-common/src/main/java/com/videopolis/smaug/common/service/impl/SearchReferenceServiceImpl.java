package com.videopolis.smaug.common.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.videopolis.smaug.common.exception.SearchReferenceException;
import com.videopolis.smaug.common.factory.FacetCategoryFactory;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.helper.Assert;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SmaugSorter;
import com.videopolis.smaug.common.model.impl.xml.FacetCategories;
import com.videopolis.smaug.common.model.impl.xml.Sorters;
import com.videopolis.smaug.common.service.SearchReferenceService;

/**
 * Default implementation of the {@link SearchReferenceService} which uses XML
 * files to fetch configuration.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchReferenceServiceImpl implements SearchReferenceService {

    private FacetCategories facetCategories;
    private Sorters sorters;
    private String rangeFormat;

    private final Map<String, FacetCategory> facetCategoryCache;
    private final Map<String, FacetCategory> facetCategoryUrlCache;
    private final List<FacetCategory> facettedCategories;
    private final Map<String, List<SmaugSorter>> sorterFieldMap;
    private final Map<String, SmaugSorter> sorterUrlMap;
    private final Map<String, Facet> facetCache;
    private final List<String> sorterFields;
    private final List<SmaugSorter> defaultSorters;

    /**
     * Default constructor
     */
    public SearchReferenceServiceImpl() {
	facetCategoryCache = new HashMap<String, FacetCategory>();
	facetCategoryUrlCache = new HashMap<String, FacetCategory>();
	sorterFieldMap = new HashMap<String, List<SmaugSorter>>();
	sorterUrlMap = new HashMap<String, SmaugSorter>();
	sorterFields = new ArrayList<String>();
	facetCache = new HashMap<String, Facet>();
	facettedCategories = new ArrayList<FacetCategory>();
	defaultSorters = new ArrayList<SmaugSorter>();
    }

    @Override
    public Facet getFacet(final String categoryCode, final String facetCode)
	    throws SearchReferenceException {
	return getFacet(getFacetCategory(categoryCode), facetCode);
    }

    @Override
    public FacetRange getFacetRange(final String categoryCode,
	    final long lowerBound, final long higherBound)
	    throws SearchReferenceException {
	return getFacetRange(getFacetCategory(categoryCode), lowerBound,
		higherBound);
    }

    @Override
    public Facet getFacet(final FacetCategory category, final String facetCode)
	    throws SearchReferenceException {
	final String facetCacheKey = buildFacetCacheKey(
		category.getCategoryCode(), facetCode);
	Facet facet = facetCache.get(facetCacheKey);
	if (facet == null) {
	    facet = FacetFactory.createFacet(category, facetCode);
	    // Only caching depending on the category
	    if (category.isCachingFacets()) {
		facetCache.put(facetCacheKey, facet);
	    }
	}
	return facet;
    }

    @Override
    public FacetRange getFacetRange(final FacetCategory category,
	    final long lowerBound, final long higherBound)
	    throws SearchReferenceException {
	return FacetFactory.createFacetRange(category, rangeFormat, lowerBound,
		higherBound);
    }

    @Override
    public FacetCategory getFacetCategory(final String categoryCode)
	    throws SearchReferenceException {
	final FacetCategory category = facetCategoryCache.get(categoryCode);
	Assert.notNull(category, "Invalid facet category code: " + categoryCode);
	return category;
    }

    @Override
    public FacetCategory getFacetCategoryForUrlCode(final String urlCode)
	    throws SearchReferenceException {
	final FacetCategory category = facetCategoryUrlCache.get(urlCode);
	Assert.notNull(category, "Invalid facet category URL code: " + category);
	return category;
    }

    @Override
    public List<FacetCategory> getFacettedCategories() {
	return Collections.unmodifiableList(facettedCategories);
    }

    @Override
    public List<FacetCategory> getFacettedCategories(
	    final SearchScope searchScope) {
	final List<FacetCategory> filteredFacetCategories = new LinkedList<FacetCategory>();

	for (final FacetCategory facettedCategory : facettedCategories) {
	    if (facettedCategory.getScopes().isEmpty()
		    || facettedCategory.getScopes().contains(searchScope)) {
		filteredFacetCategories.add(facettedCategory);
	    }
	}
	return Collections.unmodifiableList(filteredFacetCategories);
    }

    @Override
    public List<String> getAvailableSortFields() {
	return Collections.unmodifiableList(sorterFields);
    }

    @Override
    public List<SmaugSorter> getAvailableSorters(final String sortField)
	    throws SearchReferenceException {
	final List<SmaugSorter> sorterList = sorterFieldMap.get(sortField);
	Assert.notNull(sorterList, "Invalid sort field : " + sortField);
	return sorterList;
    }

    /**
     * Sets the available facet categories
     * 
     * @param facetCategories
     *            Categories
     */
    public void setFacetCategories(final FacetCategories facetCategories) {
	this.facetCategories = facetCategories;
    }

    /**
     * Sets the available sorters
     * 
     * @param sorters
     *            Sorters
     */
    public void setSorters(final Sorters sorters) {
	this.sorters = sorters;
    }

    /**
     * Initializes this instance (required before starting using it)
     */
    public void init() {
	// Initializing facet categories cache
	for (final com.videopolis.smaug.common.model.impl.xml.FacetCategory xmlFacetCategory : facetCategories
		.getFacetCategory()) {
	    final FacetCategory facetCategory = FacetCategoryFactory
		    .createFacetCategoryFromXml(xmlFacetCategory);

	    // Registering facet category in our global cache
	    facetCategoryCache.put(facetCategory.getCategoryCode(),
		    facetCategory);
	    facetCategoryUrlCache
		    .put(facetCategory.getUrlCode(), facetCategory);

	    // Current category is a facettedCategory only if specified
	    if (!xmlFacetCategory.isFilterOnly()) {
		facettedCategories.add(facetCategory);
	    }
	}

	// Initializing sorter definition from XML
	for (final com.videopolis.smaug.common.model.impl.xml.Sorter xmlSorter : sorters
		.getSorter()) {
	    final SmaugSorter sorter = FacetCategoryFactory
		    .createSorterFromXml(xmlSorter);

	    List<SmaugSorter> sorterList = sorterFieldMap.get(sorter.getSortField());
	    if (sorterList == null) {
		sorterList = new LinkedList<SmaugSorter>();
		sorterFieldMap.put(sorter.getSortField(), sorterList);
	    }
	    sorterList.add(sorter);
	    if (!sorterFields.contains(sorter.getSortField())) {
		sorterFields.add(sorter.getSortField());
	    }
	    // Filling the URL code index map
	    sorterUrlMap.put(sorter.getUrlCode(), sorter);
	    final Integer defaultIndex = xmlSorter.getDefaultIndex();
	    if (defaultIndex != null) {
		if (defaultIndex > defaultSorters.size()) {
		    defaultSorters.add(sorter);
		} else {
		    defaultSorters.add(defaultIndex, sorter);
		}
	    }
	}
    }

    /**
     * Build a cache key for a facet category/code couple
     * 
     * @param categoryCode
     *            Category code
     * @param facetCode
     *            Facet code
     * @return Cache key
     */
    private String buildFacetCacheKey(final String categoryCode,
	    final String facetCode) {
	return categoryCode + "/" + facetCode;
    }

    @Override
    public SmaugSorter getSorterFromUrlCode(final String urlCode)
	    throws SearchReferenceException {
	final SmaugSorter sorter = sorterUrlMap.get(urlCode);
	Assert.notNull(sorter, "No sorter defined for URL code " + urlCode);
	return sorter;
    }

    @Override
    public List<SmaugSorter> getDefaultSorters() {
	return defaultSorters;
    }

    /**
     * Sets the format of the range codes which will used to create instances of
     * {@link FacetRange}
     * 
     * @param rangeFormat
     *            Range format
     */
    public void setRangeFormat(final String rangeFormat) {
	this.rangeFormat = rangeFormat;
    }

    @Override
    public SmaugSorter getSorter(final String sortField, final boolean isAscending)
	    throws SearchReferenceException {
	final List<SmaugSorter> sorterList = getAvailableSorters(sortField);
	for (final SmaugSorter s : sorterList) {
	    if (s.isAscending() == isAscending) {
		return s;
	    }
	}
	throw new SearchReferenceException("No sorter defined for " + sortField
		+ "," + (isAscending ? "ascending" : "descending"));
    }
}
