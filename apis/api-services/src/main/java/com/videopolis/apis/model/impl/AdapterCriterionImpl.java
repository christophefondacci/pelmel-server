package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.AdaptCalmObjectTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisCalmObjectAdapter;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.model.Task;

public class AdapterCriterionImpl extends AbstractApisCriterion {

    private ApisCalmObjectAdapter adapter;

    public AdapterCriterionImpl(ApisCalmObjectAdapter adapter) {
	this.adapter = adapter;
    }

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects)
	    throws ApisException {
	return new AdaptCalmObjectTask(context, adapter, parentObjects);
    }

    @Override
    public String getType() {
	throw new UnsupportedOperationException(
		"getType() not supported by APIS adapter");
    }

    @Override
    public String toString() {
	return "adapt";
    }
}
