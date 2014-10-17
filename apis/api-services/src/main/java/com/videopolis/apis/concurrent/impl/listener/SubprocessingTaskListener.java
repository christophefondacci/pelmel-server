package com.videopolis.apis.concurrent.impl.listener;

import java.util.Collection;

import com.videopolis.apis.concurrent.base.listener.AbstractApisTaskListener;
import com.videopolis.apis.concurrent.impl.ApisCriterionTask;
import com.videopolis.apis.concurrent.model.ApisTaskListener;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.service.TaskRunnerService;

/**
 * A {@link TaskListener} which processes children of a
 * {@link CriteriaContainer} upon successful finish of the parent criteria task.
 * 
 * @author Christophe Fondacci
 * 
 */
public class SubprocessingTaskListener extends AbstractApisTaskListener {

    private CriteriaContainer criteriaContainer;
    private TaskRunnerService taskRunnerService;
    private ApisContext apisContext;

    /**
     * Creates a new listener based on the supplied information.
     * 
     * @param criteriaContainer
     *            a {@link CriteriaContainer} on which the children will be used
     *            to invoke sub tasks
     * @param apisContext
     *            the {@link ApisContext} in use
     * @param taskRunnerService
     *            the {@link TaskRunnerService} to use to invoke sub tasks.
     */
    public SubprocessingTaskListener(CriteriaContainer criteriaContainer,
	    ApisContext apisContext, TaskRunnerService taskRunnerService) {
	this.criteriaContainer = criteriaContainer;
	this.apisContext = apisContext;
	this.taskRunnerService = taskRunnerService;
    }

    @Override
    protected void doTaskFinished(final Task<ItemsResponse> task,
	    TaskExecutionContext context, ItemsResponse result) {
	final Collection<? extends CalmObject> resultObjects = result
		.getItems();
	// Only processing sub-chain when there is at least one object that is
	// returned by the parent
	if (resultObjects != null && !resultObjects.isEmpty()) {
	    // Creating a subtask on the child of the current criterion
	    ApisCriterionTask subTask = new ApisCriterionTask(
		    criteriaContainer, apisContext,
		    resultObjects.toArray(new CalmObject[resultObjects.size()]));
	    subTask.setTaskRunnerService(taskRunnerService);
	    // This listener will only log in case we have problems running
	    // criterion task
	    final ApisTaskListener listener = new AbstractApisTaskListener() {

		@Override
		public void taskFailed(Task<ItemsResponse> task,
			TaskExecutionContext context, Exception exception) {
		    super.taskFailed(task, context, exception);
		}

		@Override
		protected void doTaskFinished(Task<ItemsResponse> task,
			TaskExecutionContext context, ItemsResponse result) {
		}
	    };
	    subTask.addTaskListener(listener);
	    taskRunnerService.execute(subTask);
	}
    }

    @Override
    public void taskFailed(Task<ItemsResponse> task,
	    TaskExecutionContext context, Exception exception) {
	apisContext.getApiResponse().setPartial(true);
	super.taskFailed(task, context, exception);
    }

    @Override
    public String toString() {
	return "SUB;" + criteriaContainer;
    }
}
