package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.exception.ApisNoSuchElementException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.smaug.common.model.SearchScope;

public class CleanPastEventsAction extends AbstractAction {

	private static final Log LOGGER = LogFactory
			.getLog(CleanPastEventsAction.class);
	private static final long serialVersionUID = 2268585738057513476L;
	private static final int PAGE_SIZE = 100;

	private TaskRunnerService taskRunnerService;
	private SearchPersistenceService searchService;
	private final List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					cleanEvents();
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
		messages.add("Clean process has been successfully started.");
		return SUCCESS;
	}

	private void cleanEvents() throws ApisException {
		// Searching for every favorited events
		ApisRequest request = buildRequest(0);
		// Executing search
		ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		// Iterating over users
		final PaginationInfo pagination = response
				.getPaginationInfo(User.class);
		ContextHolder.toggleWrite();
		for (int i = 0; i < pagination.getPageCount(); i++) {
			final List<? extends User> users = (List) response.getElements();
			for (User user : users) {
				// Extracting favorite events
				final List<? extends Event> events = user.get(Event.class);
				// Iterating over events to remove any past event
				final List<ItemKey> eventKeys = new ArrayList<ItemKey>();
				for (Event e : events) {
					if (e.getEndDate().getTime() > System.currentTimeMillis()) {
						eventKeys.add(e.getKey());
					}
				}
				// Refreshing search if needed
				LOGGER.info("Updated " + user.getKey().toString()
						+ " : events " + eventKeys.toString());
				searchService.updateWithRemoval(user, eventKeys);
			}
			// Fetching next page
			request = buildRequest(i + 1);
			try {
				response = getApiService().execute(request,
						ContextFactory.createContext(getLocale()));
			} catch (ApisNoSuchElementException e) {
				break;
			}
		}
	}

	private ApisRequest buildRequest(int page) throws ApisException {
		return (ApisRequest) ApisFactory.createRequest(User.class)
				.searchAll(User.class, SearchScope.CHILDREN, PAGE_SIZE, page)
				.with(Event.class, EventRequestTypes.ALL_EVENTS);
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
