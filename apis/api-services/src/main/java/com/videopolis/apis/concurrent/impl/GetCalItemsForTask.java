package com.videopolis.apis.concurrent.impl;

import java.util.Arrays;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A {@link Task} which can invoke a
 * {@link CalService#getItemsFor(java.util.List, com.videopolis.cals.model.CalContext)}
 * method on a given {@link CalService}.
 * 
 * @author Christophe Fondacci
 * 
 */
public class GetCalItemsForTask extends AbstractApisTask {

    private CalService service;
    private ItemKey[] parentItemKeys;
    private ApisContext context;

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
    public GetCalItemsForTask(CalService service, ApisContext context,
	    ItemKey... parentItems) {
	this.service = service;
	this.parentItemKeys = parentItems;
	this.context = context;
    }

    @Override
    public ItemsResponse doExecute(TaskExecutionContext taskExecutionContext)
	    throws TaskExecutionException {
	try {
	    return service.getItemsFor(Arrays.asList(parentItemKeys), context
		    .getCalContext());
	} catch (CalException e) {
	    throw new TaskExecutionException(
		    "Problems while calling CalService.getItemsFor("
			    + Arrays.toString(parentItemKeys) + ","
			    + context.toString() + "): " + e.getMessage(), e);
	}
    }

    @Override
    public String toString() {
	return "getCalItemsFor;"
		+ (service == null ? "null" : ApisRegistry
			.getTypeForCalService(service)) + ";"
		+ Arrays.toString(parentItemKeys);
    }
}
