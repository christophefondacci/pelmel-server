package com.nextep.proto.apis.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.services.EventManagementService;
import com.nextep.users.model.User;
import com.videopolis.apis.cals.impl.PaginationInfoAdapter;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.impl.ExpirableItemKeyImpl;
import com.videopolis.smaug.common.model.SearchMethod;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.factory.SearchFactory;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;
import com.videopolis.smaug.model.impl.SearchWindowImpl;

public class ApisExpirableLikesCustomAdapter implements ApisCustomAdapter {

	private static final Log LOGGER = LogFactory
			.getLog(ApisExpirableLikesCustomAdapter.class);
	public static final String APIS_ALIAS_LIKES = "expirableLikes";
	private EventManagementService eventManagementService;
	private String alias;
	private int maxRelatedElements;
	private int offset;

	public ApisExpirableLikesCustomAdapter(
			EventManagementService eventManagementService, String alias,
			int maxRelatedElements, int offset) {
		this.eventManagementService = eventManagementService;
		this.alias = alias;
		this.maxRelatedElements = maxRelatedElements;
		this.offset = offset;
	}

	@Override
	public List<ItemKey> adapt(ApisContext context, CalmObject... parents) {
		if (parents.length == 1) {
			final CalmObject parent = parents[0];
			if (parent instanceof EventSeries) {
				final EventSeries series = (EventSeries) parent;

				String timezoneId = null;
				// Request for getting series location
				try {
					ApisRequest request = ApisFactory.createRequest(
							series.getLocationKey().getType()).uniqueKey(
							series.getLocationKey().getId());
					ApiResponse response = context.getApiService().execute(
							request, context.getCalContext());
					GeographicItem item = (GeographicItem) response
							.getUniqueElement();
					if (item instanceof Place) {
						timezoneId = ((Place) item).getCity().getTimezoneId();
					} else if (item instanceof City) {
						timezoneId = ((City) item).getTimezoneId();
					}
				} catch (ApisException e) {
					LOGGER.error("Cannot get city for event timezone");
				}

				final Date nextEnd = eventManagementService.computeNext(series,
						timezoneId, false);

				// Default to series unique key
				ItemKey likeExpirableKey = series.getKey();

				// We may not have an next end date...
				if (nextEnd != null) {
					likeExpirableKey = new ExpirableItemKeyImpl(
							series.getKey(), nextEnd.getTime());
				}

				final SearchSettings settings = SearchFactory
						.createSearchSettings(User.CAL_TYPE,
								SearchScope.CHILDREN,
								SearchMethod.CITIES_WITHOUT_SHADOW,
								Collections.EMPTY_LIST, Collections.EMPTY_LIST,
								Collections.EMPTY_LIST, Locale.getDefault());
				final SearchWindow window = new SearchWindowImpl(
						maxRelatedElements, offset);
				final SearchResponse response = context.getSearchService()
						.searchIn(likeExpirableKey, settings, window);

				context.getApiResponse().setAliasedPaginationInfo(alias,
						new PaginationInfoAdapter(response.getSearchWindow()));

				final List<ItemKey> itemKeyResponse = new ArrayList<ItemKey>();
				for (SearchItem item : response.getItems()) {
					itemKeyResponse.add(item.getKey());
				}
				return itemKeyResponse;
			}
		}
		//
		return Collections.emptyList();
	}
}
