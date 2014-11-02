package com.videopolis.smaug.common.model;

/**
 * Scope for suggest search.
 * 
 * @author Shoun Ichida
 * @since 11 janv. 2011
 */
public enum SuggestScope {
	/** The destination scope for suggest queries. */
	DESTINATION,
	/** The hotel scope for suggest queries. */
	PLACE, USER,
	/** Fulltext search on every name / geo name */
	GEO_FULLTEXT
}
