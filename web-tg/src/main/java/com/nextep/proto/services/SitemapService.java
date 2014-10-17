package com.nextep.proto.services;

import java.util.Date;

import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.SitemapEntry;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This service provides methods to compute information published in sitemaps.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface SitemapService {

	/**
	 * Retrieves the priority of a sitemap
	 * 
	 * @param pageType
	 *            the page type of the sitemap entry
	 * @param scope
	 *            the {@link SearchScope} of the sitemap / sitemap entry
	 * @return the priority string
	 */
	String getPriority(String pageType, SearchType type);

	/**
	 * Retrieves the change frequency of a sitemap.
	 * 
	 * @param pageType
	 *            the page type of the sitemap entry
	 * @param scope
	 *            the {@link SearchScope} of the sitemap
	 * @return the change frequency String
	 */
	String getChangeFrequency(String pageType, SearchType type);

	/**
	 * Retrieves the last modification date of a sitemap entry
	 * 
	 * @param pageType
	 *            the page type of the sitemap entry
	 * @param scope
	 *            the {@link SearchScope} of the sitemap entry
	 * @param key
	 *            the optional key of the element, for later use, ignored by the
	 *            service for now
	 * @return the last modification date
	 */
	Date getLastModificationDate(String pageType, SearchType type, ItemKey key);

	SitemapEntry buildEntry(String pageType, SearchType type, ItemKey key,
			String url);

	SitemapEntry buildEntry(String pageType, SearchType type, ItemKey key,
			String url, boolean isNearby);
}
