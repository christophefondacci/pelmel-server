package com.videopolis.concurrent.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.UnableToInterruptJobException;

import com.videopolis.concurrent.factory.QuartzFactory;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.service.base.AbstractTimeoutTaskRunnerService;

/**
 * An implementation of {@link TaskRunner} which uses the Quartz scheduling
 * framework to execute tasks.
 *
 * @author julien
 *
 */
public class QuartzTaskRunnerServiceImpl extends
	AbstractTimeoutTaskRunnerService {

    private static final Log LOGGER = LogFactory
	    .getLog(QuartzTaskRunnerServiceImpl.class);

    /** The Quartz scheduler to use */
    private Scheduler scheduler;

    public void setScheduler(final Scheduler scheduler) {
	this.scheduler = scheduler;
    }

    @Override
    public void execute(final Collection<Task<?>> tasks) {

	try {
	    // Start the scheduler if required
	    LOGGER.info("Starting Quartz Scheduler...");
	    if (!scheduler.isStarted()) {
		scheduler.start();
	    }

	    // This object will be used as a lock
	    final Object lock = new Object();

	    // Compute the group name for the jobs
	    final String group = Long.toHexString(System.currentTimeMillis())
		    + "-" + lock.toString();

	    // Schedule jobs
	    synchronized (lock) {
		int runningJobs = 0;
		for (final Task<?> task : tasks) {
		    // Building job-related objects
		    final JobDetail jobDetail = QuartzFactory.newTaskJobDetail(
			    task, lock, group);
		    final Trigger trigger = QuartzFactory
			    .newImmediateTrigger(jobDetail);

		    if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Scheduling job "
				+ jobDetail.getFullName());
		    }

		    // Scheduling job
		    scheduler.scheduleJob(jobDetail, trigger);
		    runningJobs++;
		}

		if (hasTimeout()) {
		    waitWithTimeout(group, lock, runningJobs);
		} else {
		    waitWithoutTimeout(group, lock, runningJobs);
		}
	    }
	} catch (final SchedulerException e) {
	    // TODO: Do something smart
	    LOGGER.error(e.getMessage(), e);
	}
    }

    /**
     * Wait until job are finished or the timeout is over
     *
     * @param group
     *            Job group
     * @param lock
     *            Lock object
     * @param runningJobs
     *            Number of jobs to wait for
     * @throws SchedulerException
     */
    private void waitWithTimeout(final String group, final Object lock,
	    final int jobCount) throws SchedulerException {
	int runningJobs = jobCount;

	long timeout = getTimeout();
	long lastTime = System.currentTimeMillis();

	// Wait
	try {
	    while (runningJobs > 0 && timeout > 0) {
		lock.wait(timeout);
		final long now = System.currentTimeMillis();
		timeout -= now - lastTime;
		lastTime = now;
		if (timeout > 0) {
		    runningJobs--;
		}
	    }
	} catch (final InterruptedException e) {
	    LOGGER.debug("Execution interrupted");
	} finally {
	    cleanUpExecutingJobs(group);
	}
    }

    /**
     * Wait until all the jobs are finished, without any timeout
     *
     * @param group
     *            Job group
     * @param lock
     *            Lock object
     * @param runningJobs
     *            Number of jobs to wait for
     * @throws SchedulerException
     */
    private void waitWithoutTimeout(final String group, final Object lock,
	    final int jobCount) throws SchedulerException {
	int runningJobs = jobCount;

	// Wait until all jobs finished
	try {
	    while (runningJobs > 0) {
		lock.wait();
		runningJobs--;
	    }
	} catch (final InterruptedException e) {
	    LOGGER.debug("Execution interrupted");
	} finally {
	    cleanUpExecutingJobs(group);
	}
    }

    /**
     * Interrupt unfinished jobs which belong to a group
     *
     * @param group
     *            Group
     * @throws SchedulerException
     */
    private void cleanUpExecutingJobs(final String group)
	    throws SchedulerException {

	@SuppressWarnings("unchecked")
	final List<JobExecutionContext> jobs = scheduler
		.getCurrentlyExecutingJobs();

	// Clean up remaining jobs
	for (final JobExecutionContext job : jobs) {
	    if (group.equals(job.getJobDetail().getGroup())) {
		try {
		    scheduler.interrupt(job.getJobDetail().getName(), group);
		} catch (final UnableToInterruptJobException e) {
		    LOGGER.error("Unable to interrupt job "
			    + job.getJobDetail().getFullName());
		}
	    }
	}
    }

    @Override
    public void execute(final Task<?>... tasks) {
	execute(Arrays.asList(tasks));
    }

    @Override
    public void shutdown() {
	try {
	    scheduler.shutdown();
	} catch (final SchedulerException e) {
	    LOGGER.error(
		    "Unable to shutdown Quartz scheduler: " + e.getMessage(), e);
	}
    }
}
