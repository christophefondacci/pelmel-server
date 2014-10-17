package com.nextep.descriptions.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.nextep.descriptions.dao.DescriptionDao;
import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.descriptions.model.MutableDescription;
import com.nextep.descriptions.model.impl.DescriptionImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class DescriptionsServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	private final int modificationSourceId = 1000;

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Description.class;
	}

	@Override
	public String getProvidedType() {
		return Description.CAL_TYPE;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {
		final Map<ItemKey, List<Description>> descMap = ((DescriptionDao) getCalDao())
				.getDescriptionsFor(itemKeys, context.getLocale(),
						DescriptionRequestType.SINGLE_DESC.equals(requestType));
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (ItemKey key : descMap.keySet()) {
			response.setItemsFor(key, descMap.get(key));
		}
		return response;
	}

	@Override
	public CalmObject createTransientObject() {
		final MutableDescription desc = new DescriptionImpl();
		desc.setSourceId(modificationSourceId);
		return desc;
	}

	@Override
	public void saveItem(CalmObject object) {
		// Since we save a description, we assign it to our own source ID so it
		// will not get affected by further data pushes
		if (object instanceof MutableDescription) {
			((MutableDescription) object).setSourceId(modificationSourceId);
		}
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		if (internalItemKeys == null || internalItemKeys.length == 0) {
			((DescriptionDao) getCalDao())
					.clearDescriptions(contributedItemKey);
		} else {
			// Warning: this setItemFor implementation is not doing what it
			// should as we use it to specify keys to remove
			((DescriptionDao) getCalDao()).clearDescriptions(
					contributedItemKey, internalItemKeys);
			// throw new UnsupportedCalServiceException(
			// "setItemFor not supported for descriptions");
		}
		return Collections.emptyList();
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType requestType,
			RequestSettings requestSettings) throws CalException {
		if (DescriptionRequestType.SORT_BY_LENGTH_DESC.equals(requestType)) {
			final DescriptionDao dao = (DescriptionDao) getCalDao();

			// Computing number of descriptions to fetch
			int maxValues = -1;
			int startOffset = 0;
			if (requestSettings instanceof PaginationRequestSettings) {
				final PaginationRequestSettings pagination = (PaginationRequestSettings) requestSettings;
				maxValues = pagination.getResultsPerPage();
				startOffset = pagination.getResultsPerPage()
						* pagination.getPageNumber();
			}

			// Fetching descriptions from DAO
			final List<Description> descriptions = dao
					.listDescriptionsByLength(false, maxValues, startOffset);

			// Building response
			final ItemsResponseImpl response = new ItemsResponseImpl();
			response.setItems(descriptions);

			// Returning response
			return response;
		}
		return super.listItems(context, requestType, requestSettings);
	}
}
