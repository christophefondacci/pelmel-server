package com.videopolis.calm.model.impl;

import com.videopolis.calm.model.Sorter;

/**
 * Default implementation of {@link Sorter}
 * 
 * @author julien
 * 
 */
public class SorterImpl implements Sorter {

    /** Sort criterion */
    private final String criterion;

    /** Sort order */
    private final Order order;

    /**
     * Default constructor
     * 
     * @param criterion
     *            Criterion
     * @param order
     *            Order
     */
    public SorterImpl(final String criterion, final Order order) {
	this.criterion = criterion;
	this.order = order;
    }

    @Override
    public String getCriterion() {
	return criterion;
    }

    @Override
    public Order getOrder() {
	return order;
    }

}
