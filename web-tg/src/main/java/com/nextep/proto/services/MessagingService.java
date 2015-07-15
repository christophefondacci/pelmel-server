package com.nextep.proto.services;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.Future;

import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageType;
import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;

/**
 * Entry point for sending system messages to users. This service should
 * generally always be used to send a message to anyone as it will handle
 * message delivery, push, and might support emails recall.
 * 
 * @author cfondacci
 *
 */
public interface MessagingService {

	Future<Boolean> sendWelcomeMessage(ItemKey toKey, Locale locale);

	Message sendMessage(User from, User to, ItemKey recipientsGroupKey, String message);

	Message sendMessage(User from, User to, ItemKey recipientsGroupKey, String message, MessageType type);

	Message sendMessageWithMedia(User from, User to, ItemKey recipientsGroupKey, String message, File mediaFile,
			String mediaContentType, String mediaFilename);
}
