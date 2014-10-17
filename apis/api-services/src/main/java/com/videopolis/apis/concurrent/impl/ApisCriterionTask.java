package com.videopolis.apis.concurrent.impl;

import com.videopolis.apis.concurrent.base.AbstractApisCriterionTask;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.calm.model.CalmObject;

/**
 * {@inheritDoc}
 * 
 * @see AbstractApisCriterionTask
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApisCriterionTask extends AbstractApisCriterionTask {

    /**
     * Creates this task.
     * 
     * @param container
     *            the parent container whose {@link ApisCriterion} contents
     *            should be processed
     * @param context
     *            the {@link ApisContext} to use
     * @param parentObjects
     *            parent objects to aggregate results into
     */
    public ApisCriterionTask(CriteriaContainer container, ApisContext context,
	    CalmObject... parentObjects) {
	super(container, context, parentObjects);
    }
}
