package com.nextep.proto.services.impl;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.messages.model.MutableMessage;
import com.nextep.proto.services.MessagingService;
import com.nextep.proto.spring.ContextHolder;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;

@Service
public class MessagingServiceImpl implements MessagingService {
	private final static String MESSAGE_KEY_WELCOME = "register.welcomeMsg";

	@Autowired
	@Qualifier("messagesService")
	private CalPersistenceService messagePersistenceService;

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

		final MutableMessage msg = (MutableMessage) messagePersistenceService
				.createTransientObject();

		msg.setFromKey(welcomeMsgUserItemKey);
		msg.setToKey(toKey);
		msg.setMessage(messageSource.getMessage(MESSAGE_KEY_WELCOME, null,
				locale));
		msg.setMessageDate(new Date());
		ContextHolder.toggleWrite();
		messagePersistenceService.saveItem(msg);
		return new AsyncResult<Boolean>(true);
	}

	@Override
	public Future<Boolean> sendMessage(ItemKey fromKey, ItemKey toKey,
			String message) {
		// TODO Auto-generated method stub
		return null;
	}

}
