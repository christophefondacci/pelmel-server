package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.GetCalItemsForTask;
import com.videopolis.apis.concurrent.impl.GetCalItemsForWithRequestTypeTask;
import com.videopolis.apis.concurrent.impl.GetCalItemsRangeForTask;
import com.videopolis.apis.concurrent.impl.GetPaginatedCalItemsForTask;
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
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * Standard implementation of a WITH criterion.
 * 
 * @author Christophe Fondacci
 * 
 */
public class WithCriterionImpl extends AbstractWithCriterion {
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
	public WithCriterionImpl(String itemType) throws ApisException {
		Assert.notNull(itemType,
				"Cannot create a with clause using a null item type");
		this.itemType = itemType;
	}

	@Override
	public String getType() {
		return itemType;
	}

	@Override
	public Task<ItemsResponse> getTask(CriteriaContainer parent,
			final ApisContext context, CalmObject... parentObjects)
			throws ApisException {
		// Retrieving the CAL service for the type of this criterion
		final CalService service = ApisRegistry.getCalService(getType());
		// Converting parent objects into key collection
		final ItemKey[] parentKeys = ConversionHelper
				.getKeysFromObjects(parentObjects);
		ApisTask<ItemsResponse> task = null;
		if (getPagination() != null) {
			if (parentKeys.length == 1) {
				task = new GetPaginatedCalItemsForTask(service, context,
						getPagination(), parentKeys[0], getRequestType());
			} else {
				task = new GetCalItemsRangeForTask(service, context,
						getPagination(), parentKeys);
			}
		} else {
			final RequestType requestType = getRequestType();
			if (requestType == null) {
				task = new GetCalItemsForTask(service, context, parentKeys);
			} else {
				task = new GetCalItemsForWithRequestTypeTask(service, context,
						requestType, parentKeys);
			}
		}
		// Setting the instigating criterion as us
		task.setCriterion(this);
		return task;
	}

	@Override
	public String toString() {
		return "with;" + getType();
	}
}
