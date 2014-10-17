package com.videopolis.apis.concurrent.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.videopolis.apis.concurrent.base.AbstractSearchApisTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisSearchHelper;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.model.impl.SearchStatisticImpl;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;

/**
 * An {@link ApisTask} which invokes a nearby search action and a CAL item fetch
 * of the returned elements.
 *
 * @author Christophe Fondacci
 *
 */
public class SearchNearbyTask extends AbstractSearchApisTask {

    private final Localized centralPoint;
    private final double radius;

    /**
     * Creates a new {@link SearchNearbyTask} with the specified information
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
    public SearchNearbyTask(final CalService service,
	    final ApisContext context, final String requestedType,
	    final Localized centralPoint, final double radius,
	    final PaginationSettings pagination) {
	super(service, context, requestedType, pagination);
	this.centralPoint = centralPoint;
	this.radius = radius;
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
		.searchNear(centralPoint, radius, settings, window);
	final Collection<ItemKey> nearestKeys = new ArrayList<ItemKey>();
	final ApiMutableResponse apisResponse = getContext().getApiResponse();
	// Filling distance statistics
	for (final SearchItem item : response.getItems()) {
	    final ItemKey itemKey = item.getKey();
	    nearestKeys.add(item.getKey());
	    final SearchStatistic distStat = new SearchStatisticImpl(
		    SearchStatistic.DISTANCE,
		    item.getInfo(SearchItem.GEO_DISTANCE));
	    apisResponse.setStatistic(itemKey, distStat);
	}
	// Filling facet information if available
	try {
	    ApisSearchHelper.fillFacettingInformation(apisResponse, response);
	} catch (final ApisException e) {
	    throw new TaskExecutionException(
		    "Unable to set facetting information: " + e.getMessage(), e);
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

	// WORK IN PROGRESS / not working at the moment
	// Building proxy response so that getItems could be done in parallel
	// with any inner WITH criterion clause
	// try {
	// return new ApiProxiedResponseImpl(nearestKeys);
	// } catch (ApisException e) {
	// throw new TaskExecutionException(e);
	// }
	// END OF WORK IN PROGRESS

	// We're using a nested CAL task to fetch content
	final GetCalItemsTask itemsTask = new GetCalItemsTask(getService(),
		getContext(), nearestKeys.toArray(new ItemKey[nearestKeys
			.size()]));
	return itemsTask.execute(taskExecutionContext);
    }

    @Override
    public String toString() {
	return "searchNearbyTask;"
		+ super.toString()
		+ ";"
		+ (centralPoint == null ? "nullPoint" : centralPoint
			.getLongitude() + ":" + centralPoint.getLatitude())
		+ "," + radius;
    }
}
