package com.videopolis.cals.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.impl.TestModelC;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

public class TestServiceC extends AbstractCalService {

    public static final String TYPE = "CCCC";

    @Override
    protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context)
	    throws CalException {
	final ItemsResponseImpl response = new ItemsResponseImpl();
	final TestModelC modelC = buildModelC(itemKey, context,
		RequestType.FULL);
	response.addItem(modelC);
	return response;
    }

    @Override
    public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
	    CalContext context, RequestType requestType) throws CalException {
	final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();
	for (ItemKey itemKey : itemKeys) {
	    final TestModelC modelC = buildModelC(itemKey, context, requestType);
	    response.setItemsFor(itemKey, Arrays.asList(modelC));
	}

	return response;
    }

    private TestModelC buildModelC(ItemKey itemKey, CalContext context,
	    RequestType requestType) throws CalException {
	// Always returning 1 item
	final TestModelC modelC = new TestModelC(CalmFactory.createKey(TYPE,
		itemKey.getId()));
	if (requestType == RequestType.FULL
		|| requestType == TestRequestType.CODE_ONLY) {
	    modelC.setCode("C model" + modelC.getKey().getId());
	}
	if (requestType == RequestType.FULL
		|| requestType == TestRequestType.LOCALE_ONLY) {
	    modelC.setLocale(context.getLocale().toString());
	}
	return modelC;
    }

    @Override
    public ItemsResponse getItems(List<ItemKey> ids, CalContext context)
	    throws CalException {
	final ItemsResponseImpl response = new ItemsResponseImpl();
	for (ItemKey id : ids) {
	    response.addItem(buildModelC(id, context, RequestType.FULL));
	}
	return response;
    }

    @Override
    public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
	    CalContext context, int resultsPerPage, int pageNumber) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Class<? extends CalmObject> getProvidedClass() {
	return TestModelC.class;
    }

    @Override
    public String getProvidedType() {
	return TYPE;
    }

    @Override
    public Collection<String> getSupportedInputTypes() {
	return Arrays.asList("A", TYPE);
    }

}
