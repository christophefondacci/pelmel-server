package com.videopolis.apis.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.videopolis.apis.cals.impl.PaginationInfoAdapter;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.impl.FacetInformationImpl;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.model.FacetCount;
import com.videopolis.smaug.model.SearchItem;
import com.videopolis.smaug.model.SearchResponse;
import com.videopolis.smaug.model.SearchWindowResponse;
import com.videopolis.smaug.model.WindowedResponse;

/**
 * This class provides common helper methods which perform some action related
 * to search response processing.
 * 
 * @author Christophe Fondacci
 * 
 */
public final class ApisSearchHelper {

	private ApisSearchHelper() {
	}

	/**
	 * Transfers facetting information from the search response into the APIS
	 * response.
	 * 
	 * @param apiResponse
	 *            the {@link ApiMutableResponse} to fill with facetting info
	 * @param searchResponse
	 *            the {@link SearchResponse} to extract facetting information
	 *            from
	 * @throws ApisException
	 *             whenever we experienced problems while registering facet info
	 */
	public static void fillFacettingInformation(ApiMutableResponse apiResponse, SearchResponse searchResponse)
			throws ApisException {
		final Map<FacetCategory, List<FacetCount>> facetCountsMap = searchResponse.getFacetsMap();
		if (facetCountsMap != null && !facetCountsMap.isEmpty()) {
			final FacetInformationImpl facetInfo = new FacetInformationImpl(facetCountsMap);
			facetInfo.setFacetFilters(searchResponse.getSearchSettings().getFilters());

			apiResponse.setFacetInformation(searchResponse.getSearchSettings().getSearchScope(), facetInfo);
		}
	}

	/**
	 * Transfers pagination information from the search response to the APIS
	 * response.
	 * 
	 * @param apiResponse
	 *            the {@link ApiMutableResponse} to fill with pagination info
	 * @param searchResponse
	 *            the {@link SearchResponse} to extract pagination information
	 *            from
	 * @param itemType
	 *            the CAL item type for which we register pagination
	 * @param alias
	 *            the alias of the criterion which provides this information
	 */
	public static void fillPaginationInformation(ApiMutableResponse apiResponse, WindowedResponse searchResponse,
			String itemType, String alias) {
		final SearchWindowResponse windowResponse = searchResponse.getSearchWindow();
		final PaginationInfo paginationResponse = new PaginationInfoAdapter(windowResponse);
		if (alias == null) {
			apiResponse.setPaginationInfo(itemType, paginationResponse);
		} else {
			apiResponse.setAliasedPaginationInfo(alias, paginationResponse);
		}
	}

	public static void fillPaginationInformation(ApiMutableResponse response, ApisCriterion crit,
			PaginationInfo paginationInfo) {
		if (crit instanceof Aliasable<?>) {
			final String alias = ((Aliasable<?>) crit).getAlias();
			if (alias != null && !"".equals(alias)) {
				response.setAliasedPaginationInfo(alias, paginationInfo);
				return;
			}
		}
		// Default
		response.setPaginationInfo(crit.getType(), paginationInfo);
	}

	/**
	 * Unwraps the specified search items into the CAL item key they are
	 * referencing.
	 * 
	 * @param items
	 *            the search items to unwrap
	 * @return an array of {@link ItemKey} for use in CAL calls
	 */
	public static ItemKey[] unwrapItemKeys(List<? extends SearchItem> items) {
		List<ItemKey> keys = new ArrayList<ItemKey>(items.size());
		for (SearchItem item : items) {
			keys.add(item.getKey());
		}
		return keys.toArray(new ItemKey[keys.size()]);
	}
}
