package com.videopolis.smaug.model.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.SearchSettings;

/**
 * Default immutable implementation of {@link SearchSettings}.
 * 
 * @author julien
 * @author Shoun Ichida
 * 
 */
public class SearchSettingsImpl implements SearchSettings {

    /** The requested returned type of the request. */
    private String returnedType;
    /** The search scope. */
    private SearchScope searchScope;
    /** The search method. */
    private SearchMethod searchMethod;
    /** List of sorters to apply to the request. */
    private List<Sorter> sorters;
    /** List of filters to apply to the request. */
    private Collection<Facet> filters;
    /** List of facet categories for request. */
    private Collection<FacetCategory> facettedCategories;
    /** The locale of the request. */
    private Locale locale;

    @Override
    public SearchMethod getSearchMethod() {
	return searchMethod;
    }

    @Override
    public SearchScope getSearchScope() {
	return searchScope;
    }

    @Override
    public List<Sorter> getSorters() {
	return sorters;
    }

    @Override
    public Collection<Facet> getFilters() {
	return filters;
    }

    @Override
    public Collection<FacetCategory> getFacettedCategories() {
	return facettedCategories;
    }

    @Override
    public Locale getLocale() {
	return locale;
    }

    @Override
    public String getReturnedType() {
	return returnedType;
    }

    /**
     * @param returnedType
     *            the returnedType to set
     */
    public void setReturnedType(final String returnedType) {
	this.returnedType = returnedType;
    }

    /**
     * @param searchScope
     *            the searchScope to set
     */
    public void setSearchScope(final SearchScope searchScope) {
	this.searchScope = searchScope;
    }

    /**
     * @param searchMethod
     *            the searchMethod to set
     */
    public void setSearchMethod(final SearchMethod searchMethod) {
	this.searchMethod = searchMethod;
    }

    /**
     * @param sorters
     *            the sorters to set
     */
    public void setSorters(final List<Sorter> sorters) {
	this.sorters = sorters;
    }

    /**
     * @param filters
     *            the filters to set
     */
    public void setFilters(final Collection<Facet> filters) {
	this.filters = filters;
    }

    /**
     * @param facettedCategories
     *            the facettedCategories to set
     */
    public void setFacettedCategories(
	    final Collection<FacetCategory> facettedCategories) {
	this.facettedCategories = facettedCategories;
    }

    /**
     * @param locale
     *            the locale to set
     */
    public void setLocale(final Locale locale) {
	this.locale = locale;
    }

}
