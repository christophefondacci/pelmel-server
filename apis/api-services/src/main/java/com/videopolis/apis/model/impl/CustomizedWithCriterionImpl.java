package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.GetCustomizedCalItemsForTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.helper.ConversionHelper;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.model.base.AbstractWithCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * Implementation of a customized WITH criterion which can perform customized
 * CAL calls.
 * 
 * @author Christophe Fondacci
 * 
 */
public class CustomizedWithCriterionImpl extends AbstractWithCriterion {

    /** Item type to retrieve <i>with</i> the primary items */
    private final String itemType;

    /**
     * Creates a new {@link WithCriterion} which will fetch information of the
     * specified CAL item type connected to the parent of this criterion.
     * 
     * @param itemType
     *            CAL item type to be fetched
     * @throws ApisException
     *             when itemType is not valid
     */
    public CustomizedWithCriterionImpl(final String itemType)
	    throws ApisException {
	Assert.notNull(itemType,
		"Cannot create a with clause using a null item type");
	this.itemType = itemType;
    }

    @Override
    public String getType() {
	return itemType;
    }

    @Override
    public Task<ItemsResponse> getTask(final CriteriaContainer parent,
	    final ApisContext context, final CalmObject... parentObjects)
	    throws ApisException {

	Assert.uniqueElement(
		"Customized with is not supported for collection of parents, "
			+ "you must have a unique parent", parentObjects);
	// Retrieving the CAL service for the type of this criterion
	final RequestSettings requestSettings = ConversionHelper
		.toPaginationRequestSettings(getSorters(), getPagination());
	// Retrieving the CAL service to invoke
	final CalService service = ApisRegistry.getCalService(getType());
	// Retrieving parent key to query for
	final ItemKey parentKey = parentObjects[0].getKey();
	// Building our APIS task
	final ApisTask<ItemsResponse> task = new GetCustomizedCalItemsForTask(
		service, parentKey, context, requestSettings);

	// Setting the instigating criterion as us
	task.setCriterion(this);
	return task;
    }

    @Override
    public String toString() {
	return "customizedWith;" + getType();
    }
}
