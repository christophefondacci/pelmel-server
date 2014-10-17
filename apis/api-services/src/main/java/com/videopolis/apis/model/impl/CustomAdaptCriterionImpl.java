package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.CustomAdaptTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.model.Task;

public class CustomAdaptCriterionImpl extends AbstractApisCriterion implements
	ApisCriterion, Aliasable<ApisCriterion> {

    private final ApisCustomAdapter adapter;
    private String alias;

    public CustomAdaptCriterionImpl(ApisCustomAdapter adapter, String alias) {
	this.adapter = adapter;
	this.alias = alias;
    }

    @Override
    public String getType() {
	return null;
    }

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects)
	    throws ApisException {
	return new CustomAdaptTask(adapter, context, parentObjects);
    }

    @Override
    public String getAlias() {
	return alias;
    }

    @Override
    public ApisCriterion aliasedBy(String alias) throws ApisException {
	this.alias = alias;
	return this;
    }

}
