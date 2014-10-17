package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.GetCalItemsTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.ConversionHelper;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * This criterion allows to fetch elements (getItems) of the parent. At the
 * moment, it is internally used by the framework for nearby tasks to fetch
 * objects returned by a search query.
 * 
 * @author Christophe Fondacci
 * 
 */
public class GetCriterionImpl extends AbstractApisCriterion {

    private String type;

    public GetCriterionImpl(String type) {
	this.type = type;
    }

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects)
	    throws ApisException {
	// Retrieving corresponding CAL service from the main type
	final CalService calService = ApisRegistry.getCalService(type);
	ItemKey[] keys = ConversionHelper.getKeysFromObjects(parentObjects);
	// Delegating processing to a dedicated task
	final ApisTask<ItemsResponse> task = new GetCalItemsTask(calService,
		context, keys);
	task.setCriterion(this);
	return task;
    }

    @Override
    public String getType() {
	return type;
    }

}
