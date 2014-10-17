package com.videopolis.concurrent.service;

import junit.framework.TestCase;

public class TaskRunnerServicesTestInt extends AbstractTaskRunnerServicesTester {

    @Override
    protected TestCase getTestCaseFor(TaskRunnerService taskRunnerService) {
	return new TaskRunnerServiceTestCase(taskRunnerService);
    }
}
