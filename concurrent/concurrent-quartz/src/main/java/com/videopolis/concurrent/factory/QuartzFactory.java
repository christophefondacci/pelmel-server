package com.videopolis.concurrent.factory;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.quartz.QuartzConstants;
import com.videopolis.concurrent.quartz.TaskJob;
import com.videopolis.concurrent.quartz.TaskJobListener;

/**
 * A factory used to create Quartz jobs from tasks
 *
 * @author julien
 *
 */
public final class QuartzFactory {

    private QuartzFactory() {
    }

    /**
     * Creates a new immediate trigger
     *
     * @param jobDetail
     *            Job
     * @return Trigger
     */
    public static Trigger newImmediateTrigger(final JobDetail jobDetail) {
	final Trigger trigger = new SimpleTrigger(jobDetail.getName()
		+ "-trigger", jobDetail.getGroup());
	trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
	return trigger;
    }

    /**
     * Creates a job given a task
     *
     * @param task
     *            Task
     * @param lock
     *            The object used to lock the task
     * @param group
     *            The job group
     * @return The job
     */
    public static JobDetail newTaskJobDetail(final Task<?> task,
	    final Object lock, final String group) {
	final JobDetail jobDetail = new JobDetail(task.toString(), group,
		TaskJob.class);

	// Create the data map
	final JobDataMap jobDataMap = new JobDataMap();
	jobDataMap.put(QuartzConstants.TASK, task);
	jobDataMap.put(QuartzConstants.LOCK, lock);
	jobDataMap.put(QuartzConstants.CONTEXT,
		ConcurrentFactory.newMutableTaskExecutionContext());
	jobDetail.setJobDataMap(jobDataMap);

	// Add the listener
	jobDetail.addJobListener(TaskJobListener.class.getCanonicalName());

	return jobDetail;
    }
}
