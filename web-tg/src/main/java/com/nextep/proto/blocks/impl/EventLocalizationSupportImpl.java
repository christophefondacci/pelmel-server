package com.nextep.proto.blocks.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.base.AbstractLocalizationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.videopolis.calm.exception.CalException;

public class EventLocalizationSupportImpl extends AbstractLocalizationSupport {

	private static final Log LOGGER = LogFactory
			.getLog(EventLocalizationSupportImpl.class);
	private static final String KEY_SEARCHTYPE = "searchType.EVENTS";

	@Override
	public String getAjaxSearchUrl(GeographicItem item) {
		if (item instanceof Place) {
			return getUrlService().getPlaceOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), item);
		} else {
			if (item == null) {
				if (getCurrentItem() instanceof GeographicItem) {
					item = (GeographicItem) getCurrentItem();
				} else {
					return null;
				}
			}
			return getUrlService().buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), item,
					SearchType.EVENTS, getFacetInfo(), 0);
		}
	}

	@Override
	public String getSearchCalType() {
		return Event.CAL_ID;
	}

	@Override
	public String getSearchTypeLabel() {
		return getMessageSource().getMessage(KEY_SEARCHTYPE, null, getLocale());
	}

	@Override
	public String getSearchUrlForType(String type) {
		GeographicItem geoItem = null;
		if (getCurrentItem() instanceof GeographicItem) {
			geoItem = (GeographicItem) getCurrentItem();
		} else if (getCurrentItem() instanceof Event) {
			try {
				geoItem = getCurrentItem().getUnique(GeographicItem.class);
			} catch (CalException e) {
				LOGGER.error(
						"Unable to extract event localization : "
								+ e.getMessage(), e);
			}
		}
		if (geoItem instanceof Place) {
			geoItem = ((Place) geoItem).getCity();
		}
		return getUrlService().buildSearchUrl("mainContent", geoItem,
				SearchType.valueOf(type), null, 0);
	}
}
