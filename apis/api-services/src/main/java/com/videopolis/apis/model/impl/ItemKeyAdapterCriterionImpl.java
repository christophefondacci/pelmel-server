package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.AdaptItemKeyTask;
import com.videopolis.apis.delegate.impl.AliasableDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.ItemKeyAdapterCriterion;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.model.Task;

/**
 * Criterion that handles adaptation of parent objects.
 * 
 * TODO: temporary, need to switch whole APIS to base getTask method on ItemKey
 * 
 * @author Christophe Fondacci
 */
public class ItemKeyAdapterCriterionImpl extends AbstractApisCriterion
	implements ItemKeyAdapterCriterion {
    private final ApisItemKeyAdapter adapter;
    private final Aliasable<ApisCriterion> aliasableDelegate = new AliasableDelegate<ApisCriterion>(
	    this);

    public ItemKeyAdapterCriterionImpl(ApisItemKeyAdapter adapter) {
	this.adapter = adapter;
    }

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects)
	    throws ApisException {
	AdaptItemKeyTask task = new AdaptItemKeyTask(adapter, context,
		parentObjects);
	task.setCriterion(this);
	return task;
    }

    @Override
    public String getType() {
	return null;
    }

    @Override
    public ApisCriterion aliasedBy(String alias) throws ApisException {
	return aliasableDelegate.aliasedBy(alias);
    }

    @Override
    public String getAlias() {
	return aliasableDelegate.getAlias();
    }

    @Override
    public ApisItemKeyAdapter getAdapter() {
	return adapter;
    }
}
