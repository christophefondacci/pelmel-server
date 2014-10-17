package com.videopolis.apis.model;

import com.videopolis.apis.exception.ApisException;

/**
 * A "with" criteria defines the information we want to fetch <i>with</i> the
 * main item type retrieved by the request. For example, you may want to build
 * an {@link ApisRequest} which retrieves a set of hotels <i>with</i> their
 * reviews in which case you would add a {@link WithCriterion} for the REVIEW
 * item type.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface WithCriterion extends ApisCriterion, Paginable<WithCriterion>,
	Sortable<WithCriterion>, Aliasable<WithCriterion> {

    /**
     * Integrates an inner "with" criteria inside this one.
     * 
     * @param innerCriteria
     *            information to fetch <i>with</i> the parent with type
     * @return this same criteria, this is a pass-through method for convenience
     *         of method calls
     */
    WithCriterion with(WithCriterion innerCriteria) throws ApisException;

}
