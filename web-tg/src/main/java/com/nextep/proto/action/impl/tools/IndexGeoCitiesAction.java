package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.impl.RequestTypeMinPopulation;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
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
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.exception.SearchException;

public class IndexGeoCitiesAction extends AbstractAction {

	private static final Log LOGGER = LogFactory
			.getLog(IndexGeoCitiesAction.class);
	private static final long serialVersionUID = 8242078679018166793L;
	private static final String APIS_ALIAS_CITIES = "cities";
	private static final int PAGE_SIZE = 500;
	private SearchPersistenceService searchService;
	private List<String> messages = new ArrayList<String>();
	private TaskRunnerService taskRunnerService;

	private int minCityPopulation;

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					indexCities();
				} catch (ApisException e) {
					LOGGER.error(
							"Problem while indexing cities : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				} catch (RuntimeException e) {
					LOGGER.error(
							"Problem while indexing cities : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				}
				return null;
			};
		});
		messages.add("Cities indexation process has been successfully started.");
		return SUCCESS;
	}

	private void indexCities() throws ApisException {
		int offset = 0;
		int item = 1;
		boolean hasMore = true;
		while (hasMore) {
			final ApisRequest request = buildRequest(offset++);
			ContextHolder.toggleReadonly();
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));
			final PaginationInfo paginationInfo = response
					.getPaginationInfo(APIS_ALIAS_CITIES);
			// Updating offset and preparing next loop
			final int itemCount = paginationInfo.getItemCount();
			hasMore = itemCount > offset + PAGE_SIZE;
			// Retrieving fetched cities
			final List<? extends City> cities = response.getElements(
					City.class, APIS_ALIAS_CITIES);
			// Now writing
			ContextHolder.toggleWrite();
			// Reindexing cities
			for (City city : cities) {
				LOGGER.info("Storing city '" + city.getName() + "' [" + item
						+ " / " + itemCount + "]");
				item++;
				try {
					searchService.storeCalmObject(city, SearchScope.CITY);
				} catch (SearchException e) {
					LOGGER.error(" ==> FAILED : " + e.getMessage());
				}
			}
		}
	}

	private ApisRequest buildRequest(int offset) throws ApisException {
		return (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(
						SearchRestriction
								.list(GeographicItem.class,
										new RequestTypeMinPopulation(
												minCityPopulation))
								.paginatedBy(PAGE_SIZE, offset)
								.aliasedBy(APIS_ALIAS_CITIES));
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setTaskRunnerService(TaskRunnerService taskRunnerService) {
		this.taskRunnerService = taskRunnerService;
	}

	public void setMinCityPopulation(int minCityPopulation) {
		this.minCityPopulation = minCityPopulation;
	}
}
