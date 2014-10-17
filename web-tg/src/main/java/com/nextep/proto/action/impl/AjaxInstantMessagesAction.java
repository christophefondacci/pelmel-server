package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MutableMessage;
import com.nextep.messages.model.impl.MessageRequestTypeFactory;
import com.nextep.messages.model.impl.RequestTypeListUnreadMessages;
import com.nextep.messages.services.MessageService;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.apis.adapters.ApisMessageFromUserAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisNoSuchElementException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class AjaxInstantMessagesAction extends AbstractAction implements
		MessagingAware, CurrentUserAware {

	private static final long serialVersionUID = 1384215746508695952L;
	private static final Log LOGGER = LogFactory
			.getLog(AjaxInstantMessagesAction.class);
	private static final String APIS_ALIAS_TARGET_USER = "to";
	private static final ApisItemKeyAdapter messageFromUserAdapter = new ApisMessageFromUserAdapter();
	// Injected services & supports
	private MessagingSupport messagingSupport;
	private MessageService messageService;
	private CalPersistenceService messagePersistenceService;
	private CurrentUserSupport currentUserSupport;
	private NotificationService notificationService;

	// Action dynamic arguments
	private String fromKey;
	private String instantMsgText;
	private String readMsg;
	private String pageStyle;

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String doExecute() throws Exception {

		// First checking user timeout / online
		final ApisRequest userRequest = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));
		if (fromKey != null) {
			// Parsing destination user key
			final ItemKey fromUserKey = CalmFactory.parseKey(fromKey);
			userRequest.addCriterion((ApisCriterion) SearchRestriction
					.uniqueKeys(Arrays.asList(fromUserKey))
					.aliasedBy(APIS_ALIAS_TARGET_USER)
					.with(SearchRestriction.with(Message.class,
							MessageRequestTypeFactory.UNREAD)));
		}
		User user = null;
		final ApiCompositeResponse userResponse = (ApiCompositeResponse) getApiService()
				.execute(userRequest, ContextFactory.createContext(getLocale()));
		try {
			user = userResponse.getUniqueElement(User.class,
					CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
			checkCurrentUser(user);
		} catch (ApisNoSuchElementException e) {
			throwConnectionTimeout();
		}

		currentUserSupport.initialize(getUrlService(), user);
		// First we send our reply if one has been posted
		if (fromKey != null && instantMsgText != null
				&& !"".equals(instantMsgText.trim())) {
			// The from / to corresponds to the original message so we invert it
			// to send a reply
			final MutableMessage msg = (MutableMessage) messagePersistenceService
					.createTransientObject();
			msg.setFromKey(user.getKey());
			msg.setToKey(CalmFactory.parseKey(fromKey));
			msg.setMessage(instantMsgText);
			msg.setMessageDate(new Date());
			ContextHolder.toggleWrite();
			messagePersistenceService.saveItem(msg);
		}
		// Then we mark the message as read
		if (readMsg != null) {
			final ItemKey msgKey = CalmFactory.parseKey(readMsg);
			ContextHolder.toggleWrite();
			messageService.markRead(Arrays.asList(msgKey));
			// Waiting for proper synch
			Thread.sleep(500);
		}

		// Notifying recipient if deviceId and push enabled
		final User targetUser = userResponse.getUniqueElement(User.class,
				APIS_ALIAS_TARGET_USER);
		final String pushMsg = user.getPseudo() + ": \"" + instantMsgText
				+ "\"";
		if (targetUser != null && targetUser.getPushDeviceId() != null) {
			final List<? extends Message> unreadMessages = targetUser
					.get(Message.class);

			notificationService.sendNotification(targetUser, pushMsg,
					unreadMessages.size(), null);
		}
		// Then we fetch new unread messages and initialize the messaging
		// support
		ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						(ApisCriterion) SearchRestriction
								.list(Message.class,
										new RequestTypeListUnreadMessages(user
												.getKey())).addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														messageFromUserAdapter)
												.with(Media.class)));
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final List<Message> messages = new ArrayList<Message>(
				(Collection<Message>) response.getElements());
		// Initializing messaging support from unread messages
		messagingSupport.initialize(getUrlService(), getLocale(), messages,
				response.getPaginationInfo(Message.class), user.getKey(),
				pageStyle);
		return SUCCESS;
	}

	public String getReadMsg() {
		return readMsg;
	}

	public void setReadMsg(String readMsg) {
		this.readMsg = readMsg;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}

	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}

	public String getFromKey() {
		return fromKey;
	}

	public void setInstantMsgText(String instantMsgText) {
		this.instantMsgText = instantMsgText;
	}

	public String getInstantMsgText() {
		return instantMsgText;
	}

	public void setMessagePersistenceService(
			CalPersistenceService messagePersistenceService) {
		this.messagePersistenceService = messagePersistenceService;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setPageStyle(String pageStyle) {
		this.pageStyle = pageStyle;
	}

	public String getPageStyle() {
		return pageStyle;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
