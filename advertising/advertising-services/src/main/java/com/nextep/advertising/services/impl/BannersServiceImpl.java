package com.nextep.advertising.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.nextep.advertising.dao.AdvertisingDao;
import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.AdvertisingBannerRequestType;
import com.nextep.advertising.model.Payment;
import com.nextep.advertising.model.impl.AdvertisingBannerImpl;
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
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;

public class BannersServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {
		final List<AdvertisingBanner> banners = ((AdvertisingDao) getCalDao())
				.getBanners(ids);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		response.setItems(banners);
		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		throw new UnsupportedCalServiceException("Unsupported");
	}

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return AdvertisingBanner.class;
	}

	@Override
	public String getProvidedType() {
		return AdvertisingBanner.CAL_ID;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Collections.emptyList();
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException("Unsupported");
	}

	@Override
	public CalmObject createTransientObject() {
		return new AdvertisingBannerImpl();
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		final List<Payment> payments = ((AdvertisingDao) getCalDao())
				.getPaymentsFor(itemKey);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		if (payments != null) {
			response.setItems(payments);
		}
		return response;
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {
		return getItemsFor(itemKeys, context, null);
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context, RequestType requestType) throws CalException {
		if (AdvertisingRequestTypes.USER_BANNERS.equals(requestType)) {
			if (itemKeys.size() == 1) {

				// Extracting single user item key
				final ItemKey userKey = itemKeys.iterator().next();

				// Querying through DAO
				List<AdvertisingBanner> banners = ((AdvertisingDao) getCalDao())
						.getBannersForUser(userKey);

				// Building our multi key response for 1 entry
				final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
				response.setItemsFor(userKey, banners);
				return response;
			} else {
				throw new UnsupportedCalServiceException(
						"Cannot query banners for a user and ask for more than one user");
			}
		} else {
			String searchType = null;

			// Extracting any query parameter
			if (requestType instanceof AdvertisingBannerRequestType) {
				searchType = ((AdvertisingBannerRequestType) requestType)
						.getSearchType();
			}

			// Querying content from database
			final Map<ItemKey, List<AdvertisingBanner>> bannersMap = ((AdvertisingDao) getCalDao())
					.getBannersFor(itemKeys, searchType, context.getLocale());

			// Building our structured response
			final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
			for (ItemKey key : bannersMap.keySet()) {
				final List<AdvertisingBanner> banners = bannersMap.get(key);
				response.setItemsFor(key, banners);
			}

			// We're done
			return response;
		}
	}

}
