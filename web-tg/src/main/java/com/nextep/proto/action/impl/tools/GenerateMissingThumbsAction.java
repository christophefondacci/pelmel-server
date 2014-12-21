package com.nextep.proto.action.impl.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncResult;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.model.impl.TaskResult;
import com.nextep.proto.services.StorageService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class GenerateMissingThumbsAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4951636441112839644L;
	private static final Log LOGGER = LogFactory
			.getLog(GenerateMissingThumbsAction.class);
	private static TaskResult taskResult;

	@Autowired
	private TaskScheduler taskScheduler;
	private MediaPersistenceSupport mediaPersistenceSupport;
	private CalPersistenceService mediaService;
	@Autowired
	private StorageService storageService;

	private boolean force = false;
	private boolean offline = false;
	private String itemKey;
	private List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {

		if (taskResult == null) {
			taskResult = new TaskResult();
			taskScheduler.schedule(new Runnable() {

				@Override
				public void run() {
					try {
						generateThumbs();
					} catch (Exception e) {
						LOGGER.error(
								"Problem while generating thumbs : "
										+ e.getMessage(), e);
						taskResult.setFinished(true);
						taskResult.setSuccess(false);
						taskResult.setMessage(e.getMessage());
					}
				}
			}, new Date());

			messages.add("Generate thumbs process has been successfully started.");
		} else if (taskResult.isFinished()) {
			messages.add("Generation finished: "
					+ (taskResult.isSuccess() ? "SUCCESS" : "FAILURE") + " - "
					+ taskResult.getMessage());
			taskResult = null;
		} else {
			messages.add("Generation in progress: " + taskResult.getMessage());
		}
		return SUCCESS;
	}

	private Future<TaskResult> generateThumbs() throws ApisException,
			CalException {

		List<? extends Media> media = Collections.emptyList();

		if (itemKey == null) {
			final ApisRequest request = ApisFactory.createRequest(Media.class)
					.list(Media.class, null);
			final ApiResponse response = getApiService().execute(request,
					ContextFactory.createContext(getLocale()));

			media = (List<? extends Media>) response.getElements();
		} else {
			final ItemKey ik = CalmFactory.parseKey(itemKey);
			final ApisRequest request = (ApisRequest) ApisFactory
					.createCompositeRequest().addCriterion(
							(ApisCriterion) SearchRestriction.uniqueKeys(
									Arrays.asList(ik)).with(Media.class));
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));
			final CalmObject obj = response.getUniqueElement();
			media = obj.get(Media.class);
		}
		ContextHolder.toggleWrite();
		final int totalCount = media.size();
		int current = 0;
		int failure = 0;
		for (Media m : media) {
			taskResult.setMessage("Processing media [" + current + "/"
					+ totalCount + "] - " + failure + " failures");
			current++;
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

				// Getting stream
				try {
					final InputStream inputStream = storageService
							.readStream(mediaUrl);
					mediaPersistenceSupport.generateThumb((MutableMedia) m,
							inputStream);
					// Saving changes
					mediaService.saveItem(m);
					// Message
					LOGGER.info("Generated thumb for media " + m.getUrl()
							+ " -> " + m.getThumbUrl() + "[OK]");
				} catch (IOException e) {
					LOGGER.error("Unable to read file from URL '" + mediaUrl
							+ "'");
					failure++;
				}
			}
		}
		taskResult.setFinished(true);
		taskResult.setSuccess(true);
		taskResult.setMessage("Generated " + current + " media, " + failure
				+ " failures");
		return new AsyncResult<TaskResult>(taskResult);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMediaPersistenceSupport(
			MediaPersistenceSupport mediaPersistenceSupport) {
		this.mediaPersistenceSupport = mediaPersistenceSupport;
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
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

	public void setItemKey(String itemKey) {
		this.itemKey = itemKey;
	}

	public String getItemKey() {
		return itemKey;
	}
}
