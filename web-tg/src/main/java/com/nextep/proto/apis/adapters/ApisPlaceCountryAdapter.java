package com.nextep.proto.apis.adapters;

import java.util.Arrays;
import java.util.List;

import com.nextep.geo.model.Place;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.calm.model.CalmObject;

/**
 * Provides the country of a given place.
 * 
 * @author cfondacci
 * 
 */
public class ApisPlaceCountryAdapter implements ApisCalmObjectAdapter {

	@Override
	public List<? extends CalmObject> adapt(CalmObject element)
			throws ApisException {
		if (element instanceof Place) {
			return Arrays.asList(((Place) element).getCity().getCountry());
		}
		return null;
	}

}
