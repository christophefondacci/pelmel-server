package com.nextep.geo.model;

/**
 * This interface represents a country.
 * 
 * @author cfondacci
 * 
 */
public interface Country extends GeographicItem, GeonameItem {

	String CAL_ID = "CNTY";

	/**
	 * Provides access to the continent in which this country is located
	 * 
	 * @return the continent, as a {@link GeographicItem}
	 */
	Continent getContinent();

	/**
	 * Provides the country code
	 * 
	 * @return the country code
	 */
	String getCode();

}
