package com.videopolis.apis.delegate.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.videopolis.apis.delegate.base.AbstractOwnedDelegate;
import com.videopolis.apis.model.Sortable;
import com.videopolis.calm.model.Sorter;

/**
 * <p>
 * A default delegate implementation of {@link Sortable}
 * </p>
 * <p>
 * This implementation provides the default behavior for {@link Sortable}: The
 * sorters can be set using {@link #sortBy(List)} or {@link #sortBy(Sorter...)}
 * and returned using {@link #getSorters()}.
 * </p>
 * <p>
 * By default, if no sorter is set, {@link #getSorters()} will return an empty
 * list.
 * </p>
 * 
 * @author julien
 * 
 * @param <T>
 *            Type of object owning the delegate
 */
public class SortableDelegate<T> extends AbstractOwnedDelegate<T> implements
	Sortable<T> {

    /** The sorters */
    private List<? extends Sorter> sorters = Collections.emptyList();

    /**
     * Default constructor
     * 
     * @param owner
     *            Owner of this delegate
     */
    public SortableDelegate(final T owner) {
	super(owner);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Sorter> getSorters() {
	return (List<Sorter>) sorters;
    }

    @Override
    public T sortBy(final Sorter... sorters) {
	this.sorters = Arrays.asList(sorters);
	return getOwner();
    }

    @Override
    public <U extends Sorter> T sortBy(final List<U> sorters) {
	this.sorters = sorters;
	return getOwner();
    }

}
