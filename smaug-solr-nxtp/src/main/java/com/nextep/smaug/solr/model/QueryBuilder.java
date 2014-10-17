package com.nextep.smaug.solr.model;

import org.apache.solr.client.solrj.SolrQuery;

import com.videopolis.smaug.model.SearchSettings;
import com.videopolis.smaug.model.SearchWindow;

/**
 * This interface offers different implementations to build different types of
 * queries
 * 
 */
public interface QueryBuilder {

	/**
	 * build an object that would be specified by an implementor (conventionally
	 * ) using the given {@link SearchSettings} and {@link SearchWindow}
	 * 
	 * @param searchSettings
	 *            the {@link SearchSettings} used to configure and build the
	 *            query
	 * @param searchWindow
	 *            the {@link SearchWindow} used to configure the start and rows
	 *            number for query
	 * @return an Object so that the implementer can return any type of query
	 *         depending on the underlying technology in our case it's a
	 *         SolrQuery
	 */
	SolrQuery buildQuery(SearchSettings searchSettings,
			SearchWindow searchWindow);

}
