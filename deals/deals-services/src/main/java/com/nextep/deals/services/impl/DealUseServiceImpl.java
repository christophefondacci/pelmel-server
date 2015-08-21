package com.nextep.deals.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fgp.deals.model.DealUse;
import com.fgp.deals.model.impl.DealRequestTypes;
import com.fgp.deals.model.impl.DealUseImpl;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.deals.dao.DealDao;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class DealUseServiceImpl extends AbstractCalService implements CalPersistenceService {

	private DealDao dealDao;

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context) throws CalException {
		final List<DealUse> dealUses = dealDao.getDealUses(ids);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(dealUses);

		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey, CalContext context, int resultsPerPage,
			int pageNumber) throws CalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return DealUse.class;
	}

	@Override
	public String getProvidedType() {
		return DealUse.CAL_ID;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context) throws CalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys, CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys, CalContext context, RequestType requestType)
			throws CalException {
		final Map<ItemKey, List<DealUse>> dealUsesMap = dealDao.getDealUsesFor(itemKeys,
				requestType == DealRequestTypes.DAILY_DEAL);
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (ItemKey key : dealUsesMap.keySet()) {
			response.setItemsFor(key, dealUsesMap.get(key));
		}
		return response;
	}

	public void setDealDao(DealDao dealDao) {
		this.dealDao = dealDao;
	}

	@Override
	public void saveItem(CalmObject object) {
		dealDao.save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey, ItemKey... internalItemKeys)
			throws CalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CalmObject createTransientObject() {
		return new DealUseImpl();
	}
}
