package com.videopolis.apis.concurrent.impl;

import com.videopolis.apis.concurrent.base.AbstractSearchApisTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisSearchHelper;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;

/**
 * An {@link ApisTask} which invokes a search all action and a CAL item fetch of
 * the returned elements.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SearchAllTask extends AbstractSearchApisTask {

	/**
	 * Creates a new {@link SearchAllTask} with the specified information
	 * 
	 * @param service
	 *            CAL service to be called
	 * @param context
	 *            {@link ApisContext} in use
	 * @param requestedType
	 *            requested CAL item type
	 * @param centralPoint
	 *            central geo-localized point for the nearby search
	 * @param radius
	 *            radius of the search
	 * @param pagination
	 *            pagination information
	 */
	public SearchAllTask(final CalService service, final ApisContext context,
			final String requestedType, final PaginationSettings pagination) {
		super(service, context, requestedType, pagination);
	}

	@Override
	public ItemsResponse doExecute(
			final TaskExecutionContext taskExecutionContext)
			throws TaskExecutionException, InterruptedException {

		final SearchSettings settings = createSearchSettings();
		// Building window
		final SearchWindow window = createSearchWindow();
		// Searching
		final SearchResponse response = getContext().getSearchService()
				.searchAll(settings, window);
		// Retrieving the CAL Item key collection
		if (response != null) {
			final ItemKey[] searchedKeys = ApisSearchHelper
					.unwrapItemKeys(response.getItems());
			final ApiMutableResponse apisResponse = getContext()
					.getApiResponse();
			// Filling facet information if available
			try {
				ApisSearchHelper.fillFacettingInformation(apisResponse,
						response);
			} catch (final ApisException e) {
				throw new TaskExecutionException(
						"Unable to set facetting information: "
								+ e.getMessage(), e);
			}
			// Extracting alias to fill pagination info for this alias
			String alias = null;
			final ApisCriterion criterion = getCriterion();
			if (criterion instanceof Aliasable<?>) {
				alias = ((Aliasable<?>) criterion).getAlias();
			}
			// Filling pagination information
			ApisSearchHelper.fillPaginationInformation(apisResponse, response,
					getRequestedType(), alias);

			// We're using a nested CAL task to fetch content
			if (searchedKeys.length > 0) {
				final GetCalItemsTask itemsTask = new GetCalItemsTask(
						getService(), getContext(), searchedKeys);
				return itemsTask.execute(taskExecutionContext);
			} else {
				return new ItemsResponseImpl();
			}
		} else {
			return new ItemsResponseImpl();
		}
	}

	@Override
	public String toString() {
		return "searchAllTask;" + super.toString();
	}
}
