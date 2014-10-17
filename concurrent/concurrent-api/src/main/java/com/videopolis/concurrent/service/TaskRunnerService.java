package com.videopolis.concurrent.service;

import java.util.Collection;

import com.videopolis.concurrent.model.Task;

/**
 * <p>
 * A task runner which is designed to execute separate tasks. Runners may
 * implement features like concurrent execution.
 * </p>
 * <p>
 * How the tasks are actually run is implementation-specific. However, the
 * execution contract is the following:
 * <ul>
 * <li>The tasks may be executing in parallel or not</li>
 * <li>The {@code execute()} methods should return as soon as all tasks
 * successfully completed returned from the {@code taskFinished()} listeners.</li>
 * <li>When a task execution is finished (with no exception thrown), the method
 * {@code taskFinished()} must be called on every listener of the task</li>
 * <li>When the {@code execute()} method of a task throws an exception, the
 * method {@code taskFailed()} must be called on every listener of the task</li>
 * <li>When, for any reason, the execution is interrupted while the {@code
 * execute()} method is running, the following actions must be taken:
 * <ul>
 * <li>The executing thread must be interrupted</li>
 * <li>The {@code executionInterrupted} flag of the {@code TaskExecutionContext}
 * object attached to the task must be set</li>
 * <li>As soon the execution of {@code executed()} has returned, the method
 * {@code taskInterrupted()} of each listener of the task must be called</li>
 * </ul>
 * </li>
 * <li>When, for any reason, the execution is interrupted while the {@code
 * taskFinished()} methods are called, the executing thread must be interrupted</li>
 * <li>When the execution is interrupted in any other case, no particular action
 * is required, the running method may terminate normally.</li>
 * </ul>
 * </p>
 * <p>
 * For details on timeouts, see {@link AbstractTimeoutTaskRunnerService}
 * </p>
 * 
 * @see Task
 * @author julien
 * 
 */
public interface TaskRunnerService {

    /**
     * Executes the given tasks
     * 
     * @param tasks
     *            tasks to run
     */
    void execute(Collection<Task<?>> tasks);

    /**
     * Executes the given tasks using the default timeout value
     * 
     * @param tasks
     *            tasks to run
     */
    void execute(Task<?>... tasks);

    /**
     * Shutdown this runner. It will not accept tasks
     */
    void shutdown();
}
