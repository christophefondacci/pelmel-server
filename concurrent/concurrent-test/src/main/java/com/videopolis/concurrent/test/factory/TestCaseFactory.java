package com.videopolis.concurrent.test.factory;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.event.base.AbstractTaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.concurrent.test.task.DumbTask;

/**
 * A factory used to generate tasks for testing purpose
 *
 * @author julien
 *
 */
public final class TestCaseFactory {

    private static final int SHORT_TIMING = 1500;
    private static final int INTERMEDIATE_TIMING = 2500;
    private static final int LONG_TIMING = 3500;

    /**
     * A listener which makes the task respawn
     *
     * @author julien
     *
     */
    private static class RespawnTaskListener extends
	    AbstractTaskListener<String> {

	private static final Log LOGGER = LogFactory
		.getLog(RespawnTaskListener.class);

	private final TaskRunnerService taskRunnerService;
	private final TaskListener<String>[] listeners;

	/**
	 * Default constructor
	 *
	 * @param taskRunnerService
	 *            Service
	 * @param listeners
	 *            Listeners
	 */
	public RespawnTaskListener(final TaskRunnerService taskRunnerService,
		final TaskListener<String>[] listeners) {
	    this.taskRunnerService = taskRunnerService;
	    this.listeners = Arrays.copyOf(listeners, listeners.length);
	}

	@Override
	public void taskFinished(final Task<String> task,
		final TaskExecutionContext context, final String result) {
	    LOGGER.info("[" + task + "] RESPAWN");
	    taskRunnerService.execute(new DumbTask(SHORT_TIMING, listeners),
		    new DumbTask(INTERMEDIATE_TIMING, listeners), new DumbTask(
			    LONG_TIMING, listeners));
	    LOGGER.info("[" + task + "] RESPAWN DONE");
	}
    }

    private TestCaseFactory() {
    }

    /**
     * Creates a dumb task
     *
     * @param taskRunnerService
     *            Target service
     * @param listeners
     *            Listeners
     * @return Tasks
     */
    public static DumbTask[] createSimpleTestCase(
	    final TaskRunnerService taskRunnerService,
	    final TaskListener<String>... listeners) {

	final DumbTask respawnTask = new DumbTask(2000, listeners);
	respawnTask.addTaskListener(new RespawnTaskListener(taskRunnerService,
		listeners));

	final DumbTask[] tasks = new DumbTask[] {
		new DumbTask(1000, listeners), respawnTask,
		new DumbTask(3000, listeners), new DumbTask(4000, listeners) };

	return tasks;
    }
}
