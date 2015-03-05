package com.nextep.proto.helpers;

import java.util.List;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

public final class GeoHelper {

	private GeoHelper() {
	}

	public static String buildShortLocalizationString(City city) {
		return buildShortLocalizationString(city, DisplayHelper.getName(city));
	}

	public static String buildShortLocalizationString(CalmObject obj,
			String htmlName) {
		final StringBuilder cityName = new StringBuilder();
		String prefix = "";
		if (htmlName != null) {
			cityName.append(htmlName);
			prefix = ", ";
		}
		if (obj instanceof Place) {
			final City city = ((Place) obj).getCity();
			cityName.append(city.getName());
			prefix = ", ";
			obj = city;
		}
		if (obj instanceof City) {
			final City city = (City) obj;
			// Building fully qualified city name
			if (city.getAdm1() != null) {
				cityName.append(prefix + DisplayHelper.getName(city.getAdm1()));
				prefix = ", ";
			}
			cityName.append(prefix + DisplayHelper.getName(city.getCountry()));
			return cityName.toString();
		} else if (obj instanceof Admin) {
			final Admin admin = (Admin) obj;
			final StringBuilder admName = new StringBuilder();
			admName.append(DisplayHelper.getName(admin));
			if (admin.getAdm1() != null) {
				admName.append(", " + DisplayHelper.getName(admin.getAdm1()));
			}
			admName.append(", " + DisplayHelper.getName(admin.getCountry()));
			return admName.toString();
		} else {
			return DisplayHelper.getName(obj);
		}
	}

	public static String buildShortPlaceLocalizationString(Place place) {
		return buildShortPlaceLocalizationString(place, place.getName());
	}

	public static String buildShortPlaceLocalizationString(Place place,
			String htmlPlace) {
		// Building fully qualified place name
		final StringBuilder placeName = new StringBuilder();
		if (htmlPlace != null) {
			placeName.append(htmlPlace + ", ");
		}
		final City city = place.getCity();
		placeName.append(buildShortLocalizationString(city));
		return placeName.toString();
	}

	public static String buildFullLocalizationString(City city) {
		final StringBuilder cityName = new StringBuilder();
		final Country country = city.getCountry();
		final GeographicItem continent = country.getContinent();
		cityName.append(DisplayHelper.getName(continent));
		cityName.append(" > " + DisplayHelper.getName(country));
		final Admin adm1 = city.getAdm1();
		if (adm1 != null) {
			cityName.append(" > " + DisplayHelper.getName(adm1));
		}
		final Admin adm2 = city.getAdm2();
		if (adm2 != null) {
			cityName.append(" > " + DisplayHelper.getName(adm2));
		}
		cityName.append(" > " + DisplayHelper.getName(city));
		return cityName.toString();
	}

	public static GeographicItem extractLocalization(CalmObject o) {
		if (o instanceof GeographicItem) {
			return (GeographicItem) o;
		} else {
			final List<? extends GeographicItem> geoItems = o
					.get(GeographicItem.class);
			if (geoItems.size() == 1) {
				return geoItems.iterator().next();
			}
		}
		return null;
	}

	public static String getPageStyle(CalmObject obj) {
		if (obj instanceof Place) {
			return ((Place) obj).getPlaceType();
		} else if (User.CAL_TYPE.equals(obj.getKey().getType())) {
			return SearchType.MEN.name().toLowerCase();
		} else if (Event.CAL_ID.equals(obj.getKey().getType())
				|| EventSeries.SERIES_CAL_ID.equals(obj.getKey().getType())) {
			return SearchType.EVENTS.name().toLowerCase();
		} else if (obj instanceof City) {
			return Constants.PAGE_STYLE_CITY;
		} else if (obj instanceof Admin) {
			return Constants.PAGE_STYLE_STATE;
		} else if (obj instanceof Country) {
			return Constants.PAGE_STYLE_COUNTRY;
		} else {
			return Constants.PAGE_STYLE_CONTINENT;
		}
	}

	public static double distanceBetween(double lat1, double lng1, double lat2,
			double lng2) {
		double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
				* Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;
	}
}
