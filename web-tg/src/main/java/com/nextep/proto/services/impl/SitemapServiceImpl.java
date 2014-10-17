package com.nextep.proto.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.SitemapEntry;
import com.nextep.proto.model.impl.SitemapEntryImpl;
import com.nextep.proto.services.SitemapService;
import com.videopolis.calm.model.ItemKey;

public class SitemapServiceImpl implements SitemapService {

	@Override
	public String getPriority(String pageType, SearchType scope) {
		if (Constants.PAGE_TYPE_HOMEPAGE.equals(pageType)) {
			return "1.0";
		} else if (Constants.PAGE_TYPE_SEARCH.equals(pageType)) {
			return getScopePriority(scope);
		} else if (Constants.PAGE_TYPE_OVERVIEW.equals(pageType)) {
			return getScopePriority(scope);
		}
		return "0.5";
	}

	@Override
	public String getChangeFrequency(String pageType, SearchType scope) {
		return "weekly";
	}

	private String getScopePriority(SearchType scope) {
		if (scope == null) {
			return "0.4";
		} else {
			switch (scope) {
			case BARS:
				return "0.9";
			case SAUNAS:
			case SEXCLUBS:
				return "0.8";
			case CLUBS:
				return "0.7";
			case SHOPS:
				return "0.6";
			case RESTAURANTS:
				return "0.5";
			case ASSOCIATIONS:
				return "0.4";
			case EVENTS:
				return "1.0";
			default:
				return "0.3";
			}
		}
	}

	@Override
	public Date getLastModificationDate(String pageType, SearchType scope,
			ItemKey key) {
		final Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -2);
		return cal.getTime();
	}

	@Override
	public SitemapEntry buildEntry(String pageType, SearchType scope,
			ItemKey key, String url) {
		return buildEntry(pageType, scope, key, url, false);
	}

	@Override
	public SitemapEntry buildEntry(String pageType, SearchType scope,
			ItemKey key, String url, boolean isNearby) {
		final String changeFreq = getChangeFrequency(pageType, scope);
		final Date lastMod = getLastModificationDate(pageType, scope, null);
		String priority = getPriority(pageType, scope);
		if (isNearby) {
			priority = String.valueOf(Double.valueOf(priority) - 0.1);
			priority = priority.substring(0, Math.min(3, priority.length()));
		}
		return new SitemapEntryImpl(url, lastMod, changeFreq, priority);
	}
}
