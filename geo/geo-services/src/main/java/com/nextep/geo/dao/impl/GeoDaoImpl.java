package com.nextep.geo.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.model.CalDao;
import com.nextep.geo.dao.GeoDao;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.AlternateName;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.AdmImpl;
import com.nextep.geo.model.impl.CountryImpl;
import com.nextep.geo.model.impl.ItemCityImpl;
import com.nextep.geo.model.impl.ItemPlaceImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

@SuppressWarnings("unchecked")
public class GeoDaoImpl implements CalDao<CalmObject>, GeoDao {

	private static final Log log = LogFactory.getLog(GeoDaoImpl.class);

	@PersistenceContext(unitName = "nextep-geo")
	private EntityManager entityManager;

	@Override
	public CalmObject getById(long id) {
		return (CalmObject) entityManager
				.createQuery("from CityImpl where id=:id")
				.setParameter("id", id).getSingleResult();
	}

	// public Map<Long, AlternateName> getAlternateNamesForObjects(
	// Collection<GeonameItem> geonameItems, Locale locale) {
	// // Building our list of all geoname IDs
	// final List<Long> geonameIds = new ArrayList<Long>();
	// for (GeonameItem item : geonameItems) {
	// geonameIds.add(item.getGeonameId());
	// }
	// return getAlternateNamesFor(geonameIds, locale);
	// }

	@Override
	public Map<Long, List<AlternateName>> getAlternateNamesFor(
			Collection<Long> geonameIds, Locale locale) {
		// Extracting language
		final Map<Long, List<AlternateName>> alternateNamesMap = new HashMap<Long, List<AlternateName>>();
		if (geonameIds != null) {
			// Searching
			final List<AlternateName> alternates = entityManager
					.createQuery(
							"from AlternateNameImpl where geonameId in (:geonameIds)")
					.setParameter("geonameIds", geonameIds).getResultList();
			// Building our result map by hashing by geoname ID
			for (AlternateName altName : alternates) {
				final Long geonameId = altName.getParentGeonameId();
				List<AlternateName> alternateResults = alternateNamesMap
						.get(geonameId);
				if (alternateResults == null) {
					alternateResults = new ArrayList<AlternateName>();
					alternateNamesMap.put(geonameId, alternateResults);
				}
				alternateResults.add(altName);
			}
		}
		return alternateNamesMap;
	}

	@Override
	public List<CalmObject> getByIds(final List<Long> idList) {
		final List<CalmObject> objList = entityManager
				.createQuery(
						"from CityImpl c left join fetch c.adm1 "
								+ "left join fetch c.adm2 "
								+ "left join fetch c.country country "
								+ "left join fetch country.continent "
								+ "where c.id in (:ids) ")
				.setParameter("ids", idList).getResultList();
		return objList;

	}

	@Override
	public List<Country> getCountries(List<Long> ids) {
		final List<Country> objList = entityManager
				.createQuery(
						"from CountryImpl c left join fetch c.continent where c.id in (:ids)")
				.setParameter("ids", ids).getResultList();
		return objList;
	}

	@Override
	public List<Admin> getAdmins(List<String> ids) {
		final List<Admin> objList = entityManager
				.createQuery("from AdmImpl where id in (:ids)")
				.setParameter("ids", ids).getResultList();
		return objList;
	}

