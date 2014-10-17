package com.nextep.proto.action.model;

import com.nextep.proto.blocks.GeoPlaceTypesSupport;

/**
 * An interface supporting the enumeration of place types inside a geographical
 * container
 * 
 * @author cfondacci
 * 
 */
public interface GeoPlaceTypesAware {

	/**
	 * Installs the support
	 * 
	 * @param geoPlaceTypesSupport
	 *            the {@link GeoPlaceTypesSupport} implementation to install
	 */
	void setGeoPlaceTypesSupport(GeoPlaceTypesSupport geoPlaceTypesSupport);

	/**
	 * Exposes the support
	 * 
	 * @return the {@link GeoPlaceTypesSupport} implementation
	 */
	GeoPlaceTypesSupport getGeoPlaceTypesSupport();

}
