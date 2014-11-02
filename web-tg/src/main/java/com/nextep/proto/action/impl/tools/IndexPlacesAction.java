package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.RequestTypeWithAlternates;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.exception.SearchException;

public class IndexPlacesAction extends AbstractAction {

	private static final Log LOGGER = LogFactory
			.getLog(IndexPlacesAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 8242078679018166793L;
	private SearchPersistenceService searchService;
	private TaskRunnerService taskRunnerService;
	private List<String> messages = new ArrayList<String>();;

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					indexPlaces();
				} catch (ApisException e) {
					LOGGER.error(
							"Problem while indexing places : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				} catch (RuntimeException e) {
					LOGGER.error(
							"Problem while indexing places : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				}
				return null;
			};
		});
		messages.add("Places indexation process has been successfully started.");
		return SUCCESS;
	}

	private void indexPlaces() throws ApisException {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createRequest(Place.class)
				.list(Place.class, RequestTypeWithAlternates.WITH_ALTERNATES)
				.with(Tag.class);
		LOGGER.info("Loading all places to index");
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final Collection<Place> places = (Collection<Place>) response
				.getElements();
		// Now that we have the elements, we can safely remove
		// TODO remove this for PROD
		// searchService.removeAll(Place.CAL_TYPE);
		ContextHolder.toggleWrite();
		// Reindexing places
		final int count = places.size();
		int i = 1;
		for (Place place : places) {
			String msg = "Storing place '" + place.getName() + "'...";
			try {
				searchService.storeCalmObject(place, SearchScope.CHILDREN);
				// searchService.storeSuggest(place.getKey(),
				// Arrays.asList(place.getName()));
				msg = msg + "SUCCESS!";
			} catch (SearchException e) {
				msg = msg + "FAILED : " + e.getMessage();
			}
			LOGGER.info("[" + (i++) + " / " + count + "] : " + msg);
		}
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
}
