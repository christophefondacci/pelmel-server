package com.nextep.proto.apis.adapters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

/**
 * This adapter converts facets resulting from a searchto item keys
 * 
 * @author cfondacci
 * 
 */
public class ApisFacetToItemKeyAdapter implements ApisCustomAdapter {

	private static final Log LOGGER = LogFactory
			.getLog(ApisFacetToItemKeyAdapter.class);

	private final SearchScope scope;
	private final FacetCategory facetCategory;
	private final int maxFacets;
	private final int facetOffset;

	/**
	 * Builds a new facet adapter for the given search scope (used to retrieve
	 * the adequate facets)
	 * 
	 * @param scope
	 */
	public ApisFacetToItemKeyAdapter(SearchScope scope, FacetCategory category,
			int maxFacets) {
		this.scope = scope;
		this.facetCategory = category;
		this.maxFacets = maxFacets;
		this.facetOffset = 0;
	}

	public ApisFacetToItemKeyAdapter(SearchScope scope, FacetCategory category,
			int maxFacets, int facetOffset) {
		this.scope = scope;
		this.facetCategory = category;
		this.maxFacets = maxFacets;
		this.facetOffset = facetOffset;
	}

	@Override
	public List<ItemKey> adapt(ApisContext context, CalmObject... parents) {
		final ApiResponse response = context.getApiResponse();
		FacetInformation facetInfo = null;
		int retries = 0;
		while (facetInfo == null && retries < 10) {
			facetInfo = response.getFacetInformation(scope);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				LOGGER.error(
						"Interruption during sleep in CustomAdapt : "
								+ e.getMessage(), e);
			}
			retries++;
		}
		if (facetInfo == null) {
			return Collections.emptyList();
		}
		List<FacetCount> facetCounts = facetInfo.getFacetCounts(facetCategory);
		// Extracting only the window we want to process
		if (facetCounts == null || facetCounts.isEmpty()
				|| facetOffset >= facetCounts.size()) {
			return Collections.emptyList();
		} else {
			if (facetOffset > 0 || maxFacets > 0) {
				facetCounts = facetCounts.subList(facetOffset,
						Math.min(facetCounts.size(), facetOffset + maxFacets));
			}

			final List<ItemKey> keys = new LinkedList<ItemKey>();
			for (FacetCount facetCount : facetCounts) {
				final String facetCode = facetCount.getFacet().getFacetCode();
				try {
					final ItemKey itemKey = CalmFactory.parseKey(facetCode);
					keys.add(itemKey);
				} catch (CalException e) {
					LOGGER.error("Facet is not an item key : " + e.getMessage());
				}
			}
			return keys;
		}
	}
}
