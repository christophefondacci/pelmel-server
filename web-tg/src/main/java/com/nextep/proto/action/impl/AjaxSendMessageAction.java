package com.nextep.proto.action.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonMessage;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageRecipientsGroup;
import com.nextep.messages.model.MutableMessageRecipientsGroup;
import com.nextep.messages.model.impl.MessageRequestTypeFactory;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.apis.adapters.ApisMessageRecipientsGroupItemKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.MessagingService;
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

import net.sf.json.JSONObject;

public class AjaxSendMessageAction extends AbstractAction implements JsonProviderWithError {

	private static final long serialVersionUID = -4442024505781884882L;
	private static final String APIS_ALIAS_TARGET_USER = "to";
	private static final String APIS_ALIAS_GROUP_RECIPIENTS = "rcpt";

	private CurrentUserSupport currentUserSupport;
	@Autowired
	private MessagingService messagingService;
	@Autowired
	@Qualifier("messageRecipientsGroupService")
	private CalPersistenceService messageRecipientsGroupService;
	@Autowired
	private JsonBuilder jsonBuilder;

	private User currentUser;
	private String to;
	private String msgText;
	private String recipients;
	private File media;
	private String mediaContentType, mediaFileName;

	private Message message;

	@SuppressWarnings("unchecked")
	@Override
	protected String doExecute() throws Exception {
		// Parsing destination user key
		final List<ItemKey> toUserKeys = new ArrayList<ItemKey>();

		if (!to.contains(",")) {
			final ItemKey toUserKey = CalmFactory.parseKey(to);
			toUserKeys.add(toUserKey);
		} else {
			// Splitting every comma-separated key
			final String[] toList = to.split(",");
			// Parsing each key and appending to our list
			for (String toKey : toList) {
				final ItemKey toUserKey = CalmFactory.parseKey(toKey);
				toUserKeys.add(toUserKey);
			}
		}

		// Getting users
		final ApisRequest request = ApisFactory.createCompositeRequest();
		request.addCriterion(currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true))
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKeys(toUserKeys).aliasedBy(APIS_ALIAS_TARGET_USER)
						.with(SearchRestriction.with(Message.class, MessageRequestTypeFactory.UNREAD))
						.addCriterion(SearchRestriction.customAdapt(new ApisMessageRecipientsGroupItemKeyAdapter(),
								APIS_ALIAS_GROUP_RECIPIENTS)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Extracting current user
		currentUser = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Default recipients user list
		List<? extends User> users = response.getElements(User.class, APIS_ALIAS_TARGET_USER);
		// If no users there, we must have a recipients group
		if (users == null || users.isEmpty()) {
			users = response.getElements(User.class, APIS_ALIAS_GROUP_RECIPIENTS);
		}

		ContextHolder.toggleWrite();
		// Retrieving any passed recipients group
		MessageRecipientsGroup recipientsGroup = response.getUniqueElement(MessageRecipientsGroup.class,
				APIS_ALIAS_TARGET_USER);

		// If multi recipients users and no group, we need to create one
		if (users.size() > 1 && recipientsGroup == null) {

			// Creating a new recipients group
			recipientsGroup = (MessageRecipientsGroup) messageRecipientsGroupService.createTransientObject();

			// Adding ourselves to the group
			if (!toUserKeys.contains(currentUser.getKey())) {
				toUserKeys.add(currentUser.getKey());
				((List<User>) users).add(currentUser);
			}

			// Setting the current list of recipients
			((MutableMessageRecipientsGroup) recipientsGroup).setRecipients(toUserKeys);

			// Saving to get an ID
			messageRecipientsGroupService.saveItem(recipientsGroup);
		}

		// Now sending message
		for (User targetUser : users) {
			final Message msg = messagingService.sendMessageWithMedia(currentUser, targetUser,
					recipientsGroup == null ? null : recipientsGroup.getKey(), media == null ? msgText : null, media,
					mediaContentType, mediaFileName);
			// Our returning message is the message to self
			if (targetUser.getKey().equals(currentUser.getKey()) || users.size() == 1) {
				message = msg;
			}
		}
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
		s.setMessage("Unable to send message: " + (e == null ? " unknown reason" : e.getMessage()));
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

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
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

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getRecipients() {
		return recipients;
	}
}
