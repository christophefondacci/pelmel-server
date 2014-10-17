package com.nextep.geo.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.dao.GeoDao;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.AlternateName;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Continent;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.GeonameItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.services.GeoService;
import com.videopolis.calm.model.CalmObject;

/**
 * This helper factorizes some logic between the {@link GeoService} and the
 * {@link PlacesServiceImpl}
 * 
 * @author cfondacci
 * 
 */
public final class GeoServiceHelper {

	private static final Log LOGGER = LogFactory.getLog(GeoServiceHelper.class);

	private GeoServiceHelper() {
	}

	/**
	 * Helper method for unitary fill of alternates. Should only be used for
	 * single fill, iterations should use the
	 * {@link GeoServiceHelper#fillAlternateMap(Map, GeographicItem)} in the
	 * iteration loop and then should make a single call to
	 * {@link GeoServiceHelper#fillAlternatesFromMap(GeoDao, Map, Locale)} to
	 * fill all alternates in one pass.
	 * 
	 * @param geoDao
	 *            the {@link GeoDao}
	 * @param geoItem
	 *            the {@link GeographicItem} to fill alternates for
	 * @param locale
	 *            the locale to use for alternates
	 */
	public static void fillAlternates(GeoDao geoDao, GeographicItem geoItem,
			Locale locale) {
		final Map<Long, List<CalmObject>> alternateIdMap = new HashMap<Long, List<CalmObject>>();
		fillAlternateMap(alternateIdMap, geoItem);
		fillAlternatesFromMap(geoDao, alternateIdMap, locale);
	}

	/**
	 * Fills a map of all needed alternates. This method allows preparation of
	 * the structure that can batch query alternates for several elements in one
	 * db roundtrip. Alternates will be retrieved for all fragments of the geo
	 * item. For example, if used on a city, it will fill alternates for region,
	 * state, country, continent.
	 * 
	 * @param alternateIdMap
	 *            the structure to fill
	 * @param geoItem
	 *            the {@link GeographicItem} to get alternates for
	 */
	public static void fillAlternateMap(
			Map<Long, List<CalmObject>> alternateIdMap, GeographicItem geoItem) {
		CalmObject obj = null;
		if (geoItem instanceof Place) {
			obj = ((Place) geoItem).getCity();
		} else {
			obj = geoItem;
		}
		if (obj instanceof GeonameItem) {
			putListItem(alternateIdMap, obj, ((GeonameItem) obj).getGeonameId());
		}
		// Registering every component of our object
		if (obj instanceof City) {
			final City city = (City) obj;
			final Admin adm1 = city.getAdm1();
			final Admin adm2 = city.getAdm2();
			final Country country = city.getCountry();
			final Continent continent = city.getCountry().getContinent();
			if (adm1 != null) {
				putListItem(alternateIdMap, adm1, adm1.getGeonameId());
			}
			if (adm2 != null) {
				putListItem(alternateIdMap, adm2, adm2.getGeonameId());
			}
			putListItem(alternateIdMap, country, country.getGeonameId());
			putListItem(alternateIdMap, continent, continent.getGeonameId());
		} else if (obj instanceof Admin) {
			final Admin admin = (Admin) obj;
			final Admin parent = admin.getAdm1();
			if (parent != null) {
				putListItem(alternateIdMap, parent, parent.getGeonameId());
			}
			final Country country = admin.getCountry();
			putListItem(alternateIdMap, country, country.getGeonameId());
			final Continent continent = country.getContinent();
			putListItem(alternateIdMap, continent, continent.getGeonameId());
		} else if (obj instanceof Country) {
			final Country country = (Country) obj;
			final Continent continent = country.getContinent();
			putListItem(alternateIdMap, continent, continent.getGeonameId());
		}
	}

	public static void fillAlternatesFromMap(GeoDao dao,
			Map<Long, List<CalmObject>> alternateIdMap, Locale locale) {
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Dumping alternates map : ");
			for (Long l : alternateIdMap.keySet()) {
				final List<CalmObject> list = alternateIdMap.get(l);
				final StringBuilder buf = new StringBuilder();
				String separator = "";
				for (CalmObject o : list) {
					buf.append(separator);
					separator = ",";
					if (o != null) {
						buf.append(o.getKey().toString());
					} else {
						buf.append("null");
					}
				}
				LOGGER.trace("  -> " + l + " -> " + buf.toString());
			}
		}
		if (!alternateIdMap.isEmpty()) {
			// Looking for alternate in our requested locale
			final Map<Long, List<AlternateName>> alternateMap = dao
					.getAlternateNamesFor(alternateIdMap.keySet(), locale);
			// Injecting found alternates into objects
			for (Long geonameId : alternateIdMap.keySet()) {
				final List<CalmObject> parentObjects = alternateIdMap
						.get(geonameId);
				final List<AlternateName> alternates = alternateMap
						.get(geonameId);
				if (alternates != null && !alternates.isEmpty()) {
					for (CalmObject parentObj : parentObjects) {
						parentObj.addAll(alternates);
					}
				}
			}
		}
	}

	private static <T> void putListItem(Map<Long, List<T>> map, T item, Long key) {
		if (key != null) {
			List<T> list = map.get(key);
			if (list == null) {
				list = new ArrayList<T>();
				map.put(key, list);
			}
			list.add(item);
		}
	}
}
