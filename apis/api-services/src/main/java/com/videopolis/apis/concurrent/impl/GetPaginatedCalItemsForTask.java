package com.videopolis.apis.concurrent.impl;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.ApisSearchHelper;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.apis.model.PaginationSettings;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

/**
 * A ApisTask invoking a
 * {@link CalService#getPaginatedItemsFor(ItemKey, com.videopolis.cals.model.CalContext, int, int)}
 * CAL service method.
 * 
 * @author Christophe Fondacci
 * 
 */
public class GetPaginatedCalItemsForTask extends AbstractApisTask {

	private final CalService service;
	private final ItemKey parentItemKey;
	private final ApisContext context;
	private final PaginationSettings paginationWindow;
	private final RequestType requestType;

	/**
	 * Creates this task based on the given information.
	 * 
	 * @param service
	 *            the {@link CalService} to use
	 * @param context
	 *            the {@link ApisContext} to use
	 * @param paginationWindow
	 *            pagination information to pass to the service
	 * @param parentItem
	 *            parent item of the "for" call
	 */
	public GetPaginatedCalItemsForTask(CalService service, ApisContext context,
			PaginationSettings paginationWindow, ItemKey parentItem,
			RequestType requestType) {
		this.service = service;
		this.context = context;
		this.paginationWindow = paginationWindow;
		this.parentItemKey = parentItem;
		this.requestType = requestType;
	}

	@Override
	public ItemsResponse doExecute(TaskExecutionContext taskExecutionContext)
			throws TaskExecutionException {
		final int itemsByPage = paginationWindow.getItemsByPage();
		final int pageNumber = paginationWindow.getPageOffset();
		try {
			// Calling CAL with pagination
			final PaginatedItemsResponse response = service
					.getPaginatedItemsFor(parentItemKey,
							context.getCalContext(), itemsByPage, pageNumber,
							requestType);
			// Registering pagination info
			ApisSearchHelper.fillPaginationInformation(
					context.getApiResponse(), getCriterion(), response);

			return response;
		} catch (CalException e) {
			throw new TaskExecutionException("Problems while calling "
					+ service.toString() + ".getPaginatedItemsFor("
					+ parentItemKey.toString() + "," + context.toString() + ","
					+ itemsByPage + "," + pageNumber + "): " + e.getMessage(),
					e);
		}
	}

	@Override
	public String toString() {
		return "getPaginatedCalItemsFor;"
				+ (service == null ? "null" : ApisRegistry
						.getTypeForCalService(service))
				+ ";"
				+ parentItemKey
				+ ","
				+ (paginationWindow == null ? "Null pagination"
						: paginationWindow.toString());
	}
}
