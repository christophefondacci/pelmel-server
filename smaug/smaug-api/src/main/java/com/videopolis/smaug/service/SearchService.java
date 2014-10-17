package com.videopolis.smaug.service;

import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.smaug.exception.SearchException;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchTextResponse;
import com.videopolis.smaug.model.SearchTextSettings;
import com.videopolis.smaug.model.SearchWindow;

/**
 * <p>
 * The main entry point for searching stuff.
 * </p>
 * <p>
 * Each method provides a specific type of search. However, the way to set up a
 * search query is common to all methods using {@link SearchSettings} and
 * {@link SearchWindow} objects.
 * </p>
 * <p>
 * Search methods will throw an {@link SearchException} if a technical problem
 * prevented the search to be done successfully.
 * </p>
 * </p>
 * 
 * @see SearchResponse
 * @see SearchSettings
 * @see SearchWindow
 * @author julien
 * 
 */
public interface SearchService {

	/**
	 * Searches for items which are localized around a point
	 * 
	 * @param point
	 *            Point to search around
	 * @param radius
	 *            Radius of the search (expressed in km)
	 * @param settings
	 *            Search query settings
	 * @param window
	 *            Pagination of results
	 * @return Search response
	 * @throws SearchException
	 *             If the search failed
	 */
	SearchResponse searchNear(Localized point, double radius,
			SearchSettings settings, SearchWindow window);

	/**
	 * Searches for items which have a child relationship with the specified
	 * {@link ItemKey}.
	 * 
	 * @param parent
	 *            parent {@link ItemKey} to search into
	 * @param settings
	 *            {@link SearchSettings} for this query (facetting, filters,
	 *            etc.)
	 * @param window
	 *            the {@link SearchWindow} of the search (pagination, etc.)
	 * @return the {@link SearchResponse}
	 */
	SearchResponse searchIn(ItemKey parent, SearchSettings settings,
			SearchWindow window);

	/**
	 * Searches for elements whose names match the specified text to search.
	 * 
	 * @param textToSearch
	 *            The text to search for
	 * @param settings
	 *            The search settings
	 * @param window
	 *            The window settings
	 * @return the {@link SearchTextResponse}
	 */
	SearchTextResponse searchText(String textToSearch,
			SearchTextSettings settings, SearchWindow window);

	/**
	 * Searches for elements by type without parent id nor geo localization.
	 * 
	 * @param settings
	 *            The search settings
	 * @param window
	 *            The window settings
	 * @return the {@link SearchTextResponse}
	 */
	SearchResponse searchAll(SearchSettings settings, SearchWindow window);
}
