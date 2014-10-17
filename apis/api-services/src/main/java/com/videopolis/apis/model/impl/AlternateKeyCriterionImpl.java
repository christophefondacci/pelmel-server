package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.GetCalItemsTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.delegate.impl.AliasableDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.AlternateKeyCriterion;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * Implementation of an alternate key criteria.
 * 
 * @author Christophe Fondacci
 * 
 */
public class AlternateKeyCriterionImpl extends AbstractApisCriterion implements
	AlternateKeyCriterion {

    private final ItemKey alternateKey;
    private String calType;
    private final Aliasable<AlternateKeyCriterion> aliasableDelegate = new AliasableDelegate<AlternateKeyCriterion>(
	    this);

    /**
     * Builds a new {@link ApisCriterion} implementing the connection to the
     * specified alternate key.
     * 
     * @param alternateKey
     *            alternate key to fetch items for
     */
    public AlternateKeyCriterionImpl(ItemKey alternateKey) {
	this.alternateKey = alternateKey;
    }

    /**
     * Builds a new {@link ApisCriterion} implementing the connection to the
     * specified alternate key.
     * 
     * @param alternateKey
     *            alternate key to fetch items for
     */
    public AlternateKeyCriterionImpl(String calType, ItemKey alternateKey) {
	this.calType = calType;
	this.alternateKey = alternateKey;
    }

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects) {
	final CalService calService = ApisRegistry
		.getCalService(calType == null ? parent.getType() : calType);
	ApisTask<ItemsResponse> task = new GetCalItemsTask(calService, context,
		alternateKey);
	task.setCriterion(this);
	return task;
    }

    @Override
    public String getType() {
	return calType == null ? alternateKey.getType() : calType;
    }

    @Override
    public String getAlias() {
	return aliasableDelegate.getAlias();
    }

    @Override
    public AlternateKeyCriterion aliasedBy(String alias) throws ApisException {
	return aliasableDelegate.aliasedBy(alias);
    }
}
