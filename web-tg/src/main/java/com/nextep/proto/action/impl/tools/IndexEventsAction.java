package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.concurrent.exception.TaskExecutionException;
import com.videopolis.concurrent.model.TaskExecutionContext;
import com.videopolis.concurrent.model.base.AbstractTask;
import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.exception.SearchException;

public class IndexEventsAction extends AbstractAction {

	private static final Log LOGGER = LogFactory
			.getLog(IndexEventsAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 8242078679018166793L;
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private SearchPersistenceService searchService;
	private TaskRunnerService taskRunnerService;
	private boolean newOnly = false;
	private List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					indexEvents();
				} catch (ApisException e) {
					LOGGER.error(
							"Problem while indexing events : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				} catch (RuntimeException e) {
					LOGGER.error(
							"Problem while indexing events : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				}
				return null;
			};
		});
		messages.add("Events indexation process has been successfully started.");
		return SUCCESS;
	}

	private void indexEvents() throws ApisException {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createRequest(Event.class).list(Event.class, null)
				.with(Tag.class)
				.addCriterion(SearchRestriction.adaptKey(eventLocationAdapter));
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final Collection<Event> events = (Collection<Event>) response
				.getElements();
		ContextHolder.toggleWrite();
		// Now we safely fetched our events we purge solr
		// searchService.removeAll(Event.CAL_ID);
		final int count = events.size();
		int i = 1;
		for (Event event : events) {
			String msg = "Storing event '" + event.getName() + "' [" + i
					+ " / " + count + "] ";
			i++;
			if (!newOnly
					|| event.getStartDate().getTime() > System
							.currentTimeMillis()) {
				try {
					searchService.storeCalmObject(event, SearchScope.CHILDREN);
					msg = msg + " -> SUCCESS!";
				} catch (SearchException e) {
					msg = msg + " -> FAILED : " + e.getMessage();
					LOGGER.error(msg, e);
				} catch (RuntimeException e) {
					msg = msg + " -> FAILED (runtime exception: "
							+ e.getMessage() + ")";
					LOGGER.error(msg, e);
				}
				LOGGER.info(msg);
			}
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

	public void setNewOnly(boolean newOnly) {
		this.newOnly = newOnly;
	}

	public boolean getNewOnly() {
		return newOnly;
	}
}
