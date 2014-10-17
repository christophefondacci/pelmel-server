package com.videopolis.smaug.common.model.impl;

import java.util.Collections;
import java.util.List;

import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.GroupingStrategy;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * Default {@link FacetCategory} bean implementation
 * 
 * @author Christophe Fondacci
 * 
 */
public class FacetCategoryImpl implements FacetCategory {

    private String categoryCode;
    private GroupingStrategy groupingStrategy;
    private boolean facetCaching;
    private String urlCode;
    private boolean urlSeoCategory;
    private boolean range;
    private List<SearchScope> scopes;

    /**
     * Builds a new {@link FacetCategory} implementation
     */
    public FacetCategoryImpl() {
	// Initializing default grouping strategy
	groupingStrategy = GroupingStrategy.AND;
    }

    @Override
    public String getCategoryCode() {
	return categoryCode;
    }

    @Override
    public GroupingStrategy getGroupingStrategy() {
	return groupingStrategy;
    }

    public void setCategoryCode(final String categoryCode) {
	this.categoryCode = categoryCode;
    }

    public void setGroupingStrategy(final GroupingStrategy groupingStrategy) {
	this.groupingStrategy = groupingStrategy;
    }

    /**
     * FacetCategory is used as a map key
     * 
     * @author mehdi BEN HAJ ABBES
     * @since 07 Jan 2011
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ (categoryCode == null ? 0 : categoryCode.hashCode());
	return result;
    }

    /**
     * FacetCategory is used as a map key in
     * 
     * @author mehdi BEN HAJ ABBES
     * @since 07 Jan 2011
     */
    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final FacetCategoryImpl other = (FacetCategoryImpl) obj;
	if (categoryCode == null) {
	    if (other.categoryCode != null) {
		return false;
	    }
	} else if (!categoryCode.equals(other.categoryCode)) {
	    return false;
	}
	return true;
    }

    /**
     * more expressive while testing
     * 
     * @author mehdi BEN HAJ ABBES
     * @since 07 Jan 2011
     */
    @Override
    public String toString() {
	return "[categoryCode=" + categoryCode + "]";
    }

    @Override
    public boolean isCachingFacets() {
	return facetCaching;
    }

    public void setCachingFacets(final boolean facetCaching) {
	this.facetCaching = facetCaching;
    }

    @Override
    public String getUrlCode() {
	return urlCode;
    }

    public void setUrlCode(final String urlCode) {
	this.urlCode = urlCode;
    }

    @Override
    public boolean isUrlSeoCategory() {
	return urlSeoCategory;
    }

    public void setUrlSeoCategory(final boolean isUrlSeoCategory) {
	urlSeoCategory = isUrlSeoCategory;
    }

    @Override
    public boolean isRange() {
	return range;
    }

    /**
     * @param range
     *            Whether or not this facet category is a range
     */
    public void setRange(final boolean range) {
	this.range = range;
    }

    public void setScopes(final List<SearchScope> scopes) {
	this.scopes = scopes;
    }

    @Override
    public List<SearchScope> getScopes() {
	if (scopes == null) {
	    return Collections.emptyList();
	} else {
	    return scopes;
	}
    }
}
