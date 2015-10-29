package com.videopolis.apis.concurrent.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.ApisSearchHelper;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.model.impl.SearchStatisticImpl;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.smaug.common.model.SuggestScope;
import com.videopolis.smaug.factory.SearchFactory;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchTextResponse;
import com.videopolis.smaug.model.SearchTextSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.service.SearchService;

/**
 * This task executes a textual search from the SMAUG text search service.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchTextTask extends AbstractApisTask {

	private final String calType;
	private final List<SuggestScope> scopes;
	private final String searchedText;
	private final ApisContext apisContext;
	private final PaginationSettings pagination;
	private List<Sorter> sorters = Collections.emptyList();

	/**
	 * Creates a new task which can search for text through the SMAUG
	 * {@link SearchService}
	 * 
	 * @param calType
	 *            the calType of the elements to search for
	 * @param scopes
	 *            the suggest scope to use
	 * @param searchedText
	 *            the text to search for
	 * @param apisContext
	 *            the current apisContext
	 */
	public SearchTextTask(final String calType, final List<SuggestScope> scopes, final String searchedText,
			final PaginationSettings pagination, final ApisContext apisContext) {
		this.calType = calType;
		this.scopes = scopes;
		this.searchedText = searchedText;
		this.pagination = pagination;
		this.apisContext = apisContext;
	}

	@Override
	protected ItemsResponse doExecute(final TaskExecutionContext context)
			throws TaskExecutionException, InterruptedException {
		final SearchService searchService = apisContext.getSearchService();
		final SearchTextSettings textSettings = SearchFactory.createSuggestSettings(scopes, sorters);
		final SearchWindow window = SearchFactory.createSearchWindow(pagination.getItemsByPage(),
				pagination.getPageOffset());
		final SearchTextResponse response = searchService.searchText(searchedText, textSettings, window);
		final CalService calService = ApisRegistry.getCalService(calType);
		// Unwrapping items and extracting matched information
		final List<SearchItem> items = response.getItems();
		final List<ItemKey> keys = new ArrayList<ItemKey>(items.size());
		final ApiMutableResponse apiResponse = apisContext.getApiResponse();
		for (final SearchItem item : items) {
			final ItemKey itemKey = item.getKey();
			keys.add(itemKey);
			apiResponse.setStatistic(itemKey,
					new SearchStatisticImpl(SearchStatistic.MATCHED_TEXT, item.getMatchedText()));
		}
		// Extracting alias to fill pagination info for this alias
		String alias = null;
		final ApisCriterion criterion = getCriterion();
		if (criterion instanceof Aliasable<?>) {
			alias = ((Aliasable<?>) criterion).getAlias();
		}
		// Filling pagination information
		final ApiMutableResponse apisResponse = apisContext.getApiResponse();
		ApisSearchHelper.fillPaginationInformation(apisResponse, response, calType, alias);

		// Fetching sub-elements
		final Task<ItemsResponse> getItemsTask = new GetCalItemsTask(calService, apisContext,
				keys.toArray(new ItemKey[keys.size()]));
		return getItemsTask.execute(context);
	}

	public void setSorters(final List<Sorter> sorters) {
		this.sorters = sorters;
	}

	@Override
	public String toString() {
		return "searchTextTask;" + calType + ";" + searchedText;
	}
}
