package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.SearchNearbyTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.model.base.AbstractWithCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * An extension of the {@link WithCriterion} which will search connected items
 * located <b>near</b> a specified point.
 * 
 * @author Christophe Fondacci
 * 
 */
public class WithNearestCriterionImpl extends AbstractWithCriterion {

    private double radius;
    private String itemType;

    /**
     * Creates a new WITH NEAREST criterion which will fetch the element of the
     * given CAL item type closest to the parent, within the given radius and
     * pagination setting.
     * 
     * @param itemType
     *            CAL item type to be fetched.
     * @param radius
     *            max distance of elements to retrieve
     * @param window
     *            pagination information
     * @throws ApisException
     */
    public WithNearestCriterionImpl(String itemType, double radius,
	    PaginationSettings window) throws ApisException {
	this.itemType = itemType;
	this.radius = radius;
	setPagination(window);
	// addCriterion(new GetCriterionImpl(itemType));
    }

    public double getRadius() {
	return radius;
    }

    @Override
    public Task<ItemsResponse> getTask(CriteriaContainer parent,
	    ApisContext context, CalmObject... parentObjects)
	    throws ApisException {
	Assert.uniqueElement(
		"Nearest search is not supported for collection of parents, you must have a unique parent to search nearbys from",
		parentObjects);
	final CalmObject parentObject = parentObjects[0];
	Assert.instanceOf(parentObject, Localized.class,
		"Cannot perform a nearby search: parent item is not Localized");
	final CalService service = ApisRegistry.getCalService(getType());
	final ApisTask<ItemsResponse> task = new SearchNearbyTask(service,
		context, getType(), (Localized) parentObject, radius,
		getPagination());
	task.setCriterion(this);
	return task;
    }

    @Override
    public String getType() {
	return itemType;
    }

    @Override
    public String toString() {
	return "withNearest;" + getType() + ";" + radius;
    }

}
