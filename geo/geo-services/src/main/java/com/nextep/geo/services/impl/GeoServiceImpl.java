package com.nextep.geo.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.geo.dao.GeoDao;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.CityImpl;
import com.nextep.geo.model.impl.GeoRequestTypes;
import com.nextep.geo.model.impl.RequestTypeMinPopulation;
import com.nextep.geo.services.GeoService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;

public class GeoServiceImpl extends AbstractDaoBasedCalServiceImpl implements GeoService, CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return GeographicItem.class;
	}

	@Override
	public String getProvidedType() {
		return City.CAL_ID;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Arrays.asList(Country.CAL_ID, Continent.CAL_ID, Admin.CAL_ID, City.CAL_ID);
	}

	@Override
	public List<GeographicItem> listContinents() {
		return ((GeoDao) getCalDao()).listContinents();
	}

	@Override
	public List<GeographicItem> listCountries(GeographicItem continent) {
		return ((GeoDao) getCalDao()).listCountries(continent);
	}

	@Override
	public List<Admin> listAdmins(GeographicItem country) {
		return ((GeoDao) getCalDao()).listAdmins(country);
	}

	@Override
	public List<City> listCities(ItemKey parentKey, int count) {
		final List<City> cities = ((GeoDao) getCalDao()).listCities(parentKey, 0, count);
		final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		for (City c : cities) {
			GeoServiceHelper.fillAlternateMap(alternateIdMap, c);
		}
		GeoServiceHelper.fillAlternatesFromMap((GeoDao) getCalDao(), alternateIdMap, null);
		return cities;
	}

	@Override
	public GeographicItem getCountry(String countryCode) {
		return ((GeoDao) getCalDao()).getCountry(countryCode);
	}

	@Override
	public Admin getAdmin(String countryCode, String admCode) {
		return ((GeoDao) getCalDao()).getAdmin(countryCode, admCode);
	}

	@Override
	public List<City> findCities(String cityName) {
		List<City> cities = ((GeoDao) getCalDao()).findCities(cityName, false);
		List<City> approxCities = ((GeoDao) getCalDao()).findCities(cityName, true);
		cities.addAll(approxCities);
		return cities;
	}

	@Override
	public CalmObject createTransientObject() {
		return new CityImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		// No support for update of geographical reference
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey, ItemKey... internalItemKeys)
			throws CalException {
		Assert.notNull(internalItemKeys, "Need to define the city item key");
		Assert.notNull(contributedItemKey, "Need to define the item to assign a city to");
		Assert.equals(internalItemKeys.length, 1, "Can only assign 1 city to an element");
		final ItemKey cityKey = internalItemKeys[0];
		final City city = ((GeoDao) getCalDao()).bindCity(contributedItemKey, cityKey);
		return Arrays.asList((CalmObject) city);
	}

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context) throws CalException {
		// Casting our well-known DAO
		GeoDao dao = (GeoDao) getCalDao();
		// Preparing our id map (one id list per requested type)
		final Map<String, List<Object>> idsMap = new HashMap<String, List<Object>>();

		// Hashing ids
		for (ItemKey key : ids) {
			// Retrieving corresponding ID list
			final String calType = key.getType();
			List<Object> idsList = idsMap.get(calType);
			// Initializing it if needed
			if (idsList == null) {
				idsList = new LinkedList<Object>();
				idsMap.put(calType, idsList);
			}
			// Appending our ID
			if (Admin.CAL_ID.equals(calType) || Continent.CAL_ID.equals(calType) || Country.CAL_ID.equals(calType)) {
				idsList.add(key.getId());
			} else {
				idsList.add(key.getNumericId());
			}
		}

		// We will now batch query our IDS by type
		final List<CalmObject> objects = new ArrayList<CalmObject>();
		final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		for (String calType : idsMap.keySet()) {
			final List typedIds = idsMap.get(calType);
			if (City.CAL_ID.equals(calType)) {
				// Retrieving cities
				final List<City> cities = dao.getCities(typedIds);
				objects.addAll(cities);
				// Filling list for alternate search
				for (City city : cities) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, city);
				}
			} else if (Place.CAL_TYPE.equals(calType)) {
				final List<Place> places = dao.getPlaces(typedIds);
				objects.addAll(places);
				for (Place place : places) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, place);
				}
			} else if (Country.CAL_ID.equals(calType)) {
				final List<Country> countries = dao.getCountries(typedIds);
				objects.addAll(countries);
				// Filling alternate name search
				for (Country country : countries) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, country);
				}
			} else if (Admin.CAL_ID.equals(calType)) {
				final List<Admin> admins = dao.getAdmins(typedIds);
				objects.addAll(admins);
				// Filling alternate name search
				for (Admin admin : admins) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, admin);
				}
			} else if (Continent.CAL_ID.equals(calType)) {
				final List<Continent> continents = dao.getContinents(typedIds);
				objects.addAll(continents);
				for (Continent continent : continents) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, continent);
				}

			}
		}
		if (!alternateIdMap.isEmpty()) {
			GeoServiceHelper.fillAlternatesFromMap(dao, alternateIdMap, context.getLocale());
		}
		// Reordering
		// Building response
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(reorderCalmObjects(ids, objects));

		return response;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys, CalContext context, RequestType requestType)
			throws CalException {
		// Only supporting unique element as input key
		if (requestType == GeoRequestTypes.TOP_CITIES) {
			Assert.uniqueElement(itemKeys);
			final ItemKey parentKey = itemKeys.iterator().next();

			final List<City> cities = listCities(parentKey, 10);
			final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
			for (City c : cities) {
				GeoServiceHelper.fillAlternateMap(alternateIdMap, c);
			}
			GeoServiceHelper.fillAlternatesFromMap((GeoDao) getCalDao(), alternateIdMap, context.getLocale());
			final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
			response.setItemsFor(parentKey, cities);
			return response;
		} else {
			return super.getItemsFor(itemKeys, context, requestType);
		}
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys, CalContext context) throws CalException {
		final MultiKeyItemsResponse response = super.getItemsFor(itemKeys, context);
		// Filling alternates
		final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		for (ItemKey itemKey : itemKeys) {
			final List<? extends CalmObject> items = response.getItemsFor(itemKey);
			for (CalmObject obj : items) {
				if (obj instanceof GeographicItem) {
					GeoServiceHelper.fillAlternateMap(alternateIdMap, (GeographicItem) obj);
				}
			}
		}
		GeoServiceHelper.fillAlternatesFromMap((GeoDao) getCalDao(), alternateIdMap, context.getLocale());
		return response;
	}

	@Override
	public List<Place> findPlaces(String placeName) {
		List<Place> places = ((GeoDao) getCalDao()).findPlaces(placeName, false);
		List<Place> approxPlaces = ((GeoDao) getCalDao()).findPlaces(placeName, true);
		places.addAll(approxPlaces);
		return places;
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType requestType, RequestSettings requestSettings)
			throws CalException {
		int pageOffset = 0;
		int pageSize = 1000;
		int minPopulation = 0;
		// Adjusting any minimum population requested
		if (requestType instanceof RequestTypeMinPopulation) {
			minPopulation = ((RequestTypeMinPopulation) requestType).getMinPopulation();
		}
		// Adjusting pagination from input
		if (requestSettings instanceof PaginationRequestSettings) {
			final PaginationRequestSettings paginationSettings = (PaginationRequestSettings) requestSettings;
			pageOffset = paginationSettings.getPageNumber();
			pageSize = paginationSettings.getResultsPerPage();
		}
		final GeoDao geoDao = (GeoDao) getCalDao();
		final List<City> cities = geoDao.listCities(null, pageOffset * pageSize, pageSize, minPopulation);

		// Filling alternate names
		Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		for (City city : cities) {
			GeoServiceHelper.fillAlternateMap(alternateIdMap, city);
		}
		GeoServiceHelper.fillAlternatesFromMap(geoDao, alternateIdMap, context.getLocale());

		// Preparing pagination info
		final int citiesCount = geoDao.getCitiesCount(null, minPopulation);
		final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(pageSize, pageOffset);

		// Defining statistics for pagination
		response.setItemCount(citiesCount);
		int pages = citiesCount / pageSize;
		int pagesMod = citiesCount % pageSize;
		response.setPageCount(pages + (pagesMod > 0 ? 1 : 0));
		response.setItemCount(citiesCount);
		response.setItems(cities);
		return response;
	}
}
