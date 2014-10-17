package com.videopolis.apis.model;

/**
 * A criterion which retrieves a list of elements of a specific type with a
 * particular request type. The exact behavior of this method will depend on the
 * underlying CAL service.
 * 
 * @author julien
 * 
 */
public interface ListCriterion extends ApisCriterion, Paginable<ListCriterion>,
	Sortable<ListCriterion>, Aliasable<ListCriterion> {

}
