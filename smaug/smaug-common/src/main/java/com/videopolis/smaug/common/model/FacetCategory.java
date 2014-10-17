package com.videopolis.smaug.common.model;

import java.util.List;

/**
 * A facet category defines a category for which we want to group facets. For
 * example with the tvtrip website : amenities are one category, stars are
 * another.<br>
 * Within a category, all distinct values will generate one facet.<br>
 * 
 * 
 * @author Christophe Fondacci
 * 
 */
public interface FacetCategory {

    /**
     * Informs about the code of this category.
     * 
     * @return the category code
     */
    String getCategoryCode();

    /**
     * Retrieves the grouping strategy for this facet category. This strategy
     * allows to control the way filters will be combined within a same
     * strategy.
     * 
     * @return the {@link GroupingStrategy}
     * @see GroupingStrategy
     */
    GroupingStrategy getGroupingStrategy();

    /**
     * Informs whether facets from this category should be cached or not.
     * 
     * @return <code>true</code> when a {@link Facet} from this
     *         {@link FacetCategory} should be cached, <code>false</code> if it
     *         should not be cached
     */
    boolean isCachingFacets();

    /**
     * Retrieves the URL code of this facet which is a compact version of the
     * facet code.
     * 
     * @return the facet URL code
     */
    String getUrlCode();

    /**
     * Indicates whether this category appears in the SEO facet section of the
     * URL.
     * 
     * @return <code>true</code> when facets of this category should appear in
     *         the URL SEO part, else <code>false</code>
     */
    boolean isUrlSeoCategory();

    /**
     * Indicates whether this category is a range (a special facet for which the
     * value is composed of two numbers, a lower and an higher bounds)
     * 
     * @return {@code true} when this facet category is a range category.
     */
    boolean isRange();

    /**
     * <p>
     * Returns the search scopes for which this category is applicable.
     * </p>
     * <p>
     * <strong>Warning</strong>: This method will return an empty list if the
     * category is available for <strong>all</strong> the scopes
     * </p>
     * 
     * @return The list of applicable scopes, or an empty list for
     *         <strong>all</strong> the scopes.
     */
    List<SearchScope> getScopes();
}
