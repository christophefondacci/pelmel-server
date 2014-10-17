package com.nextep.proto.model;

import java.util.List;

/**
 * Generic interface for components providing sitemap entries.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface SitemapEntryProvider {

	/**
	 * Provides the list of all available sitemap entries.
	 * 
	 * @return a list of {@link SitemapEntry}
	 */
	List<SitemapEntry> getSitemapEntries();
}
