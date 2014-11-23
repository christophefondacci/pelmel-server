package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.comments.model.MutableComment;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
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

public class PostCommentAction extends AbstractAction implements
		CurrentUserAware, JsonProvider {

	private static final long serialVersionUID = -3099028863871297662L;
	private static final Log LOGGER = LogFactory
			.getLog(PostCommentAction.class);
	private static final String APIS_ALIAS_COMMENTED = "com";
	private CalPersistenceService commentService;
	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService activitiesService;
	private SearchPersistenceService searchService;
	private NotificationService notificationService;

	private String commentItemKey;
	private String comment;

	private boolean success = false;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey commentedItemKey = CalmFactory.parseKey(commentItemKey);
		// Authenticating current user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(commentedItemKey))
								.aliasedBy(APIS_ALIAS_COMMENTED)
								.with(GeographicItem.class));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		// Publishing comment
		final MutableComment c = (MutableComment) commentService
				.createTransientObject();
		c.setAuthorItemKey(currentUser.getKey());
		c.setCommentedItemKey(commentedItemKey);
		c.setDate(new Date());
		c.setMessage(comment);
		ContextHolder.toggleWrite();
		commentService.saveItem(c);

		// Generating activity
		final MutableActivity activity = (MutableActivity) activitiesService
				.createTransientObject();
		activity.setActivityType(ActivityType.COMMENT);
		activity.setDate(new Date());
		activity.setLoggedItemKey(commentedItemKey);
		activity.setUserKey(currentUser.getKey());
		activitiesService.saveItem(activity);

		// Localizing activity and storing in search
		final CalmObject commentedItem = response.getUniqueElement(
				CalmObject.class, APIS_ALIAS_COMMENTED);
		final GeographicItem geoItem = GeoHelper
				.extractLocalization(commentedItem);
		activity.add(geoItem);
		searchService.storeCalmObject(activity, SearchScope.CHILDREN);
		getHeaderSupport().initialize(getLocale(), commentedItem, null, null);
		try {
			notificationService.sendCommentAddedEmailNotification(
					commentedItem, currentUser, c);
		} catch (Exception e) {
			LOGGER.error(
					"Unable to send comment notification: " + e.getMessage(), e);
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
		final JsonStatus status = new JsonStatus();
		status.setError(!success);
		return JSONObject.fromObject(status).toString();
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
