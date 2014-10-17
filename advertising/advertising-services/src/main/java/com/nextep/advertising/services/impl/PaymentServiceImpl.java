package com.nextep.advertising.services.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nextep.advertising.dao.AdvertisingDao;
import com.nextep.advertising.model.Payment;
import com.nextep.advertising.model.impl.PaymentImpl;
import com.nextep.cal.util.services.CalPersistenceService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class PaymentServiceImpl extends AbstractCalService implements
		CalPersistenceService {

	private AdvertisingDao dao;

	@Override
	public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
			throws CalException {
		throw new UnsupportedCalServiceException("Unsupported");
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		throw new UnsupportedCalServiceException("Unsupported");
	}

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Payment.class;
	}

	@Override
	public String getProvidedType() {
		return Payment.CAL_ID;
	}

	@Override
	public Collection<String> getSupportedInputTypes() {
		return Collections.emptyList();
	}

	@Override
	public void saveItem(CalmObject object) {
		dao.save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException("Unsupported");
	}

	@Override
	public CalmObject createTransientObject() {
		return new PaymentImpl();
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
			throws CalException {
		final List<Payment> payments = dao.getPaymentsFor(itemKey);
		final ItemsResponseImpl response = new ItemsResponseImpl();
		if (payments != null) {
			response.setItems(payments);
		}
		return response;
	}

	public void setDao(AdvertisingDao dao) {
		this.dao = dao;
	}
}
