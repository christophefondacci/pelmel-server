package com.videopolis.smaug.model;

import java.util.List;
import java.util.Map;

import com.videopolis.smaug.common.model.FacetCategory;

/**
 * The result of a search query
 * 
 * @author julien
 * 
 */
public interface SearchResponse extends WindowedResponse {

	/**
	 * Returns the list of items this query found
	 * 
	 * @return Items
	 */
	List<? extends SearchItem> getItems();

	/**
	 * Returns the facets this query found
	 * 
	 * @return a map containing a list of {@link FacetCount} hashed by their
	 *         corresponding {@link FacetCategory}
	 */
	Map<FacetCategory, List<FacetCount>> getFacetsMap();

	/**
	 * Returns the settings used to issue the request
	 * 
	 * @return Settings
	 */
	SearchSettings getSearchSettings();

}
