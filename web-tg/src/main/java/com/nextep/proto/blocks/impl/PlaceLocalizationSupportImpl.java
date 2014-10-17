package com.nextep.proto.blocks.impl;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.base.AbstractLocalizationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;

public class PlaceLocalizationSupportImpl extends AbstractLocalizationSupport {

	private static final String KEY_PLACETYPE_PREFIX = "facet.label.";

	@Override
	public String getAjaxSearchUrl(GeographicItem item) {
		if (item == null) {
			if (getCurrentItem() instanceof GeographicItem) {
				item = (GeographicItem) getCurrentItem();
			} else {
				return null;
			}
		}
		if (!Place.CAL_TYPE.equals(item.getKey().getType())) {
			return getUrlService().buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), item,
					getSearchType(), getFacetInfo(), null, null);
		} else {
			return getUrlService().getPlaceOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), item);
		}
	}

	@Override
	public String getSearchCalType() {
		return Place.CAL_TYPE;
	}

	@Override
	public String getSearchTypeLabel() {
		return getMessageSource().getMessage(
				KEY_PLACETYPE_PREFIX + getSearchType().getSubtype(), null,
				getLocale());
	}

	@Override
	public String getSearchUrlForType(String type) {
		GeographicItem geoItem = (GeographicItem) getCurrentItem();
		if (geoItem instanceof Place) {
			geoItem = ((Place) geoItem).getCity();
		}
		SearchType searchType = null;
		try {
			searchType = SearchType.valueOf(type);
		} catch (RuntimeException e) {
			searchType = SearchType.fromPlaceType(type);
		}
		return getUrlService().buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), geoItem, searchType,
				null, 0);
	}
}
