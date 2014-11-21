package com.nextep.proto.apis.adapters;

import java.util.Arrays;
import java.util.List;

import com.nextep.geo.model.City;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Selects the city with the highest population from a range of cities.
 * Typically used to locate the biggest city in a nearby search.
 * 
 * @author cfondacci
 * 
 */
public class ApisCityLocalizerAdapter implements ApisCustomAdapter {

	private static final int MAX_RADIUS = 50;

	@Override
	public List<ItemKey> adapt(ApisContext context, CalmObject... parents) {
		City selectedCity = null;
		int selectedPopulation = 0;
		City defaultCity = null;
		double defaultDistance = Double.MAX_VALUE;
		for (CalmObject parent : parents) {
			if (parent instanceof City) {
				final City city = (City) parent;
				// Retrieving distance from current position
				final SearchStatistic stat = context.getApiResponse()
						.getStatistic(city.getKey(), SearchStatistic.DISTANCE);
				final double cityDistance = stat.getNumericValue()
						.doubleValue();

				// Normal use case, cities within small radius
				if (cityDistance <= MAX_RADIUS) {
					if (city.getPopulation() > selectedPopulation * 20) {
						selectedCity = city;
						selectedPopulation = city.getPopulation();
					}
				} else {
					// Use-case when user is far from any city
					if (cityDistance < defaultDistance) {
						defaultCity = city;
						defaultDistance = cityDistance;
					}
				}
			}
		}
		if (selectedCity != null) {
			return Arrays.asList(selectedCity.getKey());
		} else {
			return Arrays.asList(defaultCity.getKey());
		}
	}
}
