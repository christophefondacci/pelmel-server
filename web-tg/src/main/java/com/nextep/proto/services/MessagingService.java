package com.nextep.proto.services;

import java.util.Locale;
import java.util.concurrent.Future;

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

	Future<Boolean> sendMessage(ItemKey fromKey, ItemKey toKey, String message);
}
