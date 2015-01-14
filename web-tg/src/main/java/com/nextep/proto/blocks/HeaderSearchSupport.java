package com.nextep.proto.blocks;

import com.videopolis.apis.model.FacetInformation;

/**
 * A small extension to the header support allowing to define whether we are in
 * the context of a nearby search.
 * 
 * @author cfondacci
 * 
 */
public interface HeaderSearchSupport extends HeaderSupport {

	/**
	 * Sets whether the current search results have been returned from a
	 * secondary nearby search.
	 * 
	 * @param isNearbySearch
	 *            <code>true</code> for nearby, else <code>false</code>
	 *            (default)
	 */
	void setNearbySearch(boolean isNearbySearch);

	/**
	 * Indicates whether the current search results have been returned from a
	 * secondary nearby search.
	 * 
	 * @return <code>true</code> when the current search items results from a
	 *         nearby search, else <code>false</code>
	 */
	boolean isNearbySearch();

	/**
	 * Provides GEO facetting information that helps to resolve indexation
	 * status
	 * 
	 * @param geoFacets
	 *            the {@link FacetInformation} bean containing geo information
	 *            (sub or super GEO facets)
	 */
	// void setGeoFacetting(FacetInformation geoFacets);

	String getAlternate(String language, boolean facetted);
}
