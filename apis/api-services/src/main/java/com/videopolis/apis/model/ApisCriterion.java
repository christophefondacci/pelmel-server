package com.videopolis.apis.model;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.service.TaskRunnerService;

/**
 * This interface abstracts any search criteria which could be used to search
 * items.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApisCriterion extends CriteriaContainer {

    /**
     * This method builds the task that this criterion will perform. Each
     * criterion corresponds to a given task.
     * 
     * @param parent
     *            the parent element which defines / call this method
     * @param context
     *            the APIS context information, see {@link ApisContext}
     * @param parentObjects
     *            parent {@link CalmObject} of this element. The parents may
     *            provide the information to retrieve the data for and represent
     *            the target objects to aggregate the response into
     * @return a {@link Runnable} task in the framework
     */
    Task<ItemsResponse> getTask(CriteriaContainer parent, ApisContext context,
	    CalmObject... parentObjects) throws ApisException;

    TaskListener<ItemsResponse> getSubprocessingListener(
	    ApisCriterion criterion, ApisContext context,
	    TaskRunnerService taskRunnerService);
}
