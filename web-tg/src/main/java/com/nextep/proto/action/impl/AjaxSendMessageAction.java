package com.nextep.proto.action.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonMessage;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MutableMessage;
import com.nextep.messages.model.impl.MessageRequestTypeFactory;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class AjaxSendMessageAction extends AbstractAction implements
		JsonProviderWithError {

	private static final long serialVersionUID = -4442024505781884882L;
	private static final String APIS_ALIAS_TARGET_USER = "to";

	private CalPersistenceService messageService;
	private CurrentUserSupport currentUserSupport;
	private NotificationService notificationService;
	@Autowired
	private JsonBuilder jsonBuilder;
	@Autowired
	private MediaPersistenceSupport mediaPersistenceSupport;

	private User currentUser;
	private String to;
	private String msgText;
	private File media;
	private String mediaContentType, mediaFileName;

	private Message message;

	@Override
	protected String doExecute() throws Exception {
		// Parsing destination user key
		final ItemKey toUserKey = CalmFactory.parseKey(to);

		// Getting users
		final ApisRequest request = ApisFactory.createCompositeRequest();
		request.addCriterion(
				currentUserSupport.createApisCriterionFor(getNxtpUserToken(),
						true)).addCriterion(
				(ApisCriterion) SearchRestriction
						.uniqueKeys(Arrays.asList(toUserKey))
						.aliasedBy(APIS_ALIAS_TARGET_USER)
						.with(SearchRestriction.with(Message.class,
								MessageRequestTypeFactory.UNREAD)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		// Extracting user
		currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Now sending message
		ContextHolder.toggleWrite();
		final MutableMessage msg = (MutableMessage) messageService
				.createTransientObject();
		msg.setFromKey(currentUser.getKey());
		msg.setToKey(toUserKey);
		msg.setMessage(media != null ? getText("message.photoUpgrade")
				: msgText);
		msg.setMessageDate(new Date());
		messageService.saveItem(msg);

		// If we have an embedded media
		if (media != null) {
			ContextHolder.toggleWrite();
			final Media addedMedia = mediaPersistenceSupport.createMedia(
					currentUser, msg.getKey(), media, mediaFileName,
					mediaContentType, null, false, 1);
			msg.add(addedMedia);
		}

		// Notifying recipient if deviceId and push enabled
		final User targetUser = response.getUniqueElement(User.class,
				APIS_ALIAS_TARGET_USER);
		final String pushMsg = currentUser.getPseudo() + ": \"" + msgText
				+ "\"";
		if (targetUser.getPushDeviceId() != null) {
			final List<? extends Message> unreadMessages = targetUser
					.get(Message.class);

			notificationService.sendNotification(targetUser, pushMsg,
					unreadMessages.size(), null);
		}
		message = msg;
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonMessage m = jsonBuilder.buildJsonMessage(message);
		return JSONObject.fromObject(m).toString();
	}

	@Override
	public String getJsonError() {
		final JsonStatus s = new JsonStatus();
		final Exception e = getLastException();
		s.setError(true);
		s.setMessage("Unable to send message: "
				+ (e == null ? " unknown reason" : e.getMessage()));
		return JSONObject.fromObject(s).toString();
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		if (currentUser != null) {
			return currentUser.getKey().toString();
		} else {
			return "";
		}
	}

	public String getTo() {
		return to;
	}

	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setMessageService(CalPersistenceService messageService) {
		this.messageService = messageService;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
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
