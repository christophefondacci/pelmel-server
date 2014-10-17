package com.videopolis.apis.model.base;

import java.util.List;

import com.videopolis.apis.delegate.impl.AliasableDelegate;
import com.videopolis.apis.delegate.impl.PaginableDelegate;
import com.videopolis.apis.delegate.impl.SortableDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.Paginable;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.Sortable;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;

/**
 * Base implementation for {@link WithCriterion}.
 * 
 * @author Christophe Fondacci
 * 
 */
public abstract class AbstractWithCriterion extends AbstractApisCriterion
	implements WithCriterion {

    /** Delegate for {@link Aliasable} implementation */
    private final Aliasable<WithCriterion> aliasableDelegate = new AliasableDelegate<WithCriterion>(
	    this);

    /** Delegate for {@link Paginable} implementation */
    private final Paginable<WithCriterion> paginableDelegate = new PaginableDelegate<WithCriterion>(
	    this);

    /** Delegate for {@link Sortable} implementation */
    private final Sortable<WithCriterion> sortableDelegate = new SortableDelegate<WithCriterion>(
	    this);

    private RequestType requestType;

    @Override
    public PaginationSettings getPagination() {
	return paginableDelegate.getPagination();
    }

    @Override
    public void setPagination(final PaginationSettings window) {
	paginableDelegate.setPagination(window);
    }

    @Override
    public WithCriterion paginatedBy(final int pageSize, final int pageOffset) {
	return paginableDelegate.paginatedBy(pageSize, pageOffset);
    }

    @Override
    public <U extends Sorter> WithCriterion sortBy(final U... sorters) {
	return sortableDelegate.sortBy(sorters);
    }

    @Override
    public <U extends Sorter> WithCriterion sortBy(final List<U> sorters) {
	return sortableDelegate.sortBy(sorters);
    }

    @Override
    public List<Sorter> getSorters() {
	return sortableDelegate.getSorters();
    }

    @Override
    public WithCriterion with(final WithCriterion innerCriterion)
	    throws ApisException {
	Assert.notNull(innerCriterion,
		"Cannot build a with clause using a null criterion");
	addCriterion(innerCriterion);
	return this;
    }

    public RequestType getRequestType() {
	return requestType;
    }

    public void setRequestType(final RequestType requestType) {
	this.requestType = requestType;
    }

    @Override
    public String getAlias() {
	return aliasableDelegate.getAlias();
    }

    @Override
    public WithCriterion aliasedBy(final String alias) throws ApisException {
	return aliasableDelegate.aliasedBy(alias);
    }

}
