package com.nextep.statistics.services.impl;

import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.statistic.model.ItemView;
import com.nextep.statistic.model.impl.ItemViewImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ViewsServiceImpl extends AbstractDaoBasedCalServiceImpl implements
		CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return ItemView.class;
	}

	@Override
	public String getProvidedType() {
		return ItemView.CAL_TYPE;
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		return null;
	}

	@Override
	public CalmObject createTransientObject() {
		return new ItemViewImpl();
	}

}
