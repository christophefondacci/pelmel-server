package com.nextep.properties.services.impl;

import java.util.Collections;
import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.properties.dao.PropertiesDao;
import com.nextep.properties.model.Property;
import com.nextep.properties.model.impl.PropertyImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.exception.UnsupportedCalServiceException;

public class PropertiesServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Property.class;
	}

	@Override
	public String getProvidedType() {
		return Property.CAL_TYPE;
	}

	@Override
	public CalmObject createTransientObject() {
		return new PropertyImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		if (internalItemKeys == null || internalItemKeys.length == 0) {
			((PropertiesDao) getCalDao()).clearProperties(contributedItemKey);
			return Collections.emptyList();
		} else {
			throw new UnsupportedCalServiceException(
					"Cannot bind properties, save a property with a predefined parent item key instead");
		}
	}

}
