package com.videopolis.concurrent.service.base;


/**
 * <p>
 * A base implementation of {@link AbstractTaskRunnerService} which has a
 * timeout expressed in milliseconds.
 * </p>
 * <p>
 * The contract for timeout based executions is the following, in addition to
 * the general {@code TaskRunnerService} contract:
 * <ul>
 * <li>When the timeout is reached, the {@code execute()} methods must return as
 * soon as possible. Every unfinished task must be interrupted</li>
 * <li>The timeout runs from the beginning of the beginning of the execution of
 * the {@code execute()} method to the end of the {@code taskFinished()} method
 * on the listeners. If the timeout is over before, the task should be
 * interrupted</li>
 * </ul>
 * </p>
 *
 * @author julien
 *
 */
public abstract class AbstractTimeoutTaskRunnerService extends
	AbstractTaskRunnerService {

    /** Timeout value */
    private long timeout;

    /**
     * Sets the timeout value
     *
     * @param timeout
     *            Value in ms
     */
    public void setTimeout(final long timeout) {
	this.timeout = timeout;
    }

    /**
     * Return the timeout value
     *
     * @return timeout
     */
    protected long getTimeout() {
	return timeout;
    }

    /**
     * Return true if a timeout is set (i.e. any value but 0)
     *
     * @return {@code true} is a timeout is set
     */
    protected boolean hasTimeout() {
	return timeout != 0;
    }

}
