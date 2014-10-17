package com.videopolis.concurrent.factory;

import com.videopolis.concurrent.model.MutableTaskExecutionContext;
import com.videopolis.concurrent.model.impl.MutableTaskExecutionContextImpl;

/**
 * A factory used to create concurrent framework related objects
 * 
 * @author julien
 * 
 */
public final class ConcurrentFactory {

    private ConcurrentFactory() {
    }

    /**
     * Builds a new {@link MutableTaskExecutionContext} instance.
     * 
     * @return New MutableTaskExecutionContext
     */
    public static MutableTaskExecutionContext newMutableTaskExecutionContext() {
	return new MutableTaskExecutionContextImpl();
    }
}
