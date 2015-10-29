package com.nextep.smaug.solr.model.impl;

import java.util.List;
import java.util.Map;

import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.model.FacetCount;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.SearchWindowResponse;

/**
 * Implementation of {@link SearchResponse} interface
 * 
 * @author Shoun Ichida
 * @Since janv. 2011
 * 
 */
public class SearchResponseImpl implements SearchResponse {
	/** Map of facet count. */
	private final Map<FacetCategory, List<FacetCount>> facetsMap;
	/** Response items. */
	private final List<? extends SearchItem> items;
	/** Search settings. */
	private final SearchSettings settings;
	/** Reponse window. */
	private SearchWindowResponse window;

	/**
	 * Ctor
	 * 
	 * @param items
	 *            The items of the response
	 * @param facetsMap
	 *            The facet count map
	 * @param settings
	 *            The setting of the request
	 * @param window
	 *            The window for pagination
	 * @param itemCount
	 *            The number of found items
	 */
	public SearchResponseImpl(List<? extends SearchItem> items, Map<FacetCategory, List<FacetCount>> facetsMap,
			SearchSettings settings, SearchWindow window, final int itemCount) {
		this.facetsMap = facetsMap;
		this.items = items;
		this.settings = settings;

		if (window != null) {
			this.window = new SearchWindowResponseImpl(window.getPageNumber(), window.getItemsPerPage(), itemCount);
		}
	}

	@Override
	public List<? extends SearchItem> getItems() {
		return this.items;
	}

	@Override
	public SearchSettings getSearchSettings() {
		return this.settings;
	}

	@Override
	public SearchWindowResponse getSearchWindow() {
		return this.window;
	}

	@Override
	public Map<FacetCategory, List<FacetCount>> getFacetsMap() {
		return this.facetsMap;
	}

}
