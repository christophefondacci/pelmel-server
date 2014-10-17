package com.videopolis.apis.concurrent.model;

import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.concurrent.event.TaskListener;

public interface ApisTaskListener extends TaskListener<ItemsResponse> {

    /**
     * Indicates whether the task this listener is observing has failed.<br>
     * <b>Warning:</b> This listener should only be used for one and only one
     * task execution. <b>Notice:</b> this method will always indicate a failure
     * if called before the task execution. This was the only way to handle task
     * interruption properly.
     * 
     * @return <code>true</code> if the task failed, else <code>true</code>
     */
    boolean hasFailed();

    /**
     * Indicates whether the taks this listener observes has been interrupted.<br>
     * We distinguish interruptions from failures as interruptions represents
     * unknown states or partial information while failures represents errors.
     * Because of the chained listener mechanism we could not consider that a
     * task failed when one of its child has been interrupted.
     * 
     * @return <code>true</code> when the task has been interrupted, else
     *         <code>false</code>
     */
    boolean isInterrupted();

}
