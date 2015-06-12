package com.nextep.advertising.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nextep.advertising.dao.AdvertisingDao;
import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.advertising.model.impl.AdvertisingBoosterImpl;
import com.nextep.advertising.model.impl.AdvertisingRequestTypes;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class AdvertisingServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return AdvertisingBooster.class;
	}

	@Override
	public String getProvidedType() {
		return AdvertisingBooster.CAL_ID;
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException(
				"AdvertisingService.setItemFor not supported");
	}

	@Override
	public CalmObject createTransientObject() {
		return new AdvertisingBoosterImpl();
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {
		Map<ItemKey, List<AdvertisingBooster>> adMap = Collections.emptyMap();
		if (requestType == null) {
			adMap = ((AdvertisingDao) getCalDao()).getBoostersFor(itemKeys);
		} else if (AdvertisingRequestTypes.USER_BOOSTERS.equals(requestType)) {
			adMap = ((AdvertisingDao) getCalDao())
					.getBoostersForUsers(itemKeys);
		}
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (ItemKey key : adMap.keySet()) {
			response.setItemsFor(key, adMap.get(key));
		}
		return response;
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType requestType,
			RequestSettings requestSettings) throws CalException {
		throw new UnsupportedCalServiceException(
				"Unsupported AdvertisingService.list");
	}
}
