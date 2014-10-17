package com.nextep.geo.model;

import com.videopolis.calm.model.Localized;

/**
 * This interface is the representation of a city.
 * 
 * @author cfondacci
 * 
 */
public interface City extends GeographicItem, Localized, GeonameItem {

	String CAL_ID = "CITY";

	/**
	 * Provides the {@link Country} to which this city belongs
	 * 
	 * @return the {@link Country}
	 */
	Country getCountry();

	/**
	 * Provides the first administrative division of the country to which this
	 * city belongs.
	 * 
	 * @return the administrative division level 1 as a {@link Admin} bean
	 */
	Admin getAdm1();

	/**
	 * Provides the second administrative division of the country to which this
	 * city is connected.
	 * 
	 * @return the administrative division level 2 as a {@link Admin} bean
	 */
	Admin getAdm2();

	/**
	 * The city population
	 * 
	 * @return the population of this city
	 */
	int getPopulation();

	/**
	 * Provides the identifier of the timezone of this city. This timezone will
	 * be used for local event dates computation
	 * 
	 * @return the timezone ID
	 */
	String getTimezoneId();

}
