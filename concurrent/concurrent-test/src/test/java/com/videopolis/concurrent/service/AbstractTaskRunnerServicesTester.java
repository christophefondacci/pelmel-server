package com.videopolis.concurrent.service;

import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.videopolis.concurrent.service.impl.DummyTaskRunnerServiceImpl;

public abstract class AbstractTaskRunnerServicesTester extends
	AbstractDependencyInjectionSpringContextTests {

    private static final Log LOGGER = LogFactory
	    .getLog(AbstractTaskRunnerServicesTester.class);

    private List<TaskRunnerService> taskRunnerServicesToTest;

    public void setTaskRunnerServicesToTest(
	    List<TaskRunnerService> taskRunnerServicesToTest) {
	this.taskRunnerServicesToTest = taskRunnerServicesToTest;
    }

    @Override
    protected String[] getConfigLocations() {
	return new String[] { "/META-INF/concurrent/test/testContext.xml" };
    }

    @Override
    public void run(TestResult result) {
	try {
	    setUp();
	    final TestSuite suite = new TestSuite();
	    // Fire a collection of tests
	    for (final TaskRunnerService taskRunnerService : taskRunnerServicesToTest) {
		// TODO: Workaround to prevent strange injection
		if (!(taskRunnerService instanceof DummyTaskRunnerServiceImpl)) {
		    suite.addTest(getTestCaseFor(taskRunnerService));
		}
	    }
	    suite.run(result);
	    tearDown();
	} catch (Exception e) {
	    result.addError(this, e);
	}
    }

    public void testConfiguration() {
	assertNotNull(
		"No taskRunnerServices injected, please check Spring context declaration",
		taskRunnerServicesToTest);
	LOGGER.info(taskRunnerServicesToTest.size()
		+ " TaskRunnerService instances to test");
	assertFalse("No TaskRunnerService to test", taskRunnerServicesToTest
		.isEmpty());
    }

    protected abstract TestCase getTestCaseFor(
	    TaskRunnerService taskRunnerService);
}
