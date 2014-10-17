package com.videopolis.apis.concurrent.impl;

import com.videopolis.apis.concurrent.base.AbstractApisTask;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.ApisSearchHelper;
import com.videopolis.apis.model.ApisContext;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.RequestSettings;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;

public class ListCalItemsTask extends AbstractApisTask {

	private final CalService service;
	private final ApisContext apisContext;
	private final RequestSettings requestSettings;
	private final RequestType requestType;

	public ListCalItemsTask(final CalService service,
			final ApisContext apisContext,
			final RequestSettings requestSettings, final RequestType requestType) {
		this.service = service;
		this.apisContext = apisContext;
		this.requestSettings = requestSettings;
		this.requestType = requestType;
	}

	@Override
	protected ItemsResponse doExecute(final TaskExecutionContext context)
			throws TaskExecutionException, InterruptedException {
		try {
			final ItemsResponse response = service.listItems(
					apisContext.getCalContext(), requestType, requestSettings);
			if (response instanceof PaginatedItemsResponse) {
				// Registering pagination info
				ApisSearchHelper.fillPaginationInformation(
						apisContext.getApiResponse(), getCriterion(),
						(PaginatedItemsResponse) response);
			}
			return response;
		} catch (final CalException e) {
			throw new TaskExecutionException(
					"Problems while calling CalService.listItems("
							+ apisContext + "," + requestType + ","
							+ requestSettings + "): " + e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		return "listCalItems;"
				+ (service == null ? "null" : ApisRegistry
						.getTypeForCalService(service)) + ";" + requestType
				+ "," + requestSettings;
	}
}
