package com.nextep.geo.dao;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.AlternateName;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.RequestTypeListPlaces;
import com.videopolis.calm.model.ItemKey;

public interface GeoDao {

	/**
	 * Lists all available continents
	 * 
	 * @return a list of all continents
	 */
	List<GeographicItem> listContinents();

	/**
	 * Lists all available countries in the given continent
	 * 
	 * @param continent
	 *            the continent
	 * @return a list of all countries in that continent
	 */
	List<GeographicItem> listCountries(GeographicItem continent);

	/**
	 * Lists all administrative division of the specified {@link Country}
	 * 
	 * @param country
	 *            the {@link Country}
	 * @return the list of all known {@link Admin}
	 */
	List<Admin> listAdmins(GeographicItem country);

	/**
	 * Provides the altername name for the specified geonameItem in the given
	 * language
	 * 
	 * @param geonameItems
	 *            list of items to search for alternates
	 * @param locale
	 *            the locale to search alternates for or <code>null</code>
	 * @return a map of all found alternates hashed by the geoname id. Note that
	 *         when not all provided items may have alternates
	 */
	Map<Long, List<AlternateName>> getAlternateNamesFor(Collection<Long> geonameIds, Locale locale);

	/**
	 * Lists cities inside the given parent area, ordered by population.
	 * 
	 * @param parent
	 *            the parent {@link GeographicItem}'s {@link ItemKey} to search
	 *            cities in
	 * @param count
	 *            maximum number of cities to return
	 * @param minPopulation
	 *            the minimum population that a city must have to be returned in
	 *            that list
	 * @return the list of found cities, ordered by population
	 */
	List<City> listCities(ItemKey parentKey, int offset, int count, int minPopulation);

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
	List<City> listCities(ItemKey parentKey, int offset, int count);

	/**
	 * Provides the number of cities in the specified geo area
	 * 
	 * @param parentKey
	 * @return the number of cities (used for pagination info
	 */
	int getCitiesCount(ItemKey parentKey);

	/**
	 * Provides the number of cities in the specified geographic area having at
	 * least the specified population.
	 * 
	 * @param parentKey
	 *            parent geographic area's {@link ItemKey}
	 * @param minPopulationminimum
	 *            population of the cities to count
	 * @return the number of corresponding cities
	 */
	int getCitiesCount(ItemKey parentKey, int minPopulation);

	List<City> findCities(String cityName, boolean approx);

	List<Place> findPlaces(String placeName, boolean approx);

	List<GeographicItem> listCountryCities(GeographicItem country);

	Country getCountry(String countryCode);

	City bindCity(ItemKey externalItem, ItemKey cityKey);

	City getCity(long id);

	/**
	 * Loads an {@link Admin} referenced by its country code and admin code
	 * 
	 * @param countryCode
	 *            the {@link Admin}'s country code
	 * @param adminCode
	 *            the {@link Admin}'s code
	 * @return the loaded admin
	 */
	Admin getAdmin(String countryCode, String adminCode);

	/**
	 * Provides the Admin object referenced by its unique ID
	 * 
	 * @param adminId
	 *            {@link Admin} unique ID
	 * @return the {@link Admin}
	 */
	Admin getAdmin(String adminId);

	/**
	 * Provides the continent referenced from its code
	 * 
	 * @param continentCode
	 *            continent's code
	 * @return the Continent
	 */
	GeographicItem getContinent(String continentCode);

	/**
	 * Finds all places of a given city
	 * 
	 * @param cityId
	 * @return
	 */
	List<Place> getPlacesInCity(long cityId, int itemsPerPage, int pageOffset);

	/**
	 * Finds the number of places per place type of a given city
	 * 
	 * @param cityId
	 *            identifier of the city
	 * @return the number of places hashed by place type
	 */
	int getPlacesCountInCity(long cityId);

	/**
	 * Lists all places (used by the places indexer)
	 * 
	 * @return a list of all places
	 */
	List<Place> listPlaces();

	/**
	 * Lists places with filters and sort defined by request type (for admin use
	 * only)
	 * 
	 * @param request
	 *            the {@link RequestTypeListPlaces} containing filters, sort
	 *            information to use for this list query
	 * @return the list of matching {@link Place}s, in the sort order requested
	 */
	List<Place> listPlaces(RequestTypeListPlaces request);

	long countPlaces(RequestTypeListPlaces request);

	/**
	 * Fetches the requested places from the database
	 * 
	 * @param placeIds
	 *            list of places' id
	 * @return the list of {@link Place}, note that there could be missing
	 *         places if the requested id does not exist
	 */
	List<Place> getPlaces(List<Long> placeIds);

	/**
	 * Gets the places from their facebook id
	 * 
	 * @param facebookIds
	 *            the list of facebook ids
	 * @return the list of places attached to those ids
	 */
	List<Place> getPlacesFromFacebookId(List<String> facebookIds);

	/**
	 * Retrieves the places assigned to the given item key
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} to get places for
	 * @return the list of {@link Place} associated with the given element
	 */
	List<Place> getPlacesFor(ItemKey itemKey, int pageSize, int offset);

	List<Place> getPlacesFor(ItemKey itemKey);

	int getPlacesForCount(ItemKey itemKey);

	/**
	 * Binds the specified places to the given external item.
	 * 
	 * @param externalItem
	 *            the item to associate with places
	 * @param placeKeys
	 *            the {@link ItemKey} of {@link Place}s to associate
	 * @return the list of associated {@link Place}
	 */
	List<Place> bindPlaces(ItemKey externalItem, List<ItemKey> placeKeys);

	/**
	 * Gets the specified list of countries
	 * 
	 * @param ids
	 *            the list of country ids to fetch
	 * @return the list of {@link Country}
	 */
	List<Country> getCountries(List<Long> ids);

	/**
	 * Gets the specified list of cities
	 * 
	 * @param ids
	 *            the list of city ids
	 * @return the list of {@link City}
	 */
	List<City> getCities(List<Long> ids);

	/**
	 * Gets the specified list of admins
	 * 
	 * @param ids
	 *            list of admin ids
	 * @return the list of {@link Admin}
	 */
	List<Admin> getAdmins(List<String> ids);

	/**
	 * Gets the speicifed list of continents
	 * 
	 * @param ids
	 *            list of continent IDs
	 * @return the list of continents as {@link GeographicItem}
	 */
	List<Continent> getContinents(List<String> ids);
}
