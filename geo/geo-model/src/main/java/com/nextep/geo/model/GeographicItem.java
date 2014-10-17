package com.nextep.geo.model;

import com.nextep.cal.util.model.Named;
import com.videopolis.calm.model.CalmObject;

public interface GeographicItem extends CalmObject, Named {

	@Override
	String getName();
}
