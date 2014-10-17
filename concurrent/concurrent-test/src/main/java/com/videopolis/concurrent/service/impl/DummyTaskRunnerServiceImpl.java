package com.videopolis.concurrent.service.impl;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.videopolis.concurrent.model.Task;
import com.videopolis.concurrent.service.base.AbstractTimeoutTaskRunnerService;

/**
 * A stupid task runner which will just sleep until the timeout is reached, or
 * return immediately if no timeout is set
 *
 * @author julien
 *
 */
public class DummyTaskRunnerServiceImpl extends
	AbstractTimeoutTaskRunnerService {

    private static final Log LOGGER = LogFactory
	    .getLog(DummyTaskRunnerServiceImpl.class);

    @Override
    public void execute(final Collection<Task<?>> tasks) {
	// Just sleep
	if (hasTimeout()) {
	    try {
		Thread.sleep(getTimeout());
	    } catch (final InterruptedException e) {
		LOGGER.warn("Interrupted");
	    }
	}
    }

    @Override
    public void shutdown() {
	// Nothing to do here
    }
}