	@Override
	public List<Continent> getContinents(List<String> ids) {
		final List<Continent> objList = entityManager
				.createQuery("from ContinentImpl where id in (:ids)")
				.setParameter("ids", ids).getResultList();
		return objList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<City> getCities(List<Long> ids) {
		return (List) getByIds(ids);
	}

	@Override
	public List<CalmObject> getItemsFor(ItemKey key) {
		if (key == null) {
			return Collections.emptyList();
		} else {
			final ItemCityImpl itemCity = findItemCityFor(key);
			// Unwrapping tags
			if (itemCity != null) {
				return Arrays.asList((CalmObject) itemCity.getCity());
			} else {
				return Collections.emptyList();
			}
		}
	}

	private ItemCityImpl findItemCityFor(ItemKey key) {
		if (key == null) {
			return null;
		} else {
			final Query query = entityManager.createQuery(
					"from ItemCityImpl where externalItemKey=:key")
					.setParameter("key", key.toString());
			try {
				// Retrieving from DB
				final ItemCityImpl itemCity = (ItemCityImpl) query
						.getSingleResult();
				return itemCity;
			} catch (RuntimeException e) {
				log.error("Unable to retrieve city for item id "
						+ key.toString() + ": " + e.getMessage());
				return null;
			}
		}
	}

	private List<ItemPlaceImpl> findItemPlacesFor(ItemKey key) {
		return findItemPlacesFor(key, -1, -1);
	}

	/**
	 * Finds places registered for given external item key
	 * 
	 * @param key
	 *            the {@link ItemKey} of element to get places for
	 * @return the list of places for this item
	 */
	private List<ItemPlaceImpl> findItemPlacesFor(ItemKey key, int pageSize,
			int pageOffset) {
		if (key == null) {
			return null;
		} else {
			final Query query = entityManager.createQuery(
					"from ItemPlaceImpl where externalItemKey=:key")
					.setParameter("key", key.toString());
			if (pageSize >= 0) {
				query.setMaxResults(pageSize);
			}
			if (pageOffset >= 0) {
				query.setFirstResult(pageOffset * pageSize);
			}
			try {
				// Retrieving from DB
				final List<ItemPlaceImpl> itemPlaces = query.getResultList();
				if (itemPlaces != null) {
					return itemPlaces;
				} else {
					return Collections.emptyList();
				}
			} catch (RuntimeException e) {
				log.error("Unable to retrieve places for id " + key.toString()
						+ ": " + e.getMessage());
				return Collections.emptyList();
			}
		}
	}

	@Override
	public List<CalmObject> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		return null;
	}

	@Override
	public void save(CalmObject object) {
		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}

	@Override
	public List<GeographicItem> listContinents() {
		return entityManager.createQuery("from ContinentImpl").getResultList();
	}

	@Override
	public List<GeographicItem> listCountries(GeographicItem continent) {
		return entityManager
				.createQuery("from CountryImpl where continent=:continent")
				.setParameter("continent", continent).getResultList();
	}

	@Override
	public List<Admin> listAdmins(GeographicItem country) {
		if (CountryImpl.CAL_ID.equals(country.getKey().getType())) {
			return entityManager
					.createQuery(
							"from AdmImpl where country=:country and parentAdm is null")
					.setParameter("country", country).getResultList();
		} else if (AdmImpl.CAL_ID.equals(country.getKey().getType())) {
			return entityManager
					.createQuery("from AdmImpl where parentAdm=:parentAdm")
					.setParameter("parentAdm", country).getResultList();
		}
		return Collections.emptyList();
	}

	@Override
	public List<City> listCities(ItemKey parentKey, int offset, int count) {
		return listCities(parentKey, offset, count, 0);
	}

	@Override
	public List<City> listCities(ItemKey adminKey, int offset, int count,
			int minPopulation) {
		if (adminKey == null) {
			return entityManager
					.createQuery(
							"from CityImpl where population>=:minPop order by population desc")
					.setParameter("minPop", minPopulation).setMaxResults(count)
					.setFirstResult(offset).getResultList();
		} else if (Country.CAL_ID.equals(adminKey.getType())) {
			return entityManager
					.createQuery(
							"from CityImpl where country.code=:country and population>=:minPop order by population desc")
					.setParameter("country", adminKey.getId())
					.setParameter("minPop", minPopulation).setMaxResults(count)
					.setFirstResult(offset).getResultList();
		} else if (Admin.CAL_ID.equals(adminKey.getType())) {
			return entityManager
					.createQuery(
							"from CityImpl where population>=:minPop and (adm1.admId=:adm1 or adm2.admId=:adm2) order by population desc")
					.setParameter("adm1", adminKey.getId())
					.setParameter("adm2", adminKey.getId())
					.setParameter("minPop", minPopulation).setMaxResults(count)
					.setFirstResult(offset).getResultList();
		} else if (Continent.CAL_ID.equals(adminKey.getType())) {
			return entityManager
					.createQuery(
							"from CityImpl where population>=:minPop and country.continent.code=:continent order by population desc")
					.setParameter("continent", adminKey.getId())
					.setParameter("minPop", minPopulation).setMaxResults(count)
					.setFirstResult(offset).getResultList();
		}
		return Collections.emptyList();
	}

