package com.nextep.proto.apis.adapters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.calm.model.CalmObject;

/**
 * This adapter provides the city of a place or a location.
 * 
 * @author cfondacci
 * 
 */
public class ApisPlaceLocationAdapter implements ApisCalmObjectAdapter {

	@Override
	public List<? extends CalmObject> adapt(CalmObject element)
			throws ApisException {
		if (element instanceof Place) {
			return Arrays.asList(((Place) element).getCity());
		} else if (element instanceof GeographicItem) {
			return Arrays.asList(element);
		}
		return Collections.emptyList();
	}

}
