package com.videopolis.concurrent.model;

/**
 * The execution context of a task
 * 
 * @author julien
 * 
 */
public interface TaskExecutionContext {

    /**
     * <p>
     * Return whether the task has been interrupted.
     * </p>
     * <p>
     * If this method returns {@code true}, the task should cancel anything it
     * does and return as soon as possible
     * </p>
     * 
     * @return {@code true} is the task has been interrupted
     */
    boolean isExecutionInterrupted();
}
