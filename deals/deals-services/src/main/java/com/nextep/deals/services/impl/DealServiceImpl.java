package com.nextep.deals.services.impl;

import java.util.List;
import java.util.Map;

import com.fgp.deals.model.Deal;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.deals.dao.DealDao;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class DealServiceImpl extends AbstractDaoBasedCalServiceImpl {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Deal.class;
	}

	@Override
	public String getProvidedType() {
		return Deal.CAL_ID;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys, CalContext context) throws CalException {

		// Querying DAO for activities
		final Map<ItemKey, List<Deal>> activitiesKeyMap = ((DealDao) getCalDao()).getDealsFor(itemKeys);

		// Preparing response
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();

		// Filling response
		for (ItemKey key : activitiesKeyMap.keySet()) {
			final List<Deal> activities = activitiesKeyMap.get(key);
			response.setItemsFor(key, activities);
		}

		// Returning
		return response;
	}
}
