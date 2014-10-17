package com.nextep.ratings.services.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.ratings.dao.RatingsDao;
import com.nextep.ratings.model.Rating;
import com.nextep.ratings.model.impl.RatingImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class RatingsServiceImpl extends AbstractCalService implements
		CalPersistenceService {

	private RatingsDao dao;

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Rating.class;
	}

	@Override
	public String getProvidedType() {
		return Rating.CAL_TYPE;
	}

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Arrays.asList(Rating.CAL_TYPE);
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {

		// Fetching data
		final List<Rating> ratings = dao.getTotalRatingsFor(itemKeys);

		// Preparing response structure
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();

		// Filling response
		for (Rating rating : ratings) {
			response.setItemsFor(rating.getRatedItemKey(),
					Arrays.asList(rating));
		}
		return response;
	}

	public void setDao(RatingsDao dao) {
		this.dao = dao;
	}

	@Override
	public CalmObject createTransientObject() {
		return new RatingImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		dao.save((Rating) object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		// TODO Auto-generated method stub
		return null;
	}
}
