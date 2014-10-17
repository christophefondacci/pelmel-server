package com.nextep.proto.action.impl.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.spring.ContextHolder;
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

public class GenerateMissingThumbsAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4951636441112839644L;
	private static final Log LOGGER = LogFactory
			.getLog(GenerateMissingThumbsAction.class);

	private MediaPersistenceSupport mediaPersistenceSupport;
	private CalPersistenceService mediaService;
	private TaskRunnerService taskRunnerService;
	private String localMediaDirectory;
	private boolean force = false;
	private boolean offline = false;
	private List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {
		taskRunnerService.execute(new AbstractTask() {
			@Override
			public Object execute(TaskExecutionContext context)
					throws TaskExecutionException, InterruptedException {
				try {
					generateThumbs();
				} catch (ApisException e) {
					LOGGER.error(
							"Problem while generating thumbs : "
									+ e.getMessage(), e);
					throw new TaskExecutionException(e);
				} catch (RuntimeException e) {
					LOGGER.error(
							"Problem while generating thumbs : "
									+ e.getMessage(), e);
					throw new TaskExecutionException(e);
				}
				return null;
			};
		});
		messages.add("Generate thumbs process has been successfully started.");
		return SUCCESS;
	}

	private void generateThumbs() throws ApisException {
		final ApisRequest request = ApisFactory.createRequest(Media.class)
				.list(Media.class, null);
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		final List<Media> media = (List<Media>) response.getElements();
		ContextHolder.toggleWrite();
		for (Media m : media) {
			// Checking offline state
			if (!m.isOnline()) {
				// We only process offline media if requested and if not a user
				// photo
				if (!offline
						|| User.CAL_TYPE
								.equals(m.getRelatedItemKey().getType())) {
					continue;
				}
			}
			// If incorrect thumb
			if (!m.isVideo()
					&& (force || m.getThumbUrl() == null
							|| "".equals(m.getThumbUrl().trim())
							|| m.getThumbUrl().equals(m.getUrl())
							|| m.getMiniThumbUrl() == null || "".equals(m
							.getMiniThumbUrl()))) {
				String mediaUrl = m.getOriginalUrl();
				if (mediaUrl == null) {
					mediaUrl = m.getUrl();
					((MutableMedia) m).setOriginalUrl(m.getUrl());
				}
				// We compute local location
				final String mediaLocation = mediaUrl.replace("/media/",
						localMediaDirectory);
				final File mediaFile = new File(mediaLocation);
				// We regenerate
				mediaPersistenceSupport.generateThumb((MutableMedia) m,
						mediaFile);
				// Saving changes
				mediaService.saveItem(m);
				// Message
				LOGGER.info("Generated thumb for media " + m.getUrl() + " -> "
						+ m.getThumbUrl() + "[OK]");
			}
		}
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMediaPersistenceSupport(
			MediaPersistenceSupport mediaPersistenceSupport) {
		this.mediaPersistenceSupport = mediaPersistenceSupport;
	}

	public void setLocalMediaDirectory(String localMediaDirectory) {
		this.localMediaDirectory = localMediaDirectory;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	public void setTaskRunnerService(TaskRunnerService taskRunnerService) {
		this.taskRunnerService = taskRunnerService;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public boolean getForce() {
		return force;
	}

	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	public boolean getOffline() {
		return offline;
	}
}
