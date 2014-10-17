package com.videopolis.concurrent.service;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.test.event.LoggerTaskListener;
import com.videopolis.concurrent.test.factory.TestCaseFactory;

public class RepeatingTaskRunnerServiceTestCase extends TestCase {

    private static final Log LOGGER = LogFactory
	    .getLog(RepeatingTaskRunnerServiceTestCase.class);

    private static final int RUN_COUNT = 10;
    private static final long TIME_THRESHOLD = 3600;

    private final TaskRunnerService taskRunnerService;

    public RepeatingTaskRunnerServiceTestCase(
	    TaskRunnerService taskRunnerService) {
	super(taskRunnerService.getClass().getCanonicalName());
	this.taskRunnerService = taskRunnerService;
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

	for (int run = 1; run <= RUN_COUNT; run++) {
	    LOGGER.info("Starting run " + run + " out of " + RUN_COUNT);
	    final long start = System.currentTimeMillis();
	    taskRunnerService.execute(TestCaseFactory.createSimpleTestCase(
		    taskRunnerService, new LoggerTaskListener<String>()));
	    final long stop = System.currentTimeMillis();
	    final long duration = stop - start;
	    assertTrue("Test run for " + duration + " ms. Should be at least "
		    + TIME_THRESHOLD + " ms.", duration >= TIME_THRESHOLD);
	}

	LOGGER.info("Finished");
    }
}
