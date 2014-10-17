package com.videopolis.apis.concurrent.impl;

import java.util.Arrays;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * An {@link ApisTask} invoking a
 * {@link CalService#getItemsFor(java.util.List, com.videopolis.cals.model.CalContext, RequestType)}
 * for a specific {@link RequestType} CAL service method.
 * 
 * @author Christophe Fondacci
 * 
 */
public class GetCalItemsForWithRequestTypeTask extends AbstractApisTask {

    private CalService service;
    private ItemKey[] parentItemKeys;
    private ApisContext context;
    private RequestType requestType;

    /**
     * Creates this task based on the supplied information.
     * 
     * @param service
     *            the {@link CalService} to use
     * @param context
     *            the {@link ApisContext} to use
     * @param requestType
     *            the {@link RequestType} to pass to the service
     * @param parentItems
     *            the unique key of parent items to pas to the service method
     */
    public GetCalItemsForWithRequestTypeTask(CalService service,
	    ApisContext context, RequestType requestType,
	    ItemKey... parentItems) {
	this.service = service;
	this.parentItemKeys = parentItems;
	this.context = context;
	this.requestType = requestType;
    }

    @Override
    public ItemsResponse doExecute(TaskExecutionContext taskExecutionContext)
	    throws TaskExecutionException {
	try {
	    return service.getItemsFor(Arrays.asList(parentItemKeys), context
		    .getCalContext(), requestType);
	} catch (CalException e) {
	    throw new TaskExecutionException(
		    "Problems while calling CalService.getItemsForWithRequestType("
			    + Arrays.toString(parentItemKeys) + ","
			    + context.toString() + "," + requestType + "): "
			    + e.getMessage(), e);
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
