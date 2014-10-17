package com.videopolis.apis.model.impl;

import java.util.List;

import com.videopolis.apis.concurrent.impl.ListCalItemsTask;
import com.videopolis.apis.delegate.impl.AliasableDelegate;
import com.videopolis.apis.delegate.impl.PaginableDelegate;
import com.videopolis.apis.delegate.impl.SortableDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.ListCriterion;
import com.videopolis.apis.model.Paginable;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.Sortable;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.RequestSettingsFactory;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * Default implementation of {@link ListCriterion}
 * 
 * @author julien
 * 
 */
public class ListCriterionImpl extends AbstractApisCriterion implements
	ListCriterion {

    private final String itemType;
    private final RequestType requestType;

    private final Paginable<ListCriterion> paginableDelegate = new PaginableDelegate<ListCriterion>(
	    this);
    private final Sortable<ListCriterion> sortableDelegate = new SortableDelegate<ListCriterion>(
	    this);
    private final Aliasable<ListCriterion> aliasableDelegate = new AliasableDelegate<ListCriterion>(
	    this);

    public ListCriterionImpl(final String itemType,
	    final RequestType requestType) throws ApisException {
	Assert.notNull(itemType,
		"Cannot create a list clause using a null item type");
	this.itemType = itemType;
	this.requestType = requestType;
    }

    @Override
    public Task<ItemsResponse> getTask(final CriteriaContainer parent,
	    final ApisContext context, final CalmObject... parentObjects)
	    throws ApisException {

	// Retrieving the CAL service for the type of this criterion
	final CalService service = ApisRegistry.getCalService(getType());

	final RequestSettings requestSettings;
	if (getPagination() != null) {
	    requestSettings = RequestSettingsFactory
		    .createPaginationRequestSettings(getPagination()
			    .getItemsByPage(), getPagination().getPageOffset(),
			    getSorters());
	} else {
	    requestSettings = RequestSettingsFactory
		    .createRequestSettings(getSorters());
	}

	return new ListCalItemsTask(service, context, requestSettings,
		requestType);
    }

    @Override
    public String getType() {
	return itemType;
    }

    @Override
    public PaginationSettings getPagination() {
	return paginableDelegate.getPagination();
    }

    @Override
    public void setPagination(final PaginationSettings pagination) {
	paginableDelegate.setPagination(pagination);
    }

    @Override
    public ListCriterion paginatedBy(final int pageSize, final int pageOffset) {
	return paginableDelegate.paginatedBy(pageSize, pageOffset);
    }

    @Override
    public List<Sorter> getSorters() {
	return sortableDelegate.getSorters();
    }

    @Override
    public ListCriterion sortBy(final Sorter... sorters) {
	return sortableDelegate.sortBy(sorters);
    }

    @Override
    public <U extends Sorter> ListCriterion sortBy(final List<U> sorters) {
	return sortableDelegate.sortBy(sorters);
    }

    @Override
    public String getAlias() {
	return aliasableDelegate.getAlias();
    }

    @Override
    public ListCriterion aliasedBy(final String alias) throws ApisException {
	return aliasableDelegate.aliasedBy(alias);
    }

}
