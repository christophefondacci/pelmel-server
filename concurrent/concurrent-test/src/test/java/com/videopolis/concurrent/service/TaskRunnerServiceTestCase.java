package com.videopolis.concurrent.service;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.event.TaskListener;
import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.test.event.LoggerTaskListener;
import com.videopolis.concurrent.test.factory.TestCaseFactory;

public class TaskRunnerServiceTestCase extends TestCase {

    private static final Log LOGGER = LogFactory
	    .getLog(TaskRunnerServiceTestCase.class);

    private final TaskRunnerService taskRunnerService;
    private volatile int failureCount;
    private volatile int successCount;
    private volatile int interruptionCount;

    public TaskRunnerServiceTestCase(TaskRunnerService taskRunnerService) {
	super(taskRunnerService.getClass().getCanonicalName());
	this.taskRunnerService = taskRunnerService;
    }

    @Override
    protected void setUp() throws Exception {
	failureCount = 0;
	successCount = 0;
	interruptionCount = 0;
    }

    @Override
    protected void tearDown() throws Exception {
	taskRunnerService.shutdown();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void runTest() {

	LOGGER.info("Now testing "
		+ taskRunnerService.getClass().getCanonicalName());

	final LoggerTaskListener<String> loggerTaskListener = new LoggerTaskListener<String>();
	final CounterTaskListener counterTaskListener = new CounterTaskListener();
	taskRunnerService.execute(TestCaseFactory.createSimpleTestCase(
		taskRunnerService, loggerTaskListener, counterTaskListener));
	LOGGER
		.info("execute() finished, now waiting for interrupted tasks to finish...");
	try {
	    Thread.sleep(3000);
	} catch (InterruptedException e) {
	    LOGGER.error(e, e);
	}
	LOGGER.info("Finished with " + failureCount + " failures, "
		+ successCount + " successes, " + interruptionCount
		+ " interruptions");

	// Check values
	assertEquals("Invalid task failure count", 1, failureCount);
	assertEquals("Invalid task success count", 4, successCount);
	assertEquals("Invalid interruption count", 2, interruptionCount);
    }

    private class CounterTaskListener implements TaskListener<String> {

	@Override
	public void taskFailed(Task<String> task, TaskExecutionContext context,
		Exception exception) {
	    LOGGER.info("Incrementing failureCount");
	    failureCount++;
	}

	@Override
	public void taskFinished(Task<String> task,
		TaskExecutionContext context, String result) {
	    LOGGER.info("Incrementing successCount");
	    successCount++;
	}

	@Override
	public void taskInterrupted(Task<String> task,
		TaskExecutionContext context) {
	    LOGGER.info("Incrementing interruptionCount");
	    interruptionCount++;
	}
    }
}
