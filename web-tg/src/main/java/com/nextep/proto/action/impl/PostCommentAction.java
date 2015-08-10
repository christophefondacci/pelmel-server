package com.nextep.proto.action.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.comments.model.Comment;
import com.nextep.comments.model.MutableComment;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

import net.sf.json.JSONObject;

public class PostCommentAction extends AbstractAction implements CurrentUserAware, JsonProvider {

	private static final long serialVersionUID = -3099028863871297662L;
	private static final Log LOGGER = LogFactory.getLog(PostCommentAction.class);
	private static final String APIS_ALIAS_COMMENTED = "com";
	private CalPersistenceService commentService;
	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService activitiesService;
	private SearchPersistenceService searchService;
	private NotificationService notificationService;
	@Autowired
	private MediaPersistenceSupport mediaPersistenceSupport;
	@Autowired
	private JsonBuilder jsonBuilder;

	private String commentItemKey;
	private String comment;
	private File media;
	private String mediaContentType, mediaFileName;

	private boolean success = false;
	private Comment newComment;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey commentedItemKey = CalmFactory.parseKey(commentItemKey);
		// Authenticating current user
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(currentUserSupport.createApisCriterionFor(getNxtpUserToken(), false))
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(commentedItemKey))
						.aliasedBy(APIS_ALIAS_COMMENTED).with(GeographicItem.class));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final User currentUser = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		// Publishing comment
		final MutableComment c = (MutableComment) commentService.createTransientObject();
		c.setAuthorItemKey(currentUser.getKey());
		c.setCommentedItemKey(commentedItemKey);
		c.setDate(new Date());
		c.setMessage(media != null ? getText("message.photoUpgrade.comment") : comment);
		ContextHolder.toggleWrite();
		commentService.saveItem(c);
		newComment = c;

		// If we have an embedded media
		if (media != null) {
			ContextHolder.toggleWrite();
			final Media addedMedia = mediaPersistenceSupport.createMedia(currentUser, c.getKey(), media, mediaFileName,
					mediaContentType, null, false, 1);
			c.add(addedMedia);
		}

		// Generating activity
		final MutableActivity activity = (MutableActivity) activitiesService.createTransientObject();
		activity.setActivityType(ActivityType.COMMENT);
		activity.setDate(new Date());
		activity.setLoggedItemKey(commentedItemKey);
		activity.setUserKey(currentUser.getKey());
		activitiesService.saveItem(activity);

		// Localizing activity and storing in search
		final CalmObject commentedItem = response.getUniqueElement(CalmObject.class, APIS_ALIAS_COMMENTED);
		final GeographicItem geoItem = GeoHelper.extractLocalization(commentedItem);
		activity.add(geoItem);
		searchService.storeCalmObject(activity, SearchScope.CHILDREN);
		getHeaderSupport().initialize(getLocale(), commentedItem, null, null);
		try {
			notificationService.sendCommentAddedEmailNotification(commentedItem, currentUser, c);
		} catch (Exception e) {
			LOGGER.error("Unable to send comment notification: " + e.getMessage(), e);
		}
		success = true;
		return SUCCESS;
	}

	public void setCommentItemKey(String commentItemKey) {
		this.commentItemKey = commentItemKey;
	}

	public String getCommentItemKey() {
		return commentItemKey;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	public void setCommentService(CalPersistenceService commentService) {
		this.commentService = commentService;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	@Override
	public String getJson() {
		final JsonManyToOneMessageList messagesList = jsonBuilder
				.buildJsonMessagesFromComments(Arrays.asList(newComment), false, getLocale());
		return JSONObject.fromObject(messagesList.getMessages().get(0)).toString();
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public File getMedia() {
		return media;
	}

	public void setMedia(File media) {
		this.media = media;
	}

	public String getMediaContentType() {
		return mediaContentType;
	}

	public void setMediaContentType(String mediaContentType) {
		this.mediaContentType = mediaContentType;
	}

	public String getMediaFileName() {
		return mediaFileName;
	}

	public void setMediaFileName(String mediaFileName) {
		this.mediaFileName = mediaFileName;
	}

}
