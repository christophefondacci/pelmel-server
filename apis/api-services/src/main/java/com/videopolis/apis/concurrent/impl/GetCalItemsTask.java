package com.videopolis.apis.concurrent.impl;

import java.util.Arrays;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * An {@link ApisTask} which invokes a
 * {@link CalService#getItems(java.util.List, com.videopolis.cals.model.CalContext)}
 * 
 * @author Christophe Fondacci
 * 
 */
public class GetCalItemsTask extends AbstractApisTask {

    private ItemKey[] itemKeys;
    private ApisContext context;
    private CalService calService;

    /**
     * Creates this task based on the given information.
     * 
     * @param service
     *            {@link CalService} to call
     * @param context
     *            {@link ApisContext} to use
     * @param itemKeys
     *            unique key of elements to fetch
     */
    public GetCalItemsTask(CalService service, ApisContext context,
	    ItemKey... itemKeys) {
	this.calService = service;
	this.itemKeys = itemKeys;
	this.context = context;
    }

    @Override
    public ItemsResponse doExecute(TaskExecutionContext taskExecutionContext)
	    throws TaskExecutionException {
	try {
	    return calService.getItems(Arrays.asList(itemKeys), context
		    .getCalContext());
	} catch (CalException e) {
	    throw new TaskExecutionException("Problems while calling "
		    + calService.toString() + ".getItems("
		    + Arrays.asList(itemKeys) + "," + context.toString()
		    + "): " + e.getMessage(), e);
	}
    }

    @Override
    public String toString() {
	return "getCalItems;"
		+ (calService == null ? "null" : ApisRegistry
			.getTypeForCalService(calService)) + ";"
		+ Arrays.toString(itemKeys);
    }
}
