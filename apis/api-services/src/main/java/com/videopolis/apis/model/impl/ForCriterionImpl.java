package com.videopolis.apis.model.impl;

import com.videopolis.apis.concurrent.impl.GetCalItemsForTask;
import com.videopolis.apis.concurrent.impl.GetPaginatedCalItemsForTask;
import com.videopolis.apis.concurrent.model.ApisTask;
import com.videopolis.apis.delegate.impl.AliasableDelegate;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.Aliasable;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.CriteriaContainer;
import com.videopolis.apis.model.ForCriterion;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.apis.model.base.AbstractApisCriterion;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.model.Task;

/**
 * Implementation of a "for" {@link ApisCriterion} able to retrieve elements
 * <i>for</i> a given {@link ItemKey}.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ForCriterionImpl extends AbstractApisCriterion implements
		ForCriterion {

	private final Aliasable<ForCriterion> aliasableDelegate = new AliasableDelegate<ForCriterion>(
			this);

	private final ItemKey parentKey;
	private PaginationSettings pagination;
	private String calType = null;

	/**
	 * Builds a new FOR criterion which will retrieve all elements FOR the given
	 * {@link ItemKey}.
	 * 
	 * @param parentKey
	 *            {@link ItemKey} to retrieve elements <i>for</i>
	 */
	public ForCriterionImpl(final ItemKey parentKey) {
		this.parentKey = parentKey;
		pagination = null;
	}

	/**
	 * Builds a new FOR criterion which will retrieve paginated elements FOR the
	 * given {@link ItemKey}.
	 * 
	 * @param parentKey
	 *            {@link ItemKey} to retrieve elements <i>for</i>.
	 * @param itemsPerPage
	 *            number of elements per page (= max number of elements to
	 *            fetch)
	 * @param pageNumber
	 *            number of the page to retrieve
	 */
	public ForCriterionImpl(final ItemKey parentKey, final int itemsPerPage,
			final int pageNumber) {
		this(parentKey);
		pagination = new PaginationSettingsImpl();
		pagination.setItemsByPage(itemsPerPage);
		pagination.setPageOffset(pageNumber);
	}

	@Override
	public Task<ItemsResponse> getTask(final CriteriaContainer parent,
			final ApisContext context, final CalmObject... parentObjects)
			throws ApisException {
		// If a type has been explicitly specified (i.e. root FOR criterion in
		// composite requests), we use it, otherwise we use the parent type
		String calType = getType();
		if (calType == null) {
			calType = parent.getType();
		}
		// Retrieving the CAL service for the parent type
		final CalService service = ApisRegistry.getCalService(calType);
		// Converting parent objects into key collection
		final ItemKey[] parentKeys = new ItemKey[] { parentKey };
		ApisTask<ItemsResponse> task = null;
		// Paginated or regular task depending on the existence of pagination
		// info
		if (pagination != null) {
			task = new GetPaginatedCalItemsForTask(service, context,
					pagination, parentKeys[0], null);
		} else {
			task = new GetCalItemsForTask(service, context, parentKeys);
		}
		task.setCriterion(this);
		return task;
	}

	@Override
	public String getType() {
		return calType;
	}

	public void setType(final String calType) {
		this.calType = calType;
	}

	@Override
	public String getAlias() {
		return aliasableDelegate.getAlias();
	}

	@Override
	public ForCriterion aliasedBy(final String alias) throws ApisException {
		return aliasableDelegate.aliasedBy(alias);
	}
}
