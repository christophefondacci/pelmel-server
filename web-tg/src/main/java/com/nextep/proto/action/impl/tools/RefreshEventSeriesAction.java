package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.EventSeries;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.services.EventManagementService;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.service.TaskRunnerService;

public class RefreshEventSeriesAction extends AbstractAction {

	private static final long serialVersionUID = 5950485400657646357L;
	private static final Log LOGGER = LogFactory
			.getLog(RefreshEventSeriesAction.class);

	private static final int PAGE_SIZE = 500;
	private static final String APIS_ALIAS_SERIES = "series";

	private TaskRunnerService taskRunnerService;
	private EventManagementService eventManagementService;
	private List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					refreshEventSeries();
				} catch (ApisException e) {
					LOGGER.error(
							"Problem while cleaning events : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				} catch (RuntimeException e) {
					LOGGER.error(
							"Problem while cleaning events : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				}
				return null;
			};
		});
		messages.add("Refresh of event series process has been successfully started.");
		return SUCCESS;
	}

	private void refreshEventSeries() throws ApisException {
		ApisRequest request = buildRequest(0);
		LOGGER.info("Listing all event series");
		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		// Getting results
		final PaginationInfo pagination = response
				.getPaginationInfo(APIS_ALIAS_SERIES);
		LOGGER.info("Retrieved " + pagination.getPageCount()
				+ " pages of series on a total of " + pagination.getItemCount()
				+ ", processing...");
		for (int i = 0; i < pagination.getPageCount(); i++) {
			List<? extends EventSeries> seriesList = response.getElements(
					EventSeries.class, APIS_ALIAS_SERIES);
			LOGGER.info("Processing page " + (i + 1) + ": " + seriesList.size()
					+ " series to refresh");
			for (EventSeries series : seriesList) {
				LOGGER.info("  > Processing series " + series.getKey());
				eventManagementService.refreshEventSeries(series);
			}
			request = buildRequest(i + 1);
			response = (ApiCompositeResponse) getApiService().execute(request,
					ContextFactory.createContext(getLocale()));
		}
		LOGGER.info("All event series refreshed, done.");
	}

	private ApisRequest buildRequest(int page) throws ApisException {
		return (ApisRequest) ApisFactory.createCompositeRequest().addCriterion(
				SearchRestriction.list(EventSeries.class, null)
						.paginatedBy(500, page).aliasedBy(APIS_ALIAS_SERIES));
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setTaskRunnerService(TaskRunnerService taskRunnerService) {
		this.taskRunnerService = taskRunnerService;
	}

	public void setEventManagementService(
			EventManagementService eventManagementService) {
		this.eventManagementService = eventManagementService;
	}
}
