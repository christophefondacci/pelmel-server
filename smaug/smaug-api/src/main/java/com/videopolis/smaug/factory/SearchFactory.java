package com.videopolis.smaug.factory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SuggestScope;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchTextSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.impl.SearchSettingsImpl;
import com.videopolis.smaug.model.impl.SearchTextSettingsImpl;
import com.videopolis.smaug.model.impl.SearchWindowImpl;

/**
 * Factory class which is able to produces instances of search model objects
 * like {@link SearchSettings} or {@link SearchWindow}
 * 
 * @author julien
 * @author Shoun Ichida
 * 
 */
public final class SearchFactory {

    private SearchFactory() {
    }

    /**
     * Builds an instance of {@link SearchSettings} used to build search queries
     * 
     * @param returnedType
     *            Types of the objects that the search must return
     * @param scope
     *            The {@link SearchScope}
     * @param filters
     *            Filters to apply to the query
     * @param facets
     *            Facets
     * @param sorters
     *            Rules for sorting the results
     * @param locale
     *            The locale of the request
     * @return new {@link SearchSettings} instance
     */
    public static SearchSettings createSearchSettings(
	    final String requestedType, final SearchScope scope,
	    final Collection<Facet> filters,
	    final Collection<FacetCategory> facets, final List<Sorter> sorters,
	    final Locale locale) {
	final SearchSettingsImpl searchSettings = new SearchSettingsImpl();
	searchSettings.setReturnedType(requestedType);
	searchSettings.setSearchScope(scope);
	searchSettings.setSearchMethod(SearchMethod.CITIES_WITHOUT_SHADOW);
	searchSettings.setFilters(filters);
	searchSettings.setFacettedCategories(facets);
	searchSettings.setSorters(sorters);
	searchSettings.setLocale(locale);
	return searchSettings;
    }

    /**
     * Builds an instance of {@link SearchSettings} used to build search queries
     * 
     * @param requestedType
     *            Types of the objects that the search must return
     * @param scope
     *            The {@link SearchScope}
     * @param searchMethod
     *            The {@link SearchMethod}
     * @param filters
     *            Filters to apply to the query
     * @param facets
     *            Facets
     * @param sorters
     *            Rules for sorting the results
     * @param locale
     *            The locale of the request
     * @return new {@link SearchSettings} instance
     */
    public static SearchSettings createSearchSettings(
	    final String requestedType, final SearchScope scope,
	    final SearchMethod searchMethod, final Collection<Facet> filters,
	    final Collection<FacetCategory> facets, final List<Sorter> sorters,
	    final Locale locale) {
	final SearchSettingsImpl searchSettings = new SearchSettingsImpl();
	searchSettings.setReturnedType(requestedType);
	searchSettings.setSearchScope(scope);
	searchSettings.setSearchMethod(searchMethod);
	searchSettings.setFilters(filters);
	searchSettings.setFacettedCategories(facets);
	searchSettings.setSorters(sorters);
	searchSettings.setLocale(locale);
	return searchSettings;
    }

    /**
     * Creates a simple instance of search settings by only specifying its type
     * and scope.
     * 
     * @param requestedType
     *            the CAL-type of elements we search for
     * @param scope
     *            the {@link SearchScope} of the search to perform
     * @return a new {@link SearchSettings} instance
     */
    @SuppressWarnings("unchecked")
    public static SearchSettings createSearchSettings(
	    final String requestedType, final SearchScope scope) {
	return createSearchSettings(requestedType, scope,
		Collections.EMPTY_LIST, Collections.EMPTY_LIST,
		Collections.EMPTY_LIST, null);
    }

    /**
     * Creates a simple instance of search settings by only specifying its type,
     * scope and method.
     * 
     * @param requestedType
     *            the CAL-type of elements we search for
     * @param scope
     *            the {@link SearchScope} of the search to perform
     * @param searchMethod
     *            the {@link SearchMethod} of the search to perform
     * @return a new {@link SearchSettings} instance
     */
    @SuppressWarnings("unchecked")
    public static SearchSettings createSearchSettings(
	    final String requestedType, final SearchScope scope,
	    final SearchMethod searchMethod) {
	return createSearchSettings(requestedType, scope, searchMethod,
		Collections.EMPTY_LIST, Collections.EMPTY_LIST,
		Collections.EMPTY_LIST, null);
    }

    /**
     * Builds an instance of {@link SearchTextSettings} used to build search
     * queries
     * 
     * @param returnedTypes
     *            Types of the objects that the search must return
     * @param sorters
     *            Rules for sorting the results
     * @return new {@link SearchTextSettings} instance
     */
    public static SearchTextSettings createSuggestSettings(
	    final List<SuggestScope> suggestScope, final List<Sorter> sorters) {
	final SearchTextSettingsImpl searchSettings = new SearchTextSettingsImpl();
	searchSettings.setSuggestScope(suggestScope);
	searchSettings.setSorters(sorters);
	return searchSettings;
    }

    /**
     * Builds an instance of {@link SearchWindow} used to paginate search
     * results
     * 
     * @param itemsPerPage
     *            Number of items in one result page
     * @param pageNumber
     *            Desired result page number (starting from 0)
     * @return new {@link SearchWindow} instance
     */
    public static SearchWindow createSearchWindow(final int itemsPerPage,
	    final int pageNumber) {
	return new SearchWindowImpl(itemsPerPage, pageNumber);
    }
}
