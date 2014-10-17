package com.videopolis.cals.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ITestModelB;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.impl.TestModelB;
import com.videopolis.cals.factory.RequestSettingsFactory;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.PaginationRequestSettings;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

/**
 * This service will provide elements from keys
 * 
 * @author Christophe Fondacci
 * 
 */
public class TestServiceB extends AbstractCalService {

    public static final String TYPE = "BBBB";

    public static final String DEFAULT_SORT = "default";

    private static final int ITEMS_FOR_COUNT = 30;
    private static final int ITEM_ID_OFFSET = 100;

    @Override
    protected ItemsResponse getItemsFor(final ItemKey itemKey,
	    final CalContext context) throws CalException {
	final ItemsResponseImpl response = new ItemsResponseImpl();

	for (int i = 0; i < ITEMS_FOR_COUNT; i++) {
	    final TestModelB obj = new TestModelB(CalmFactory.createKey(TYPE,
		    (long) i));
	    obj.setName("ModelB #" + i);
	    response.addItem(obj);
	}
	return response;
    }

    @Override
    public ItemsResponse getItems(final List<ItemKey> ids,
	    final CalContext context) {
	final ItemsResponseImpl response = new ItemsResponseImpl();
	int i = ITEM_ID_OFFSET;
	for (final ItemKey key : ids) {
	    final TestModelB obj = new TestModelB(key);
	    obj.setName("ModelB #" + i++);
	    response.addItem(obj);
	}
	return response;
    }

    @Override
    public PaginatedItemsResponse getPaginatedItemsFor(final ItemKey itemKey,
	    final CalContext context, final int resultsPerPage,
	    final int pageNumber) throws CalException {
	return getPaginatedItemsFor(itemKey, context,
		RequestSettingsFactory.createPaginationRequestSettings(
			DEFAULT_SORT, RequestSettings.ASCENDING_ORDER,
			resultsPerPage, pageNumber));
    }

    @Override
    protected PaginatedItemsResponse getPaginatedItemsFor(
	    final ItemKey itemKey, final CalContext context,
	    final PaginationRequestSettings requestSettings)
	    throws CalException {

	final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
		requestSettings.getResultsPerPage(),
		requestSettings.getPageNumber());

	if (!requestSettings.getSortParameters().isEmpty()) {
	    switch (requestSettings.getSortParameters().iterator().next()
		    .getOrder()) {
	    case ASCENDING: {
		final int start = requestSettings.getResultsPerPage()
			* requestSettings.getPageNumber();
		for (int i = start; i < start
			+ requestSettings.getResultsPerPage(); i++) {
		    response.addItem(createTestModelB(i));
		}
		break;
	    }

	    case DESCENDING: {
		final int start = requestSettings.getResultsPerPage()
			* (requestSettings.getPageNumber() + 1) - 1;
		for (int i = start; i >= requestSettings.getResultsPerPage()
			* requestSettings.getPageNumber(); i--) {
		    response.addItem(createTestModelB(i));
		}
		break;
	    }

	    default:
		throw new CalException("Unkown order "
			+ requestSettings.getSortOrder());
	    }
	}
	// Yes, there's really a LOT of B's
	response.setItemCount(Integer.MAX_VALUE);

	return response;
    }

    private TestModelB createTestModelB(final int number) throws CalException {
	final TestModelB obj = new TestModelB(CalmFactory.createKey(TYPE,
		(long) number));
	obj.setName("ModelB #" + number);
	return obj;
    }

    @Override
    public Class<? extends CalmObject> getProvidedClass() {
	return ITestModelB.class;
    }

    @Override
    public String getProvidedType() {
	return TYPE;
    }

    @Override
    public Collection<String> getSupportedInputTypes() {
	return Arrays.asList(TYPE);
    }

}
