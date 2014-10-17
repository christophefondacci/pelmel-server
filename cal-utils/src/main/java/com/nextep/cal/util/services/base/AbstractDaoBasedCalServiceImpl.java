package com.nextep.cal.util.services.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nextep.cal.util.model.CalDao;
import com.nextep.cal.util.model.CalDaoExt;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

/**
 * A generic CAL service based on a CAL DAO providing CAL information from
 * database.
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractDaoBasedCalServiceImpl extends AbstractCalService {

	private CalDao<?> calDao;

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {

		final ItemsResponseImpl response = new ItemsResponseImpl();
		final List<Long> idList = new ArrayList<Long>(ids.size());
		for (ItemKey key : ids) {
			idList.add(key.getNumericId());
		}
		final List<? extends CalmObject> items = calDao.getByIds(idList);
		if (items != null) {

			response.setItems(reorderCalmObjects(ids, items));
		}
		return response;
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
		return Collections.emptyList();
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		final ItemsResponseImpl response = new ItemsResponseImpl();
		final List<? extends CalmObject> mediaList = calDao
				.getItemsFor(itemKey);
		response.setItems(mediaList);

		return response;
	}

	public void setCalDao(CalDao<?> calDao) {
		this.calDao = calDao;
	}

	protected CalDao<?> getCalDao() {
		return calDao;
	}

	@Override
	public ItemsResponse listItems(CalContext context, RequestType requestType,
			RequestSettings requestSettings) throws CalException {
		ItemsResponse response = null;
		if (requestSettings instanceof PaginationRequestSettings
				&& calDao instanceof CalDaoExt) {

			// Casting our extended DAO
			final CalDaoExt<? extends CalmObject> extDao = (CalDaoExt<? extends CalmObject>) calDao;

			// Extracting pagination settings
			final PaginationRequestSettings pagination = (PaginationRequestSettings) requestSettings;
			final int page = pagination.getPageNumber();
			final int pageSize = pagination.getResultsPerPage();

			// Querying
			final List<? extends CalmObject> allItems = extDao.listItems(
					requestType, pageSize, page * pageSize);
			final int itemsCount = extDao.getCount();

			// Preparing resulting current pagination info
			int pages = itemsCount / pageSize;
			int pagesMod = itemsCount % pageSize;

			// Preparing our resulting structure
			response = new PaginatedItemsResponseImpl(pageSize, page);
			((PaginatedItemsResponseImpl) response).setPageCount(pages
					+ (pagesMod > 0 ? 1 : 0));
			((PaginatedItemsResponseImpl) response).setItems(allItems);
			((PaginatedItemsResponseImpl) response).setItemCount(itemsCount);
		} else {
			response = new ItemsResponseImpl();
			final List<? extends CalmObject> allItems = calDao.listItems(
					requestType, requestSettings);
			if (allItems != null) {
				((ItemsResponseImpl) response).setItems(allItems);
			}
		}
		return response;
	}
}
