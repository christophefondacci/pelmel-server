package com.nextep.geo.services;

import java.util.List;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.videopolis.calm.model.ItemKey;

public interface GeoService {

	List<GeographicItem> listContinents();

	List<GeographicItem> listCountries(GeographicItem continent);

	List<Admin> listAdmins(GeographicItem country);

	/**
	 * Lists cities inside the given parent area, ordered by population.
	 * 
	 * @param parent
	 *            the parent {@link GeographicItem}'s {@link ItemKey} to search
	 *            cities in
	 * @param count
	 *            maximum number of cities to return
	 * @return the list of found cities, ordered by population
	 */
	List<City> listCities(ItemKey parentKey, int count);

	/**
	 * Finds a list of matching cities from the starting word
	 * 
	 * @param cityName
	 *            term to search
	 * @return a list of matching cities
	 */
	List<City> findCities(String cityName);

	List<Place> findPlaces(String placeName);

	GeographicItem getCountry(String countryCode);

	Admin getAdmin(String countryCode, String admCode);
}
