package com.videopolis.concurrent.model.impl;

import com.videopolis.concurrent.model.MutableTaskExecutionContext;

/**
 * Default implementation of {@link MutableTaskExecutionContext}
 *
 * @author julien
 *
 */
public class MutableTaskExecutionContextImpl implements
	MutableTaskExecutionContext {

    private volatile boolean executionInterrupted = false;

    @Override
    public boolean isExecutionInterrupted() {
	return executionInterrupted;
    }

    @Override
    public void setExecutionInterrupted(final boolean interrupted) {
	executionInterrupted = interrupted;
    }
}
