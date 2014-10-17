package com.nextep.geo.model;

/**
 * Interface defining an item which is provided by geonames and thus exposes the
 * geonames ID
 * 
 * @author cfondacci
 * 
 */
public interface GeonameItem {

	/**
	 * Provides the GEONAMES unique identifier
	 * 
	 * @return the geoname ID
	 */
	Long getGeonameId();
}
