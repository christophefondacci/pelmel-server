package com.nextep.proto.action.impl.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.model.Constants;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
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

public class ReIndexUsersAction extends AbstractAction {

	private static final long serialVersionUID = 2505734348197235844L;
	private static final Log LOGGER = LogFactory
			.getLog(ReIndexUsersAction.class);
	private SearchPersistenceService searchPersistenceService;
	private TaskRunnerService taskRunnerService;
	private List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					reindexUsers();
				} catch (ApisException e) {
					LOGGER.error(
							"Problem while indexing users : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				} catch (RuntimeException e) {
					LOGGER.error(
							"Problem while indexing users : " + e.getMessage(),
							e);
					throw new TaskExecutionException(e);
				}
				return null;
			};
		});
		messages.add("Users re-indexation process has been successfully started.");
		return SUCCESS;
	}

	private void reindexUsers() throws ApisException {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createRequest(User.class).list(User.class, null)
				.with(GeographicItem.class).with(Media.class).with(Tag.class)
				.with(Place.class, Constants.APIS_ALIAS_FAVORITE)
				.with(Event.class, Constants.APIS_ALIAS_FAVORITE)
				.with(User.class, Constants.APIS_ALIAS_FAVORITE);

		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		// Removing all previous users
		searchPersistenceService.removeAll(User.CAL_TYPE);
		// Reindexing everything
		final List<User> users = (List<User>) response.getElements();
		for (User u : users) {
			final StringBuilder buf = new StringBuilder();
			try {
				buf.append("Indexing user " + u.getKey() + "...");
				searchPersistenceService.storeCalmObject(u,
						SearchScope.CHILDREN);
				buf.append("OK");
			} catch (RuntimeException e) {
				buf.append("ERROR: " + e.getMessage());
				LOGGER.error(
						"Error indexing user " + u.getKey() + " : "
								+ e.getMessage(), e);
			} finally {
				LOGGER.info(buf.toString());
			}
		}
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setSearchPersistenceService(
			SearchPersistenceService searchPersistenceService) {
		this.searchPersistenceService = searchPersistenceService;
	}

	public void setTaskRunnerService(TaskRunnerService taskRunnerService) {
		this.taskRunnerService = taskRunnerService;
	}
}
