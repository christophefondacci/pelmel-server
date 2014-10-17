package com.videopolis.calm.factory;

import com.videopolis.calm.model.Sorter;
import com.videopolis.calm.model.impl.SorterImpl;

/**
 * A static factory class used to create {@link Sorter} instances
 * 
 * @author julien
 * 
 */
public final class SorterFactory {

    private SorterFactory() {
    }

    /**
     * Creates a {@link Sorter} object
     * 
     * @param criterion
     *            Sort criterion
     * @param order
     *            Sort order
     * @return Sorter
     */
    public static Sorter createSorter(final String criterion,
	    final Sorter.Order order) {
	return new SorterImpl(criterion, order);
    }
}
