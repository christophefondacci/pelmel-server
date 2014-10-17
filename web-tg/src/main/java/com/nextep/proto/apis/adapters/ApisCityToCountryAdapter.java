package com.nextep.proto.apis.adapters;

import java.util.Arrays;
import java.util.List;

import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.calm.model.CalmObject;

/**
 * Converts the city to its parent country. Does nothing if object is not a city
 * 
 * @author cfondacci
 * 
 */
public class ApisCityToCountryAdapter implements ApisCalmObjectAdapter {

	@Override
	public List<? extends CalmObject> adapt(CalmObject element)
			throws ApisException {
		if (element instanceof City) {
			return Arrays.asList(((City) element).getCountry());
		} else if (element instanceof GeographicItem) {
			return Arrays.asList((GeographicItem) element);
		}
		return null;
	}

}
