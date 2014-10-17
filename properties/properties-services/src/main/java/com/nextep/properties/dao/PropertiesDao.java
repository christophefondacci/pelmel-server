package com.nextep.properties.dao;

import com.nextep.cal.util.model.CalDao;
import com.nextep.properties.model.Property;
import com.videopolis.calm.model.ItemKey;

public interface PropertiesDao extends CalDao<Property> {

	/**
	 * Clears all properties from the specified parent key
	 * 
	 * @param parentKey
	 *            clears all properties defined for the given {@link ItemKey}.
	 *            This method will typically be called before re-injecting all
	 *            properties
	 */
	void clearProperties(ItemKey parentKey);
}
