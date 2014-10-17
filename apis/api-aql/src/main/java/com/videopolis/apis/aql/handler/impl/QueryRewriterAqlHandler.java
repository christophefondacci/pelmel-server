package com.videopolis.apis.aql.handler.impl;

import com.videopolis.apis.aql.handler.StringAqlHandler;
import com.videopolis.apis.aql.helper.AqlParsingHelper;

/**
 * A {@link StringAqlHandler} which will rewrite the input AQL query to a
 * canonical AQL query.
 * 
 * @author julien
 * 
 */
public class QueryRewriterAqlHandler implements StringAqlHandler {

    private StringBuilder request;

    private boolean prependComma = false;

    @Override
    public void addPagination(int itemsPerPage, int pageNumber) {
	request.append(" page ").append(pageNumber).append(" size ")
		.append(itemsPerPage);
    }

    @Override
    public void addWith(String type) {
	request.append(asCanonicalType(type));
    }

    @Override
    public void addWithNearest(String type, double radius) {
	request.append("nearest ").append(asCanonicalType(type))
		.append(" radius ").append(radius);
    }

    @Override
    public void alternateKeySelector(String itemType, String itemId) {
	request.append(" alternate key ").append(itemType).append(itemId);
    }

    @Override
    public void beginFor(String itemType, String itemId) {
	request.append(" for ").append(itemType).append(itemId);
    }

    @Override
    public void beginGet(String type) {
	request = new StringBuilder().append("get ").append(
		asCanonicalType(type));
    }

    @Override
    public void beginWith() {
	request.append(" with ");
	prependComma = false;
    }

    @Override
    public void endFor() {
	// Nothing to do
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
    public void uniqueKeySelector(String itemId) {
	request.append(" unique key ").append(itemId);
    }

    @Override
    public void endWithCriterion() {
	request.append(")");
    }

    @Override
    public void beginWithCriterion() {
	if (prependComma) {
	    request.append(", ");
	}
	prependComma = true;
	request.append("(");
    }

    /**
     * Converts a type name to a canonical type name, that is the fully
     * qualified {@code CalmObject} class name.
     * 
     * @param type
     *            Type
     * @return canonical type name
     */
    private String asCanonicalType(String type) {
	return AqlParsingHelper.getCalmObjectClass(type).getCanonicalName();
    }

    @Override
    public String getResult() {
	return request == null ? "(no request yet)" : request.toString();
    }

    @Override
    public String toString() {
	return getResult();
    }
}
