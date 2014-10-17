package com.videopolis.apis.aql.handler.impl;

import java.util.Arrays;

import com.videopolis.apis.aql.handler.AqlHandler;

/**
 * An {@link AqlHandler} which can handle several {@link AqlHandler} at the same
 * time. Every event incoming on this handler will be dispatched on multiple
 * handlers.
 * 
 * @author julien
 * 
 */
public class MultiAqlHandler implements AqlHandler {

    private final AqlHandler[] aqlHandlers;

    /**
     * Builds a new instance.
     * 
     * @param aqlHandlers
     *            Handlers to use
     */
    public MultiAqlHandler(AqlHandler[] aqlHandlers) {
	this.aqlHandlers = Arrays.copyOf(aqlHandlers, aqlHandlers.length);
    }

    @Override
    public void addPagination(int itemsPerPage, int pageNumber) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.addPagination(itemsPerPage, pageNumber);
	}
    }

    @Override
    public void addWith(String type) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.addWith(type);
	}
    }

    @Override
    public void addWithNearest(String type, double radius) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.addWithNearest(type, radius);
	}
    }

    @Override
    public void alternateKeySelector(String itemType, String itemId) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.alternateKeySelector(itemType, itemId);
	}
    }

    @Override
    public void beginFor(String itemType, String itemId) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.beginFor(itemType, itemId);
	}
    }

    @Override
    public void beginGet(String type) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.beginGet(type);
	}
    }

    @Override
    public void beginWith() {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.beginWith();
	}
    }

    @Override
    public void beginWithCriterion() {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.beginWithCriterion();
	}
    }

    @Override
    public void endFor() {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.endFor();
	}
    }

    @Override
    public void endGet() {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.endGet();
	}
    }

    @Override
    public void endWith() {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.endWith();
	}
    }

    @Override
    public void endWithCriterion() {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.endWithCriterion();
	}
    }

    @Override
    public void uniqueKeySelector(String itemId) {
	for (final AqlHandler aqlHandler : aqlHandlers) {
	    aqlHandler.uniqueKeySelector(itemId);
	}
    }

}
