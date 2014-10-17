package com.nextep.proto.model;

/**
 * Defines an entry of a XML sitemap
 * 
 * @author Christophe Fondacci
 * 
 */
public interface SitemapEntry {

	/**
	 * Provides the URL of this sitemap entry.
	 * 
	 * @return the URL string
	 */
	String getUrl();

	String getLastModification();

	String getChangeFreq();

	String getPriority();
}
