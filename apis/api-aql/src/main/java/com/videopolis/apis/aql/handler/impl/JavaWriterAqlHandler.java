package com.videopolis.apis.aql.handler.impl;

import com.videopolis.apis.aql.handler.StringAqlHandler;
import com.videopolis.apis.aql.helper.AqlParsingHelper;

/**
 * A {@link StringAqlHandler} which rewrites the AQL query in Java. It does not
 * work yet.
 * 
 * TODO: Finish this!
 * 
 * @author julien
 * 
 */
public class JavaWriterAqlHandler implements StringAqlHandler {

    private StringBuilder java;

    @Override
    public String getResult() {
	return java == null ? "(no code yet)" : java.toString();
    }

    @Override
    public void addPagination(int itemsPerPage, int pageNumber) {
	// TODO Auto-generated method stub

    }

    @Override
    public void addWith(String type) {
	// TODO Auto-generated method stub

    }

    @Override
    public void addWithNearest(String type, double radius) {
	// TODO Auto-generated method stub

    }

    @Override
    public void alternateKeySelector(String itemType, String itemId) {
	java.append(".alternateKey(\"").append(itemType).append("\", \"")
		.append(itemId).append("\")");
    }

    @Override
    public void beginFor(String itemType, String itemId) {
	// TODO Auto-generated method stub

    }

    @Override
    public void beginGet(String type) {
	java = new StringBuilder("ApisFactory.createRequest(").append(
		asClassName(type)).append(")");
    }

    @Override
    public void beginWith() {
	// TODO Auto-generated method stub

    }

    @Override
    public void beginWithCriterion() {
	// TODO Auto-generated method stub

    }

    @Override
    public void endFor() {
	// TODO Auto-generated method stub

    }

    @Override
    public void endGet() {
	// TODO Auto-generated method stub

    }

    @Override
    public void endWith() {
	// TODO Auto-generated method stub

    }

    @Override
    public void endWithCriterion() {
	// TODO Auto-generated method stub

    }

    @Override
    public void uniqueKeySelector(String itemId) {
	java.append(".uniqueKey(\"").append(itemId).append("\")");
    }

    private String asClassName(String type) {
	final String canonicalName = AqlParsingHelper.getCalmObjectClass(type)
		.getCanonicalName();
	return canonicalName.substring(canonicalName.lastIndexOf('.'));
    }

    @Override
    public String toString() {
	return getResult();
    }
}
