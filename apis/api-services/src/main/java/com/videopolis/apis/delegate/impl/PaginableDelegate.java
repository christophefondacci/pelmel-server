package com.videopolis.apis.delegate.impl;

import com.videopolis.apis.delegate.base.AbstractOwnedDelegate;
import com.videopolis.apis.model.Paginable;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.impl.PaginationSettingsImpl;

/**
 * <p>
 * A default delegate implementation of {@link Paginable}
 * </p>
 * <p>
 * This implementation provides the default behavior for {@link Paginable}:
 * {@link #setPagination(PaginationSettings)} and {@link #paginatedBy(int, int)}
 * allow to store a pagination hold by this object which can be retrieved using
 * {@link #getPagination()}
 * </p>
 * <p>
 * If no pagination is set, {@link #getPagination()} will return {@code null}
 * </p>
 * 
 * @author julien
 * 
 * @param <T>
 *            Type of object owning the delegate
 */
public class PaginableDelegate<T> extends AbstractOwnedDelegate<T> implements
	Paginable<T> {

    /** The pagination */
    private PaginationSettings pagination;

    /**
     * Default constructor
     * 
     * @param owner
     *             Owner of this delegate
     */
    public PaginableDelegate(final T owner) {
	super(owner);
    }

    @Override
    public PaginationSettings getPagination() {
	return pagination;
    }

    @Override
    public void setPagination(final PaginationSettings pagination) {
	this.pagination = pagination;
    }

    @Override
    public T paginatedBy(final int pageSize, final int pageOffset) {
	pagination = new PaginationSettingsImpl();
	pagination.setItemsByPage(pageSize);
	pagination.setPageOffset(pageOffset);
	return getOwner();
    }

}
