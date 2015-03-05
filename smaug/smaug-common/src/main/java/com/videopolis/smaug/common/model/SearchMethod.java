/**
 * 
 */
package com.videopolis.smaug.common.model;

/**
 * @author shoun
 * 
 */
public enum SearchMethod {
	/** City search including shadow cities. */
	CITIES_WITH_SHADOW,
	/** City search excluding shadow cities. */
	CITIES_WITHOUT_SHADOW,
	/** City search including shadow cities depending on the given locale. */
	CITIES_WITH_SHADOW_AUTODETECT,
	/** City search including <b>only</b> shadow cities. */
	SHADOW_CITY,
	/** Includes all facets */
	NO_FACET_LIMIT
}
