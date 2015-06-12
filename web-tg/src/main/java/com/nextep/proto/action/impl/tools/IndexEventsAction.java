package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.events.model.EventRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
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

public class IndexEventsAction extends AbstractAction {

	private static final Log LOGGER = LogFactory
			.getLog(IndexEventsAction.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 8242078679018166793L;
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();
	private static final int PAGE_SIZE = 200;
	private static final String APIS_ALIAS_EVENTS = "events";

	private SearchPersistenceService searchService;
	private TaskRunnerService taskRunnerService;
	private boolean all = false;
	private boolean clearEvents = false;
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

		int page = 0;
		int itemsCount = 0;
		boolean hasMore = true;
		while (hasMore) {
			ContextHolder.toggleReadonly();

			// Building new request for this page
			final ApisRequest request = buildRequest(page++);

			// Querying data
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));

			// Getting page info
			final PaginationInfo pagination = response
					.getPaginationInfo(APIS_ALIAS_EVENTS);
			hasMore = pagination.getItemCount() > itemsCount + PAGE_SIZE;

			// Getting this page's elements
			final Collection<? extends Event> events = response.getElements(
					Event.class, APIS_ALIAS_EVENTS);

			ContextHolder.toggleWrite();
			// Now we safely fetched our events we purge solr
			if (clearEvents) {
				searchService.removeAll(Event.CAL_ID);
			}
			final long count = pagination.getItemCount();
			for (Event event : events) {
				String msg = "Storing event '" + event.getName() + "' ["
						+ itemsCount + " / " + count + "] ";
				itemsCount++;
				try {
					if (event.isOnline()) {
						searchService.storeCalmObject(event,
								SearchScope.CHILDREN);
					} else {
						searchService.remove(event);
					}
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

	private ApisRequest buildRequest(int page) throws ApisException {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.list(Event.class,
										all ? EventRequestTypes.ALL_EVENTS
												: null)
								.paginatedBy(PAGE_SIZE, page)
								.aliasedBy(APIS_ALIAS_EVENTS)
								.with(Tag.class)
								.addCriterion(
										SearchRestriction
												.adaptKey(eventLocationAdapter)));
		return request;
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

	public void setAll(boolean all) {
		this.all = all;
	}

	public boolean isAll() {
		return all;
	}

	public void setClearEvents(boolean clearEvents) {
		this.clearEvents = clearEvents;
	}

	public boolean isClearEvents() {
		return clearEvents;
	}
}
