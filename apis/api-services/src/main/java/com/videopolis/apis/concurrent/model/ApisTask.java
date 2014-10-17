package com.videopolis.apis.concurrent.model;

import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.concurrent.model.Task;

/**
 * An {@link ApisTask} is an extension of a regular {@link Task} which provides
 * accessor methods on the {@link ApisCriterion} which has forked this task.
 * 
 * @author Christophe Fondacci
 * 
 * @param <V>
 */
public interface ApisTask<V> extends Task<V> {

    /**
     * Retrieves the {@link ApisCriterion} which spawned this task. Note that
     * the {@link ApisCriterion} might not be always set and this method could
     * therefore return <code>null</code>. Callers should never assume a non
     * null criterion.
     * 
     * @return the {@link ApisCriterion} which created this {@link Task}
     */
    ApisCriterion getCriterion();

    /**
     * Defines the parent criterion
     * 
     * @param parentCriterion
     */
    void setCriterion(ApisCriterion parentCriterion);
}
