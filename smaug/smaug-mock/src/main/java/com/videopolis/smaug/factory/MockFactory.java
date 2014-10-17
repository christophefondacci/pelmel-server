package com.videopolis.smaug.factory;

import java.util.List;

import com.videopolis.smaug.model.Facet;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.SearchWindowResponse;

/**
 * A factory used to create mock search objects
 * 
 * @author julien
 * 
 */
public final class MockFactory {

    private MockFactory() {
    }

    /**
     * Creates an instance of {@link SearchWindowResponse}
     * 
     * @param window
     *            Initial search window
     * @param itemCount
     *            Item count
     * @return Search window response
     */
    public static SearchWindowResponse createSearchWindowResponse(
	    final SearchWindow window, final int itemCount) {
	return new SearchWindowResponse() {

	    @Override
	    public int getPageNumber() {
		return window.getPageNumber();
	    }

	    @Override
	    public int getItemsPerPage() {
		return window.getItemsPerPage();
	    }

	    @Override
	    public int getItemCount() {
		return itemCount;
	    }
	};
    }

    /**
     * Creates an instance of {@link SearchResponse}
     * 
     * @param window
     *            Search window
     * @param settings
     *            Search Settings
     * @param items
     *            Resulting items
     * @param facets
     *            Facets
     * @return SearchResponse
     */
    public static SearchResponse createSearchResponse(
	    final SearchWindowResponse window, final SearchSettings settings,
	    final List<? extends SearchItem> items,
	    final List<? extends Facet> facets) {
	return new SearchResponse() {

	    @Override
	    public SearchWindowResponse getSearchWindow() {
		return window;
	    }

	    @Override
	    public SearchSettings getSearchSettings() {
		return settings;
	    }

	    @Override
	    public List<? extends SearchItem> getItems() {
		return items;
	    }

	    @Override
	    public List<? extends Facet> getFacets() {
		return facets;
	    }
	};
    }
}
