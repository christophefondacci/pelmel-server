package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiResponse;

/**
 * This interface defines the support for place types available within a given
 * geographic zone
 * 
 * @author cfondacci
 * 
 */
public interface GeoPlaceTypesSupport {

	/**
	 * Initializes this support
	 * 
	 * @param l
	 *            current {@link Locale}
	 * @param urlService
	 *            current {@link UrlService}
	 * @param parentGeoItem
	 *            parent {@link GeographicItem}
	 * @param facetInfo
	 *            current {@link FacetInformation}
	 * @param currentPlaceType
	 *            current place type
	 */
	void initialize(Locale l, UrlService urlService,
			GeographicItem parentGeoItem, FacetInformation facetInfo,
			String currentPlaceType, ApiResponse response);

	/**
	 * Provides the list of available place types to display
	 * 
	 * @return the list of place type codes
	 */
	List<String> getAvailablePlaceTypes();

	/**
	 * Provides the label for this place type
	 * 
	 * @param placeType
	 *            the code of the place type
	 * @return the corresponding label
	 */
	String getPlaceTypeLabel(String placeType);

	/**
	 * Provides the number of places available in this category
	 * 
	 * @param placeType
	 *            the place type code
	 * @return the number of places of that type
	 */
	int getPlaceTypeCount(String placeType);

	/**
	 * Indicates whether there is at least one place type to display
	 * 
	 * @return <code>true</code> when there is something to display, else
	 *         <code>false</code>
	 */
	boolean hasPlaceTypesAvailable();

	/**
	 * Provides the URL of this place type
	 * 
	 * @param placeType
	 *            the place type code
	 * @return the URL of this place
	 */
	String getPlaceTypeUrl(String placeType);

	/**
	 * Provides the current place type
	 * 
	 * @return the current place type code
	 */
	String getCurrentPlaceType();
}
