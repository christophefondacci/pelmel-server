package com.nextep.smaug.solr.model.impl;

import java.util.List;

import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchTextResponse;
import com.videopolis.smaug.model.SearchTextSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.SearchWindowResponse;

/**
 * Implementation of the {@link SearchTextResponse} interface.
 * 
 */
public class SuggestResponseImpl implements SearchTextResponse {
	/** List of returned items by the request. */
	private final List<SearchItem> items;
	/** Settings applied to the request. */
	private final SearchTextSettings settings;
	/** Window settings for the response. */
	private SearchWindowResponse window;

	/**
	 * Ctor
	 * 
	 * @param items
	 *            The items to return
	 * @param settings
	 *            The settings of the request
	 * @param window
	 *            The window settings of the request
	 * @param itemCount
	 *            The number of founded items
	 */
	public SuggestResponseImpl(List<SearchItem> items,
			SearchTextSettings settings, SearchWindow window,
			final int itemCount) {
		this.items = items;
		this.settings = settings;

		if (window != null) {
			this.window = new SearchWindowResponseImpl(window.getPageNumber(),
					window.getItemsPerPage(), itemCount);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.videopolis.smaug.model.SuggestResponse#getItems()
	 */
	@Override
	public List<SearchItem> getItems() {
		return this.items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.videopolis.smaug.model.SuggestResponse#getSearchWindow()
	 */
	@Override
	public SearchWindowResponse getSearchWindow() {
		return this.window;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.videopolis.smaug.model.SuggestResponse#getSuggestSettings()
	 */
	@Override
	public SearchTextSettings getSuggestSettings() {
		return this.settings;
	}

	public void setSearchWindow(final SearchWindowResponse window) {
		this.window = window;
	}
}
