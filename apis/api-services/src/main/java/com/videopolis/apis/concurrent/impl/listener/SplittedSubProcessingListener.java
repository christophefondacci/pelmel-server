package com.videopolis.apis.concurrent.impl.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.videopolis.apis.concurrent.base.listener.AbstractApisTaskListener;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.service.TaskRunnerService;

public class SplittedSubProcessingListener extends AbstractApisTaskListener {

    private int maxItemCount;
    private TaskListener<ItemsResponse> rootListener;
    private TaskRunnerService taskRunnerService;

    public SplittedSubProcessingListener(int maxItemCount,
	    CriteriaContainer criteriaContainer, ApisContext apisContext,
	    TaskRunnerService taskRunnerService) {
	this.maxItemCount = maxItemCount;
	rootListener = new SubprocessingTaskListener(criteriaContainer,
		apisContext, taskRunnerService);
	this.taskRunnerService = taskRunnerService;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void doTaskFinished(final Task<ItemsResponse> task,
	    TaskExecutionContext context, ItemsResponse result) {
	final List<? extends CalmObject> objects = result.getItems();
	final List<ItemsResponse> splittedResponses = new ArrayList<ItemsResponse>();
	if (objects.size() > maxItemCount) {
	    int index = 0;
	    // Splitting in chunks of maxItemCount elements
	    while (index < objects.size()) {
		// Extracting the sublist
		final List<? extends CalmObject> split = objects.subList(index,
			Math.min(objects.size(), index + maxItemCount));
		// Stacking our splitted list in a dedicated response
		final ItemsResponseImpl splittedResponse = new ItemsResponseImpl();
		splittedResponse.setItems(split);
		splittedResponses.add(splittedResponse);
		index += split.size();
	    }
	} else {
	    splittedResponses.add(result);
	}
	// Calling finished on the root listener
	final List tasks = new ArrayList<Task<Object>>(splittedResponses.size());
	for (final ItemsResponse splittedResponse : splittedResponses) {
	    // Building the task list
	    tasks.add(new AbstractTask<Object>() {
		@Override
		public Object execute(TaskExecutionContext context)
			throws TaskExecutionException, InterruptedException {
		    rootListener.taskFinished(task, context, splittedResponse);
		    return null;
		}
	    });
	}
	taskRunnerService.execute((Collection<Task<?>>) tasks);
    }
}
