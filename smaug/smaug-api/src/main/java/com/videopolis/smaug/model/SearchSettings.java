package com.videopolis.smaug.model;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * <p>
 * The settings of a search query.
 * </p>
 * <p>
 * These settings are used to configure how the search will be performed.
 * </p>
 * 
 * @author julien
 * @author refactored by christophe for listview
 */
public interface SearchSettings {

    /**
     * Returns the categories for which we should return facet information.
     * 
     * @return a collection of {@link FacetCategory} instances, informing about
     *         the fields for which facet counts should be returned
     */
    Collection<FacetCategory> getFacettedCategories();

    /**
     * Returns the filters to apply when searching for elements.
     * 
     * @return a collection of {@link Facet} which will filter the search
     *         elements. Filters need to be combined depending of their
     *         corresponding {@link FacetCategory}
     */
    Collection<Facet> getFilters();

    /**
     * Informs about the locale to use when searching. If defined, the search
     * should only be performed on the specified locale, if <code>null</code>
     * then the search should be performed for all locales.
     * 
     * @return the {@link Locale} to use for the search
     */
    Locale getLocale();

    /**
     * Returns the expected CAL type of the elements resulting from the search
     * request.
     * 
     * @return the expected CAL type of objects returned by the search query
     */
    String getReturnedType();

    /**
     * Returns the method of the search to perform. The method informs about how
     * the search should be done.
     * 
     * @return the {@link SearchMethod}
     */
    SearchMethod getSearchMethod();

    /**
     * Returns the scope of the search to perform. The scope informs about how
     * the search should be done and refers to discrete enumerated values.
     * 
     * @return the {@link SearchScope}
     */
    SearchScope getSearchScope();

    /**
     * Returns the sorting parameters of the query
     * 
     * @return a list of {@link Sorter} to use to sort search results
     */
    List<Sorter> getSorters();
}
