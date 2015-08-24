package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.blocks.GeoPlaceTypesSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.model.FacetCount;

public class GeoPlaceTypesSupportImpl implements GeoPlaceTypesSupport {

	private static final String TRANSLATION_KEY_PLACETYPE_PREFIX = "facet.label.";

	// Injected services
	private MessageSource messageSource;

	// Internal initialization variables
	private Locale locale;
	private UrlService urlService;
	private GeographicItem parentGeoItem;
	private FacetInformation facetInfo;
	private String currentPlaceType;

	// Internal
	private Map<String, Integer> otherPlaceTypesMap;
	private List<String> otherPlaceTypes;

	@Override
	public void initialize(Locale l, UrlService urlService, GeographicItem parentGeoItem, FacetInformation facetInfo,
			String currentPlaceType, ApiResponse response) {
		this.locale = l;
		this.urlService = urlService;
		this.parentGeoItem = parentGeoItem;
		this.facetInfo = facetInfo;
		this.currentPlaceType = currentPlaceType;

		// Iterating over all facet counts to extract all place types but the
		// current one
		final List<FacetCount> facetCounts = facetInfo.getFacetCounts(SearchHelper.getPlaceTypeCategory());
		otherPlaceTypesMap = new HashMap<String, Integer>();
		for (FacetCount fc : facetCounts) {
			// If we got a code different from current place type then we add it
			// to our list
			// if (!fc.getFacet().getFacetCode().equals(currentPlaceType)) {
			otherPlaceTypesMap.put(fc.getFacet().getFacetCode(), fc.getCount());
			// }
		}

		// Getting user and event pagination from APIS response to determine
		// whether
		// or not we should add those entries to the menu
		// final PaginationInfo userPagination = response
		// .getPaginationInfo(Constants.APIS_ALIAS_USER_COUNT);
		final PaginationInfo eventPagination = response.getPaginationInfo(Constants.APIS_ALIAS_EVENT_COUNT);
		// if (userPagination != null && userPagination.getItemCount() > 0) {
		// otherPlaceTypesMap.put(SearchType.MEN.getSubtype(),
		// userPagination.getItemCount());
		// }
		if (eventPagination != null && eventPagination.getItemCount() > 0) {
			otherPlaceTypesMap.put(SearchType.EVENTS.getSubtype(), eventPagination.getItemCount());
		}

		// Building a sorted list of placetypes
		otherPlaceTypes = new ArrayList<String>(otherPlaceTypesMap.keySet());
		Collections.sort(otherPlaceTypes, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				// Getting the labels
				final String name1 = getPlaceTypeLabel(o1);
				final String name2 = getPlaceTypeLabel(o2);

				// Comparing labels
				return name1.compareTo(name2);
			}
		});
	}

	@Override
	public List<String> getAvailablePlaceTypes() {
		return otherPlaceTypes;
	}

	@Override
	public String getPlaceTypeLabel(String placeType) {
		return messageSource.getMessage(TRANSLATION_KEY_PLACETYPE_PREFIX + placeType, null, locale);
	}

	@Override
	public int getPlaceTypeCount(String placeType) {
		return otherPlaceTypesMap.get(placeType);
	}

	@Override
	public boolean hasPlaceTypesAvailable() {
		return !otherPlaceTypes.isEmpty();
	}

	@Override
	public String getPlaceTypeUrl(String placeType) {
		// Building the URL for the given place type in the current geographic
		// container
		return urlService.buildSearchUrl(DisplayHelper.getDefaultAjaxContainer(), parentGeoItem,
				SearchType.fromPlaceType(placeType), facetInfo, 0);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getCurrentPlaceType() {
		return currentPlaceType;
	}
}
