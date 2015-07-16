package com.nextep.proto.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageType;
import com.nextep.messages.model.MutableMessage;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.MessagingService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Service
public class MessagingServiceImpl implements MessagingService {
	private final static Log LOGGER = LogFactory.getLog(MessagingServiceImpl.class);
	private final static String MESSAGE_KEY_WELCOME = "register.welcomeMsg";

	@Autowired
	@Qualifier("messagesService")
	private CalPersistenceService messageService;

	@Autowired
	private MediaPersistenceSupport mediaPersistenceSupport;

	@Autowired
	private NotificationService notificationService;

	@Resource(mappedName = "welcomeMessageUserKey")
	private String welcomeMsgUserKey;
	private ItemKey welcomeMsgUserItemKey;

	@Autowired
	@Qualifier("globalMessages")
	private MessageSource messageSource;

	@PostConstruct
	private void init() throws CalException {
		welcomeMsgUserItemKey = CalmFactory.parseKey(welcomeMsgUserKey);
	}

	@Async
	@Override
	public Future<Boolean> sendWelcomeMessage(ItemKey toKey, Locale locale) {

		final MutableMessage msg = (MutableMessage) messageService.createTransientObject();

		msg.setFromKey(welcomeMsgUserItemKey);
		msg.setToKey(toKey);
		msg.setMessage(messageSource.getMessage(MESSAGE_KEY_WELCOME, null, locale));
		msg.setMessageDate(new Date());
		ContextHolder.toggleWrite();
		messageService.saveItem(msg);
		return new AsyncResult<Boolean>(true);
	}

	@Override
	public Message sendMessageWithMedia(User from, User to, ItemKey recipientsGroupKey, String message, File mediaFile,
			String mediaContentType, String mediaFilename) {
		final Message msg = createMessage(from.getKey(), to.getKey(), recipientsGroupKey,
				message == null ? messageSource.getMessage("message.photoUpgrade", null, Locale.ENGLISH) : message,
				MessageType.MESSAGE);
		ContextHolder.toggleWrite();
		if (mediaFile != null) {
			try {
				final Media addedMedia = mediaPersistenceSupport.createMedia(from, msg.getKey(), mediaFile,
						mediaFilename, mediaContentType, null, false, 1);
				msg.add(addedMedia);
			} catch (IOException e) {
				LOGGER.error("Unable to create media for message " + msg.getKey() + ": " + e.getMessage(), e);
			}
		}
		sendPushNotification(from, to, message, mediaFile != null, MessageType.MESSAGE, msg.isUnread());
		return msg;
	}

	@Override
	public Message sendMessage(User from, User to, ItemKey recipientsGroupKey, String message) {
		return sendMessage(from, to, recipientsGroupKey, message, MessageType.MESSAGE);
	}

	@Override
	public Message sendMessage(User from, User to, ItemKey recipientsGroupKey, String message, MessageType type) {
		final Message msg = createMessage(from.getKey(), to.getKey(), recipientsGroupKey, message, type);
		sendPushNotification(from, to, message, false, type, msg.isUnread());
		return msg;
	}

	private Message createMessage(ItemKey fromKey, ItemKey toKey, ItemKey recipientsGroupKey, String message,
			MessageType messageType) {
		ContextHolder.toggleWrite();

		final MutableMessage msg = (MutableMessage) messageService.createTransientObject();
		msg.setFromKey(fromKey);
		msg.setToKey(toKey);
		msg.setMessageType(messageType);
		if (recipientsGroupKey != null) {
			msg.setRecipientsGroupKey(recipientsGroupKey);
		}
		if (messageType != MessageType.PRIVATE_NETWORK) {
			msg.setMessage(message);
		} else {
			msg.setMessage(messageSource.getMessage("message.networkUpgrade", null, Locale.ENGLISH));
		}
		msg.setMessageDate(new Date());
		// Our returning message is the message to self
		if (toKey.equals(fromKey) || messageType == MessageType.PRIVATE_NETWORK) {
			msg.setUnread(false);
		}
		messageService.saveItem(msg);
		return msg;
	}

	private void sendPushNotification(User sourceUser, User targetUser, String message, boolean isMediaMessage,
			MessageType messageType, boolean unread) {

		// Notifying recipient if deviceId and push enabled (and not our own
		// message)
		if (targetUser.getPushDeviceId() != null && !targetUser.getKey().equals(sourceUser.getKey())) {
			String pushMsg;
			// Building push message
			if (!isMediaMessage) {
				if (messageType == MessageType.MESSAGE) {
					pushMsg = MessageFormat.format(
							messageSource.getMessage("message.push.template", null, Locale.ENGLISH),
							sourceUser.getPseudo(), message);
				} else {
					pushMsg = message;
				}
			} else {
				pushMsg = MessageFormat.format(
						messageSource.getMessage("message.push.photo.template", null, Locale.ENGLISH),
						sourceUser.getPseudo());
			}
			// Sending message
			final List<? extends User> toApprove = targetUser.get(User.class,
					Constants.APIS_ALIAS_NETWORK_PENDING_APPROVAL);
			final List<? extends Message> unreadMessages = targetUser.get(Message.class);
			int unreadCount = unreadMessages.size();
			if (unread) {
				unreadCount++;
			}
			// Adding +1 because we just added a new unread message
			notificationService.sendNotification(targetUser, pushMsg, unreadCount, toApprove.size(), null);
		}
	}

}
