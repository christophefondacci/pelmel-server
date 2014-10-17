package com.videopolis.apis.concurrent.impl;

import java.util.Arrays;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A {@link Task} which can invoke a
 * {@link CalService#getItemsRangeFor(java.util.List, com.videopolis.cals.model.CalContext, int, int)}
 * method on a given {@link CalService}.
 * 
 * @author Christophe Fondacci
 * 
 */
public class GetCalItemsRangeForTask extends AbstractApisTask {

    private CalService service;
    private ItemKey[] parentItemKeys;
    private ApisContext context;
    private PaginationSettings paginationWindow;

    /**
     * Creates a new {@link Task} on the given {@link CalService} able to invoke
     * a getCalItemsFor with the specified arguments.
     * 
     * @param service
     *            {@link CalService} to invoke
     * @param context
     *            current {@link ApisContext}
     * @param parentItems
     *            arguments of the getItemsFor() call
     */
    public GetCalItemsRangeForTask(CalService service, ApisContext context,
	    PaginationSettings paginationWindow, ItemKey... parentItems) {
	this.service = service;
	this.parentItemKeys = parentItems;
	this.context = context;
	this.paginationWindow = paginationWindow;
    }

    @Override
    public ItemsResponse doExecute(TaskExecutionContext taskExecutionContext)
	    throws TaskExecutionException {
	final int itemsByPage = paginationWindow.getItemsByPage();
	final int pageNumber = paginationWindow.getPageOffset();
	try {
	    return service.getItemsRangeFor(Arrays.asList(parentItemKeys),
		    context.getCalContext(), itemsByPage, pageNumber);
	} catch (CalException e) {
	    throw new TaskExecutionException(
		    "Problems while calling CalService.getItemsRangeFor("
			    + Arrays.toString(parentItemKeys)
			    + ","
			    + (paginationWindow == null ? "Null pagination"
				    : paginationWindow.toString()) + ","
			    + context.toString() + "): " + e.getMessage(), e);
	}
    }

    @Override
    public String toString() {
	return "getCalItemsRangeFor;"
		+ (service == null ? "null" : ApisRegistry
			.getTypeForCalService(service))
		+ ";"
		+ (paginationWindow == null ? "Null pagination"
			: paginationWindow.toString()) + ";"
		+ Arrays.toString(parentItemKeys);
    }
}
