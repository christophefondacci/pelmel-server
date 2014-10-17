package com.nextep.statistics.services.impl;

import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.statistic.model.Statistic;
import com.nextep.statistic.model.impl.StatisticImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.exception.UnsupportedCalServiceException;

public class StatisticsServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Statistic.class;
	}

	@Override
	public String getProvidedType() {
		return Statistic.CAL_TYPE;
	}

	@Override
	public CalmObject createTransientObject() {
		return new StatisticImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException(
				"StatisticsServiceImpl does not support setItemFor");
	}
}
