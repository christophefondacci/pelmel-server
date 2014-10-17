package com.videopolis.concurrent.model;

/**
 * A mutable version of {@link TaskExecutionContext}. Designed to be used
 * internally by implementations.
 * 
 * @author julien
 * 
 */
public interface MutableTaskExecutionContext extends TaskExecutionContext {

    /**
     * Sets the executionInterrupted flag
     * 
     * @param interrupted
     *            Value
     */
    void setExecutionInterrupted(boolean interrupted);

}
