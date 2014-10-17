package com.videopolis.apis.model;

import java.util.Collection;

import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.GroupingStrategy;

/**
 * A search criterion holds all information which is needed to perform a search.
 * 
 * @author Christophe Fondacci
 */
public interface SearchCriterion extends WithCriterion {

    /**
     * Retrieves the search scope of this criterion
     * 
     * @return
     */
    // SearchScope getSearchScope();

    /**
     * Retrieves the categories which needs to be facetted when the search is
     * performed.
     * 
     * @return a collection of {@link FacetCategory} which needs to be facetted
     *         or an empty list when no facetting is needed
     */
    Collection<FacetCategory> getFacettedCategories();

    /**
     * Retrieves the filters to apply when performing this search. These filters
     * will be applied as constraints (restrictions) and will be combined
     * depending on the {@link GroupingStrategy} of the parent
     * {@link FacetCategory} of each facet filter.
     * 
     * @return a collection of {@link Facet} to use for narrowing the search
     */
    Collection<Facet> getFilters();

    /**
     * Retrieves the radius to use when performing a nearby search. This
     * information will only be used when the search method is set to
     * {@link SearchMethod#NEARBY}
     * 
     * @return the search radius, in miles
     */
    double getRadius();

    /**
     * Informs about the kind of search which should be performed. This
     * information will impact the way APIS will query the SMAUG search
     * services.
     * 
     * @return a {@link SearchMethod} enumeration.
     */
    SearchMethod getSearchMethod();

    /**
     * Adds the definition of the {@link FacetCategory} which should return
     * facet information.
     * 
     * @param facettedCategories
     *            {@link FacetCategory} for which this search should compute
     *            facet counts
     * @return the search criterion
     */
    SearchCriterion facettedBy(Collection<FacetCategory> facettedCategories);

    /**
     * Adds the definition of the filters which will narrow the search.
     * 
     * @param facetFilters
     *            the {@link Facet} selection to constraint the search with
     * @return this search criterion
     */
    SearchCriterion filteredBy(Collection<Facet> facetFilters);
}
