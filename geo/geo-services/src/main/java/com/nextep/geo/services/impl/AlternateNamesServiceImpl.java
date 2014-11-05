package com.nextep.geo.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nextep.geo.dao.GeoDao;
import com.nextep.geo.model.AlternateName;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class AlternateNamesServiceImpl extends AbstractCalService {

	private GeoDao geoDao;

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {
		throw new UnsupportedCalServiceException(
				"alternateNamesService.getItems() not supported");
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		throw new UnsupportedCalServiceException(
				"alternateNamesService.getPaginatedItemsFor() not supported");
	}

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return AlternateName.class;
	}

	@Override
	public String getProvidedType() {
		return AlternateName.CAL_TYPE;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		throw new UnsupportedCalServiceException(
				"alternateNamesService.getItemsFor(ItemKey) not supported");
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		// Unwrapping itemKeys and storing id to itemkey connection in a map
		final Map<Long, ItemKey> idKeyMap = new HashMap<Long, ItemKey>();
		final Collection<Long> ids = new ArrayList<Long>();
		for (ItemKey itemKey : itemKeys) {
			idKeyMap.put(itemKey.getNumericId(), itemKey);
			ids.add(itemKey.getNumericId());
		}
		// Getting alternates
		Map<Long, List<AlternateName>> alternatesIdMap = geoDao
				.getAlternateNamesFor(ids, context.getLocale());

		// Building back response by hashing back into ItemKey
		MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
		for (Long id : alternatesIdMap.keySet()) {
			final ItemKey itemKey = idKeyMap.get(id);
			final List<AlternateName> alternates = alternatesIdMap.get(id);

			response.setItemsFor(itemKey, alternates);
		}
		return response;
	}

	public void setGeoDao(GeoDao geoDao) {
		this.geoDao = geoDao;
	}
}
