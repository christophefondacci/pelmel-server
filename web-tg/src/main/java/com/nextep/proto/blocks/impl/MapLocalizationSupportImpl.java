package com.nextep.proto.blocks.impl;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.base.AbstractLocalizationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;

public class MapLocalizationSupportImpl extends AbstractLocalizationSupport {

	private static final String KEY_SEARCHTYPE = "searchType.PLACES";

	@Override
	public String getAjaxSearchUrl(GeographicItem item) {
		if (!Place.CAL_TYPE.equals(item.getKey().getType())) {
			return getUrlService().buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), item,
					SearchType.MAP, getFacetInfo(), null, null);
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
		return getMessageSource().getMessage(KEY_SEARCHTYPE, null, getLocale());
	}

	@Override
	public String getSearchUrlForType(String type) {
		return getUrlService().buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), getCurrentItem(),
				SearchType.valueOf(type), null, null, null);
	}
}
