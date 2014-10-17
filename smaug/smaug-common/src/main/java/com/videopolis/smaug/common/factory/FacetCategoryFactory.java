package com.videopolis.smaug.common.factory;

import java.util.ArrayList;
import java.util.List;

import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.GroupingStrategy;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.SmaugSorter;
import com.videopolis.smaug.common.model.impl.FacetCategoryImpl;
import com.videopolis.smaug.common.model.impl.SmaugSorterImpl;

public final class FacetCategoryFactory {

    private FacetCategoryFactory() {
    }

    public static FacetCategory createFacetCategoryFromXml(
	    final com.videopolis.smaug.common.model.impl.xml.FacetCategory xmlFacetCategory) {

	// Transferring XML bean to our model
	final FacetCategoryImpl facetCategory = new FacetCategoryImpl();
	facetCategory.setCategoryCode(xmlFacetCategory.getCategoryCode());
	final GroupingStrategy gs = GroupingStrategy.valueOf(xmlFacetCategory
		.getGroupingStrategy());
	facetCategory.setGroupingStrategy(gs);
	facetCategory.setCachingFacets(xmlFacetCategory.isFacetCache());
	facetCategory.setUrlCode(xmlFacetCategory.getUrlCode());
	facetCategory.setUrlSeoCategory(xmlFacetCategory.isUrlSeoTranslation());
	if (xmlFacetCategory.isRange() == null) {
	    xmlFacetCategory.setRange(false);
	} else {
	    facetCategory.setRange(xmlFacetCategory.isRange());
	}
	if (xmlFacetCategory.getScopes() != null) {
	    final List<SearchScope> scopes = new ArrayList<SearchScope>(
		    xmlFacetCategory.getScopes().getScope().size());
	    for (final String scope : xmlFacetCategory.getScopes().getScope()) {
		scopes.add(SearchScope.valueOf(scope));
	    }
	    facetCategory.setScopes(scopes);
	}

	return facetCategory;
    }

    public static SmaugSorter createSorterFromXml(
	    final com.videopolis.smaug.common.model.impl.xml.Sorter xmlSorter) {

	return new SmaugSorterImpl(xmlSorter.getFieldName(),
		xmlSorter.isAscending() ? Sorter.Order.ASCENDING
			: Sorter.Order.DESCENDING, xmlSorter.getUrlCode(),
		xmlSorter.isSticky() == null ? false : xmlSorter.isSticky());
    }
}
