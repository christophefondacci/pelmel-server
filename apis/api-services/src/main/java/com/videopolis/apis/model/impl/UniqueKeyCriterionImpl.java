package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.GetCalItemsTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.delegate.impl.AliasableDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.UniqueKeyCriterion;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * This criteria defines an item to retrieve by its unique identifier. Adding a
 * {@link UniqueKeyCriterionImpl} to a {@link ApisRequest} will result in
 * returning one and only one item max.
 * 
 * @author Christophe
 * 
 */
public class UniqueKeyCriterionImpl extends AbstractApisCriterion implements
	UniqueKeyCriterion {

    private final Aliasable<UniqueKeyCriterion> aliasableDelegate = new AliasableDelegate<UniqueKeyCriterion>(
	    this);

    /** Requested unique key */
    private ItemKey[] uniqueKeys;
    private String itemType;
    /** Requested field combination */
    private RequestType requestType = RequestType.FULL;

    /**
     * Creates a new {@link UniqueKeyCriterionImpl} which will fetch the whole
     * element from its unique key.
     * 
     * @param key
     *            element's unique key to retrieve
     */
    public UniqueKeyCriterionImpl(final ItemKey... uniqueKeys)
	    throws ApisException {
	Assert.notNull(uniqueKeys,
		"The unique key criterion cannot be built from a null ItemKey");
	for (final ItemKey key : uniqueKeys) {
	    if (itemType == null) {
		itemType = key.getType();
	    } else {
		Assert.isEqual(itemType, key.getType(),
			"A unique key criterion could only be created with elements of same type");
	    }
	}
	this.uniqueKeys = uniqueKeys;
    }

    /**
     * Creates a new {@link UniqueKeyCriterionImpl} which will partially fetch
     * the element from its unique id. The requested element will be filled
     * according to the requestedFields field identifier combination.<br>
     * <br>
     * Example : <br>
     * <code>new {@link UniqueKeyCriterionImpl}(<br>
     * &nbsp;&nbsp;CalmFactory.createKey("HOTEL", 13l), Hotel.NAME | Hotel.DESCRIPTION )<br>
     * );</code>
     * 
     * @param key
     *            element's unique key to retrieve
     * @param requestedFields
     *            fields combination to retrieve for this element
     */
    public UniqueKeyCriterionImpl(final ItemKey uniqueKey,
	    final RequestType requestType) throws ApisException {
	this(uniqueKey);
	this.requestType = requestType;
    }

    /**
     * @return whether this criteria returns a lightweight object (= an object
     *         with a limited amount of information) or the full object's data.
     */
    public boolean isLightWeight() {
	return requestType != RequestType.FULL;
    }

    @Override
    public Task<ItemsResponse> getTask(final CriteriaContainer parent,
	    final ApisContext context, final CalmObject... parentKeys) {
	// Retrieving corresponding CAL service from the main type
	final CalService calService = ApisRegistry.getCalService(itemType);
	// Delegating processing to a dedicated task
	final ApisTask<ItemsResponse> task = new GetCalItemsTask(calService,
		context, uniqueKeys);
	task.setCriterion(this);
	return task;
    }

    @Override
    public String getType() {
	return itemType;
    }

    @Override
    public String toString() {
	return "uniqueKey;" + getType() + ";" + uniqueKeys;
    }

    /**
     * Defines the unique key to use when this criterion will be processed
     * 
     * @param uniqueKey
     *            unique key reference as an {@link ItemKey}
     */
    public void setUniqueKey(final ItemKey uniqueKey) {
	uniqueKeys = new ItemKey[] { uniqueKey };
    }

    @Override
    public String getAlias() {
	return aliasableDelegate.getAlias();
    }

    @Override
    public UniqueKeyCriterion aliasedBy(final String alias)
	    throws ApisException {
	return aliasableDelegate.aliasedBy(alias);
    }
}
