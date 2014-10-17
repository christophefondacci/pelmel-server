package com.videopolis.apis.concurrent.impl;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.ApisSearchHelper;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A ApisTask invoking a
 * {@link CalService#getCustomizedItemsFor(ItemKey,com.videopolis.cals.model.CalContext,RequestSettings)}
 * CAL service method.
 * 
 * @author julien
 * 
 */
public class GetCustomizedCalItemsForTask extends AbstractApisTask {

	private final CalService service;
	private final ItemKey parentItemKey;
	private final ApisContext context;
	private final RequestSettings requestSettings;

	/**
	 * Default constructor
	 * 
	 * @param service
	 *            The target service to call
	 * @param parentItemKey
	 *            The foreign item key
	 * @param context
	 *            The context
	 * @param requestSettings
	 *            The settings used to customize the request
	 */
	public GetCustomizedCalItemsForTask(final CalService service,
			final ItemKey parentItemKey, final ApisContext context,
			final RequestSettings requestSettings) {
		this.service = service;
		this.parentItemKey = parentItemKey;
		this.context = context;
		this.requestSettings = requestSettings;
	}

	@Override
	public ItemsResponse doExecute(
			final TaskExecutionContext taskExecutionContext)
			throws TaskExecutionException {
		try {
			// Calling CAL with pagination
			final ItemsResponse response = service.getCustomizedItemsFor(
					parentItemKey, context.getCalContext(), requestSettings);

			// Registering pagination info, if required
			if (response instanceof PaginatedItemsResponse) {
				ApisSearchHelper.fillPaginationInformation(
						context.getApiResponse(), getCriterion(),
						(PaginatedItemsResponse) response);
			}

			return response;
		} catch (final CalException e) {
			throw new TaskExecutionException("Problems while calling "
					+ service + ".getPaginatedItemsFor(" + parentItemKey + ","
					+ context + "," + requestSettings + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public String toString() {
		return "getCustomizedCalItemsFor;"
				+ (service == null ? "null" : ApisRegistry
						.getTypeForCalService(service)) + ";" + parentItemKey
				+ "," + requestSettings;
	}
}
