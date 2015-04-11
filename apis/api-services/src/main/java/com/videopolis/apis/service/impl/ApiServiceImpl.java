package com.videopolis.apis.service.impl;

import java.util.List;

import com.videopolis.apis.concurrent.impl.ApisRequestTask;
import com.videopolis.apis.concurrent.impl.listener.ApisResponseTaskListener;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.exception.ApisNoSuchElementException;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.helper.Assert;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.impl.ApisContextImpl;
import com.videopolis.apis.service.ApiMutableResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.apis.service.ApiService;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.service.CalService;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.smaug.service.SearchService;

/**
 * Default and unique implementation of the {@link ApiService}. This class
 * should not be extended.
 * 
 * @author Christophe Fondacci
 * 
 */
public class ApiServiceImpl implements ApiService {

	/** Task runner for our sub tasks (atomic timeout) */
	private TaskRunnerService taskRunnerService;
	/** Task runner for our {@link ApisRequest} execution */
	private TaskRunnerService requestTaskRunnerService;
	private SearchService searchService;

	@Override
	public ApiResponse execute(ApisRequest request, CalContext context)
			throws ApisException {
		// Creating a new APIS response
		final ApiMutableResponse apiResponse = createResponse(request);

		// Creating and initializing APIS context
		final ApisContextImpl apisContext = new ApisContextImpl();
		apisContext.setApiResponse(apiResponse);
		apisContext.setCalContext(context);
		apisContext.setSearchService(getSearchService());
		apisContext.setApiService(this);

		// Our wrapper task which manages execution of our criteria
		final ApisRequestTask requestTask = new ApisRequestTask(request,
				apisContext);
		final ApisResponseTaskListener requestListener = new ApisResponseTaskListener();
		requestTask.addTaskListener(requestListener);
		requestTask.setTaskRunnerService(taskRunnerService);
		requestTaskRunnerService.execute(requestTask);
		// Extracting root response
		final ItemsResponse response = requestListener.getResponse();
		// Failure when root element unavailable
		if (isEmptyResponse(response)) {
			// If we had a failure or interruption, we raise a technical APIS
			// error
			if (requestListener.hasFailed() || requestListener.isInterrupted()) {
				throw new ApisException(
						"Problems while executing the APIS request: unable to get the root element from query: "
								+ request.toString(),
						requestListener.getException());
			} else {
				if (!request.isEmptyResultsAllowed()) {
					// If the response was successful, but no root item, this is
					// a
					// specific NOT FOUND exception as we could be sure that the
					// element does not exist (this will result in a 404)
					throw new ApisNoSuchElementException(
							"Requested root element of this APIS request does not exist: "
									+ request.getRootCriterion());
				}
			}
		}
		// Setting partial status of the response
		if (!apiResponse.isPartial()) {
			apiResponse.setPartial(requestListener.hasFailed()
					|| requestListener.isInterrupted());
		}
		// Setting returned elements
		apiResponse.setElements(response.getItems());
		return apiResponse;
	}

	private boolean isEmptyResponse(ItemsResponse response) {
		return response == null || response.getItems().isEmpty();
	}

	private ApiMutableResponse createResponse(ApisRequest request)
			throws ApisException {
		// Creating response
		final Class<? extends ApiMutableResponse> responseClass = request
				.getResponseClass();
		Assert.notNull(responseClass,
				"The APIS request does not provide a valid response class");
		try {
			final ApiMutableResponse apiResponse = responseClass.newInstance();
			return apiResponse;
		} catch (IllegalAccessException e) {
			throw new ApisException(
					"Unable to instantiate APIS response class (does "
							+ responseClass
							+ " has an accessible empty constructor ?", e);
		} catch (InstantiationException e) {
			throw new ApisException(
					"Unable to instantiate APIS response class (does "
							+ responseClass
							+ " has an accessible empty constructor ?", e);
		}
	}

	/**
	 * An injection setter allowing spring to inject all registered services and
	 * to wire services through spring context file.
	 * 
	 * @param services
	 *            list of services to register
	 */
	public void setCalServices(List<CalService> services) {
		// Registering every service from the list
		for (CalService service : services) {
			ApisRegistry.registerCalService(service);
		}
	}

	/**
	 * Spring injection setter of the {@link TaskRunner} implementation
	 * 
	 * @param taskRunner
	 *            task runner implementation
	 */
	public void setTaskRunnerService(TaskRunnerService taskRunnerService) {
		this.taskRunnerService = taskRunnerService;
	}

	/**
	 * Spring injection setter of the {@link TaskRunnerService} implementation.
	 * 
	 * @param requestTaskRunnerService
	 *            task runner which handles ApisRequest execution
	 */
	public void setRequestTaskRunnerService(
			TaskRunnerService requestTaskRunnerService) {
		this.requestTaskRunnerService = requestTaskRunnerService;
	}

	/**
	 * Spring injection setter for the {@link SearchService} implementation
	 * 
	 * @param searchService
	 *            search service implementation
	 */
	public final void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}

	protected final SearchService getSearchService() {
		return searchService;
	}
}
