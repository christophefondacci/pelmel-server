package com.videopolis.cals.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.impl.TestModelA;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;
import com.videopolis.cals.service.base.AbstractCalService;

/**
 * Test service A. Only provide elements through their primary key<br>
 * Implements a "dummy" service provider. Only here to provide a test case for
 * the APIS aggregation layer.
 * 
 * @author Christophe Fondacci
 * 
 */
public class TestServiceA extends AbstractCalService {

    public static final String TYPE = "AAAA";
    public static final String TYPE_ALTERNATE = "Alternate";

    @Override
    protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context) {
	// No response for anyone
	ItemsResponseImpl response = new ItemsResponseImpl();

	return response;
    }

    @Override
    public ItemsResponse getItems(List<ItemKey> ids, CalContext context) {
	ItemsResponseImpl response = new ItemsResponseImpl();
	// Always returning a model
	for (ItemKey key : ids) {
	    final TestModelA obj = new TestModelA(key);
	    if (key.getType().equals(TYPE)) {
		obj.setInfo("This is a A model with key: " + key.getId());
		response.addItem(obj);
	    } else if (key.getType().equals(TYPE_ALTERNATE)) {
		obj.setInfo("This is a A model with key: " + key.getId()
			+ ", coming from alternate");
		response.addItem(obj);
	    }
	}
	return response;
    }

    @Override
    public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
	    CalContext context, int resultsPerPage, int pageNumber) {
	PaginatedItemsResponse response = new PaginatedItemsResponseImpl(
		resultsPerPage, pageNumber);
	return response;
    }

    @Override
    public Class<? extends CalmObject> getProvidedClass() {
	return TestModelA.class;
    }

    @Override
    public String getProvidedType() {
	return TYPE;
    }

    @Override
    public Collection<String> getSupportedInputTypes() {
	return Arrays.asList(TYPE, TYPE_ALTERNATE);
    }

}