	@Override
	public int getCitiesCount(ItemKey parentKey) {
		return getCitiesCount(parentKey, 0);
	}

	@Override
	public int getCitiesCount(ItemKey parentKey, int minPopulation) {
		// Building sql query
		Query q = null;
		if (parentKey == null) {
			q = entityManager.createNativeQuery(
					"select count(1) from GEO_CITIES where population>:minPop")
					.setParameter("minPop", minPopulation);
		} else {
			final String type = parentKey.getType();
			if (Country.CAL_ID.equals(type)) {
				q = entityManager
						.createNativeQuery(
								"select count(1) from GEO_CITIES where country_code=:country and population>:minPop")
						.setParameter("country", parentKey.getId())
						.setParameter("minPop", minPopulation);
			} else if (Admin.CAL_ID.equals(type)) {
				q = entityManager
						.createNativeQuery(
								"select count(1) from GEO_CITIES where (adm1_id=:adm1 or adm2_id=:adm2) and population>:minPop")
						.setParameter("adm1", parentKey.getId())
						.setParameter("adm2", parentKey.getId())
						.setParameter("minPop", minPopulation);
			}
		}
		if (q != null) {
			final BigInteger count = (BigInteger) q.getSingleResult();
			return count.intValue();
		} else {
			return 0;
		}
	}

	@Override
	public List<GeographicItem> listCountryCities(GeographicItem country) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Admin getAdmin(String countryCode, String admCode) {
		return (Admin) entityManager
				.createQuery(
						"from AdmImpl where country.code = :countryCode and admCode=:admCode")
				.setParameter("countryCode", countryCode)
				.setParameter("admCode", admCode).getSingleResult();
	}

	@Override
	public Country getCountry(String countryCode) {
		return (Country) entityManager
				.createQuery("from CountryImpl where code = :code")
				.setParameter("code", countryCode).getSingleResult();
	}

	@Override
	public Admin getAdmin(String adminId) {
		return (Admin) entityManager
				.createQuery("from AdmImpl where admId = :id")
				.setParameter("id", adminId).getSingleResult();
	}

	@Override
	public City getCity(long id) {
		return (City) getById(id);
	}

	@Override
	public GeographicItem getContinent(String continentCode) {
		return (GeographicItem) entityManager
				.createQuery("from ContinentImpl where code = :code")
				.setParameter("code", continentCode).getSingleResult();
	}

	@Override
	public List<City> findCities(String cityName, boolean approx) {
		if (approx) {
			return entityManager
					.createQuery(
							"from CityImpl where name like :name and name <> :exactName")
					.setParameter("name", cityName + "%")
					.setParameter("exactName", cityName).setMaxResults(20)
					.getResultList();
		} else {
			return entityManager
					.createQuery("from CityImpl where name like :name")
					.setParameter("name", cityName).setMaxResults(20)
					.getResultList();
		}
	}

	@Override
	public List<Place> findPlaces(String placeName, boolean approx) {
		if (approx) {
			return entityManager
					.createQuery(
							"from PlaceImpl where name like :name and name <> :exactName")
					.setParameter("name", placeName + "%")
					.setParameter("exactName", placeName).setMaxResults(20)
					.getResultList();
		} else {
			return entityManager
					.createQuery("from CityImpl where name like :name")
					.setParameter("name", placeName).setMaxResults(20)
					.getResultList();
		}
	}

	@Override
	public City bindCity(ItemKey externalItem, ItemKey cityKey) {
		// Deleting previous entries
		entityManager
				.createQuery(
						"delete from ItemCityImpl where externalItemKey=:key")
				.setParameter("key", externalItem.toString()).executeUpdate();
		// If not existing we load the city
		final City city = (City) getById(cityKey.getNumericId());
		// Setting up new association
		final ItemCityImpl itemCity = new ItemCityImpl(externalItem, city);
		entityManager.persist(itemCity);
		return city;
	}

