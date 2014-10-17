package com.nextep.proto.blocks.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.blocks.base.AbstractLocalizationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;

public class UserLocalizationSupportImpl extends AbstractLocalizationSupport {

	private static final String KEY_SEARCHTYPE = "searchType.MEN";
	private static final Log LOGGER = LogFactory
			.getLog(UserLocalizationSupportImpl.class);

	@Override
	public String getAjaxSearchUrl(GeographicItem item) {
		if (item == null) {
			if (getCurrentItem() instanceof GeographicItem) {
				item = (GeographicItem) getCurrentItem();
			} else {
				return null;
			}
		}
		return getUrlService().buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), item, SearchType.MEN,
				getFacetInfo(), null, null);
	}

	@Override
	public String getSearchCalType() {
		return User.CAL_TYPE;
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
		} else if (getCurrentItem() instanceof User) {
			try {
				geoItem = ((User) getCurrentItem())
						.getUnique(GeographicItem.class);
			} catch (CalException e) {
				LOGGER.error(
						"Unable to get user localization : " + e.getMessage(),
						e);
			}
		}
		return getUrlService().buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), geoItem,
				SearchType.valueOf(type), null, 0);
	}
}
