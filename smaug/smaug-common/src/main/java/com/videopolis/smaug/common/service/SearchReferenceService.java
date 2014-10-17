package com.videopolis.smaug.common.service;

import java.util.List;

import com.videopolis.smaug.common.exception.SearchReferenceException;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SmaugSorter;

/**
 * Provides access to reference beans to use when using search services. This
 * service allows the creation of Facet, FacetCategories, Sorter, etc.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface SearchReferenceService {

    /**
     * Provides the facet instance corresponding to the specified category and
     * code.
     * 
     * @param categoryCode
     *            code of the {@link FacetCategory} of this facet
     * @param facetCode
     *            code of the facet to retrieve
     * @return the {@link Facet} instance
     * @throws SearchReferenceException
     *             whenever the facet definition could not be retrieve.
     *             Depending on implementation, this could typically happen when
     *             the facet category code is not found
     */
    Facet getFacet(String categoryCode, String facetCode)
	    throws SearchReferenceException;

    /**
     * Provides the {@link FacetRange} instance corresponding to the specified
     * category and code.
     * 
     * @param categoryCode
     *            code of the {@link FacetCategory} of this facet
     * @param lowerBound
     *            The lower bound of the range
     * @param higherBound
     *            The upper bound of the range
     * @return The {@link FacetRange} instance
     * @throws SearchReferenceException
     *             whenever the facet definition could not be retrieve.
     *             Depending on implementation, this could typically happen when
     *             the facet category code is not found
     */
    FacetRange getFacetRange(String categoryCode, long lowerBound,
	    long higherBound) throws SearchReferenceException;

    /**
     * Provides the facet instance corresponding to the specified category and
     * code.
     * 
     * @param category
     *            {@link FacetCategory} of this facet
     * @param facetCode
     *            code of the facet to retrieve
     * @return {@link Facet} instance
     * @throws SearchReferenceException
     *             whenever the facet definition could not be retrieve.
     *             Depending on implementation, this could typically happen when
     *             the facet category code is not found
     */
    Facet getFacet(FacetCategory category, String facetCode)
	    throws SearchReferenceException;

    /**
     * Provides the {@link FacetRange} instance corresponding to the specified
     * category and code.
     * 
     * @param category
     *            {@link FacetCategory} of this facet
     * @param lowerBound
     *            The lower bound of the range
     * @param higherBound
     *            The upper bound of the range
     * @return The {@link FacetRange} instance
     * @throws SearchReferenceException
     *             whenever the facet definition could not be retrieve.
     *             Depending on implementation, this could typically happen when
     *             the facet category code is not found
     */
    FacetRange getFacetRange(FacetCategory category, long lowerBound,
	    long higherBound) throws SearchReferenceException;

    /**
     * Provides the facet category bean instance corresponding to the provided
     * category code
     * 
     * @param categoryCode
     *            code of the category to retrieve
     * @return a {@link FacetCategory} instance
     * @throws SearchReferenceException
     *             whenever the provided category code is invalid
     */
    FacetCategory getFacetCategory(String categoryCode)
	    throws SearchReferenceException;

    /**
     * Retrieves the facet category defined for this URL code.
     * 
     * @param urlCode
     *            the URL codification of the {@link FacetCategory} to look for
     * @return the corresponding {@link FacetCategory}
     * @throws SearchReferenceException
     *             when no {@link FacetCategory} is defined with this URL
     *             codification
     */
    FacetCategory getFacetCategoryForUrlCode(String urlCode)
	    throws SearchReferenceException;

    /**
     * Retrieves the exhaustive list of categories which needs to be facetted.
     * Categories are listed in the order in which they should appear in URLs.
     * 
     * @return the collection of available {@link FacetCategory}
     */
    List<FacetCategory> getFacettedCategories();

    /**
     * Retrieves the list of categories which needs to be facetted for a
     * specific search scope. Categories are listed in the order in which they
     * should appear in URLs.
     * 
     * @param searchScope
     *            the SearchScope to get {@link FacetCategory}s for
     * @return the collection of available {@link FacetCategory}
     */
    List<FacetCategory> getFacettedCategories(SearchScope searchScope);

    /**
     * Retrieves the list of available sort fields.
     * 
     * @return a list of available sort fields
     */
    List<String> getAvailableSortFields();

    /**
     * Retrieves the list of available sorters which could be used for a given
     * sort field.
     * 
     * @return a list of all available {@link SmaugSorter} for the specified sort
     *         field
     * @throws SearchReferenceException
     *             when the specified sort field is not a valid sort field
     */
    List<SmaugSorter> getAvailableSorters(String sortField)
	    throws SearchReferenceException;

    /**
     * Retrieves the sorter corresponding to the given URL code.
     * 
     * @param urlCode
     *            URL code of the sorter to retrieve
     * @return the {@link SmaugSorter}
     * @throws SearchReferenceException
     *             whenever the given URL code does not correspond to any
     *             defined sorter
     */
    SmaugSorter getSorterFromUrlCode(String urlCode) throws SearchReferenceException;

    /**
     * Retrieves the specified sorter implementation or throws an exception when
     * no sorter defined for the specified information.
     * 
     * @param sortField
     *            sort field to retrieve sorter for
     * @param isAscending
     *            the sort direction
     * @return the corresponding {@link SmaugSorter} instance
     * @throws SearchReferenceException
     *             when no {@link SmaugSorter} is defined with the provided
     *             information
     */
    SmaugSorter getSorter(String sortField, boolean isAscending)
	    throws SearchReferenceException;

    /**
     * Provides the list of default sorters to use when no sorter is provided.
     * 
     * @return the list of {@link SmaugSorter} to use by default for a search with no
     *         sorter
     */
    List<SmaugSorter> getDefaultSorters();
}
