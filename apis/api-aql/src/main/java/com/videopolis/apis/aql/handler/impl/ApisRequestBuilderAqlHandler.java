package com.videopolis.apis.aql.handler.impl;

import java.util.Stack;

import com.videopolis.apis.aql.exception.QueryParsingException;
import com.videopolis.apis.aql.handler.ApisRequestAqlHandler;
import com.videopolis.apis.aql.helper.AqlParsingHelper;
import com.videopolis.apis.aql.model.PendingCriterion;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;

/**
 * The default implementation of {@link ApisRequestAqlHandler}, which builds a
 * standard {@link ApisRequest} during parsing
 * 
 * @author julien
 * 
 */
public class ApisRequestBuilderAqlHandler implements ApisRequestAqlHandler {

    private ApisRequest apisRequest;
    private Stack<PendingCriterion> pendingCriteria;

    @Override
    public ApisRequest getResult() {
	return apisRequest;
    }

    @Override
    public void addPagination(int itemsPerPage, int pageNumber) {
	pendingCriteria.lastElement().setItemsPerPage(itemsPerPage);
	pendingCriteria.lastElement().setPageNumber(pageNumber);
    }

    @Override
    public void addWith(String type) {
	pendingCriteria.lastElement().setItemType(type);
    }

    @Override
    public void addWithNearest(String type, double radius) {
	pendingCriteria.lastElement().setItemType(type);
	pendingCriteria.lastElement().setRadius(radius);
    }

    @Override
    public void alternateKeySelector(String itemType, String itemId) {
	try {
	    apisRequest.alternateKey(itemType, itemId);
	} catch (ApisException e) {
	    throw new QueryParsingException(e.getMessage(), e);
	}
    }

    @Override
    public void beginFor(String itemType, String itemId) {
	final PendingCriterion pendingCriterion = new PendingCriterion();
	pendingCriterion.setItemType(itemType);
	pendingCriterion.setItemId(itemId);
	pendingCriteria.push(pendingCriterion);
    }

    @Override
    public void beginGet(String type) {
	apisRequest = ApisFactory.createRequest(AqlParsingHelper
		.getCalmObjectClass(type));
	pendingCriteria = new Stack<PendingCriterion>();
    }

    @Override
    public void beginWith() {
	// Nothing to do
    }

    @Override
    public void beginWithCriterion() {
	pendingCriteria.push(new PendingCriterion());
    }

    @Override
    public void endFor() {
	// Process pending values
	try {
	    final PendingCriterion pendingCriterion = pendingCriteria.pop();
	    apisRequest.forKey(pendingCriterion.getItemType(),
		    pendingCriterion.getItemId(),
		    pendingCriterion.getItemsPerPage(),
		    pendingCriterion.getPageNumber());
	} catch (ApisException e) {
	    throw new QueryParsingException(e.getMessage(), e);
	}
    }

    @Override
    public void endGet() {
	// Nothing to do
    }

    @Override
    public void endWith() {
	// Nothing to do
    }

    @Override
    public void endWithCriterion() {
	try {
	    final PendingCriterion pendingCriterion = pendingCriteria.pop();

	    // Add to the parent
	    if (pendingCriteria.isEmpty()) {
		// No parent => Append to the request
		apisRequest.addCriterion(pendingCriterion.asWithCriterion());
	    } else {
		// Append to the parent
		pendingCriteria.lastElement().addChild(
			pendingCriterion.asWithCriterion());
	    }

	} catch (ApisException e) {
	    throw new QueryParsingException(e.getMessage(), e);
	}
    }

    @Override
    public void uniqueKeySelector(String itemId) {
	try {
	    apisRequest.uniqueKey(itemId);
	} catch (ApisException e) {
	    throw new QueryParsingException(e.getMessage(), e);
	}
    }
}
