package com.nextep.proto.apis.model.impl;

import com.nextep.geo.model.GeographicItem;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.apis.adapters.ApisCityLocalizerAdapter;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This helper factorizes APIS criterion construction methods for common
 * localization needs.
 * 
 * @author cfondacci
 * 
 */
public final class ApisLocalizationHelper {

	public final static String APIS_ALIAS_CITY_NEARBY = "city";
	private static ApisCustomAdapter cityLocalizerAdapter = new ApisCityLocalizerAdapter();

	private ApisLocalizationHelper() {
	}

	/**
	 * Provides an APIS criterion which will provide the nearest city for a
	 * given lat / lng
	 * 
	 * @param lat
	 *            latitude
	 * @param lng
	 *            longitude
	 * @param cityRadius
	 *            the radius of the search
	 * @return the city, within the radius, which has the most population,
	 *         published as a root object under the APIS_ALIAS_CITY_NEARBY alias
	 */
	public static ApisCriterion buildNearestCityCriterion(double lat,
			double lng, double cityRadius) throws ApisException {
		return (ApisCriterion) SearchRestriction.searchNear(
				GeographicItem.class, SearchScope.CITY, lat, lng, cityRadius,
				5, 0).addCriterion(
				(ApisCriterion) SearchRestriction.customAdapt(
						cityLocalizerAdapter, APIS_ALIAS_CITY_NEARBY).with(
						Media.class, MediaRequestTypes.THUMB));
	}
}
