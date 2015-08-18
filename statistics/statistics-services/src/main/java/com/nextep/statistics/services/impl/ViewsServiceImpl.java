package com.nextep.statistics.services.impl;

import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.statistic.model.ItemView;
import com.nextep.statistic.model.impl.ItemViewImpl;
import com.nextep.statistic.model.impl.StatisticPeriod;
import com.nextep.statistic.model.impl.StatisticRequestType;
import com.nextep.statistics.dao.impl.StatisticsDao;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;

public class ViewsServiceImpl extends AbstractDaoBasedCalServiceImpl implements CalPersistenceService {

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
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context, RequestType requestType)
			throws CalException {
		final ItemsResponseImpl response = new ItemsResponseImpl();
		if (requestType instanceof StatisticRequestType) {
			// Extracting period to query from request type
			final StatisticPeriod period = ((StatisticRequestType) requestType).getPeriod();

			// Querying database for report data
			List<ItemView> items = ((StatisticsDao) getCalDao()).getReportFor(itemKey, period);

			// Building response
			response.setItems(items);
		}
		return response;
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey, ItemKey... internalItemKeys)
			throws CalException {
		return null;
	}

	@Override
	public CalmObject createTransientObject() {
		return new ItemViewImpl();
	}

}