	@Override
	public List<Place> bindPlaces(ItemKey externalItem, List<ItemKey> placeKeys) {
		// Looking for any pre-existing city definition for this user
		final List<ItemPlaceImpl> itemPlaces = findItemPlacesFor(externalItem);
		// Hashing the found places by place key for fast lookup
		final Map<ItemKey, ItemPlaceImpl> itemPlacesMap = new HashMap<ItemKey, ItemPlaceImpl>();
		for (ItemPlaceImpl itemPlace : itemPlaces) {
			try {
				final ItemKey placeKey = CalmFactory.createKey(Place.CAL_TYPE,
						itemPlace.getPlaceId());
				itemPlacesMap.put(placeKey, itemPlace);
			} catch (CalException e) {
				log.error("Exception: " + e.getMessage(), e);
			}
		}
		// Processing new places
		final List<Long> placeIds = new ArrayList<Long>();
		final List<Place> allPlaces = new ArrayList<Place>();
		for (ItemKey placeKey : placeKeys) {
			final ItemPlaceImpl existingItemPlace = itemPlacesMap.get(placeKey);
			if (existingItemPlace == null) {
				// If not existing we load the city
				placeIds.add(placeKey.getNumericId());
			} else {
				// If existing we remove association
				entityManager.remove(existingItemPlace);
				placeIds.remove(placeKey.getNumericId());
				// allPlaces.add(existingItemPlace.getPlace());
			}
		}
		// Registering new places
		final List<Place> places = getPlaces(placeIds);
		for (Place p : places) {
			// final Place mergedPlace = entityManager.merge(p);
			// Setting up new association
			final ItemPlaceImpl itemPlace = new ItemPlaceImpl(externalItem,
					p.getKey());
			entityManager.persist(itemPlace);
		}
		entityManager.flush();
		allPlaces.addAll(places);
		return allPlaces;
	}

	@Override
	public List<CalmObject> getItemsFor(ItemKey key, int resultsPerPage,
			int pageOffset) {
		return Collections.emptyList();
	}

	@Override
	public int getPlacesCountInCity(long cityId) {
		return 0;
	}

	@Override
	public List<Place> getPlacesInCity(long cityId, int itemsPerPage,
			int pageOffset) {
		try {
			return entityManager
					.createQuery("from PlaceImpl where city.id=:cityId")
					.setParameter("cityId", cityId).setMaxResults(itemsPerPage)
					.setFirstResult(pageOffset).getResultList();
		} catch (NoResultException e) {
			// This is a normal use case: there could be no place defined in a
			// city
			return Collections.emptyList();
		}
	}

	@Override
	public List<Place> listPlaces() {
		return entityManager.createQuery("from PlaceImpl").getResultList();
	}

	@Override
	public List<Place> getPlaces(List<Long> placeIds) {
		if (placeIds == null || placeIds.isEmpty()) {
			return Collections.emptyList();
		} else {
			return entityManager
					.createQuery(
							"from PlaceImpl p "
									+ "left join fetch p.city city "
									+ "left join fetch city.adm1 "
									+ "left join fetch city.adm2 "
									+ "left join fetch city.country country "
									+ "left join fetch country.continent "
									+ "where p.id in (:placeIds)")
					.setParameter("placeIds", placeIds).getResultList();
		}
	}

	@Override
	public List<Place> getPlacesFor(ItemKey itemKey, int pageSize,
			int pageOffset) {
		final List<ItemPlaceImpl> itemPlaces = findItemPlacesFor(itemKey,
				pageSize, pageOffset);
		final List<Long> placeIds = new ArrayList<Long>();
		for (ItemPlaceImpl itemPlace : itemPlaces) {
			placeIds.add(Long.valueOf(itemPlace.getPlaceId()));
		}
		return getPlaces(placeIds);
	}

	@Override
	public List<Place> getPlacesFor(ItemKey itemKey) {
		return getPlacesFor(itemKey, -1, -1);
	}

	@Override
	public int getPlacesForCount(ItemKey itemKey) {
		return ((BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from GEO_ITEMS_PLACES where ITEM_KEY=:itemKey")
				.setParameter("itemKey", itemKey.toString()).getSingleResult())
				.intValue();
	}
}
