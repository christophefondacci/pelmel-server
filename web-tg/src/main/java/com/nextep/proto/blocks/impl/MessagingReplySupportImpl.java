package com.nextep.proto.blocks.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.messages.model.Message;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;

public class MessagingReplySupportImpl implements MessagingSupport {

	private static final Log LOGGER = LogFactory
			.getLog(MessagingReplySupportImpl.class);
	private static final String KEY_MESSAGE_TITLE = "message.panel.reply.title";

	private MessagingSupport baseMessagingSupport;
	private MessageSource messageSource;

	private User currentUser;
	private User otherUser;
	private Locale locale;
	private String pageStyle;

	@Override
	public void initialize(UrlService urlService, Locale l,
			List<? extends Message> messages, PaginationInfo pagination,
			ItemKey currentUserKey, String pageStyle) {
		this.locale = l;
		baseMessagingSupport.initialize(urlService, l, messages, pagination,
				currentUserKey, pageStyle);
		for (Message m : messages) {
			try {
				if (m.getFromKey().equals(currentUserKey)
						&& currentUser == null) {
					currentUser = m.getUnique(User.class);
				}
				if (!m.getFromKey().equals(currentUserKey) && otherUser == null) {
					otherUser = m.getUnique(User.class);
				}
			} catch (CalException e) {
				LOGGER.error("Cannot extract user from message : "
						+ e.getMessage());
			}
		}
	}

	@Override
	public int getNewMessagesCount() {
		return baseMessagingSupport.getNewMessagesCount();
	}

	@Override
	public String getFromText(Message message) {
		return baseMessagingSupport.getFromText(message);
	}

	@Override
	public String getFromKey(Message message) {
		return baseMessagingSupport.getFromKey(message);
	}

	@Override
	public Message getCurrentMessage() {
		return baseMessagingSupport.getCurrentMessage();
	}

	@Override
	public String getMessageKey(Message message) {
		return baseMessagingSupport.getMessageKey(message);
	}

	@Override
	public String getCurrentUserKey() {
		return baseMessagingSupport.getCurrentUserKey();
	}

	@Override
	public String getDateText(Message message) {
		return baseMessagingSupport.getDateText(message);
	}

	@Override
	public List<String> getMessageText(Message message) {
		return baseMessagingSupport.getMessageText(message);
	}

	// @Override
	// public String getShortMessageText(Message message) {
	// return baseMessagingSupport.getShortMessageText(message);
	// }

	@Override
	public String getFromUrl(Message message) {
		return baseMessagingSupport.getFromUrl(message);
	}

	@Override
	public String getFromIconUrl(Message message) {
		return baseMessagingSupport.getFromIconUrl(message);
	}

	@Override
	public String getAjaxNextMessage() {
		return baseMessagingSupport.getAjaxNextMessage();
	}

	@Override
	public String generateHTMLJavascriptRefreshTimer() {
		return baseMessagingSupport.generateHTMLJavascriptRefreshTimer();
	}

	@Override
	public String getCssClassForTitle() {
		return baseMessagingSupport.getCssClassForTitle();
	}

	@Override
	public List<? extends Message> getMessages() {
		return baseMessagingSupport.getMessages();
	}

	@Override
	public String getMessageTitle() {
		return messageSource.getMessage(KEY_MESSAGE_TITLE,
				new Object[] { otherUser.getPseudo() }, locale);
	}

	@Override
	public String getToolUrl(Message message, String tool) {
		return baseMessagingSupport.getToolUrl(message, tool);
	}

	@Override
	public boolean showTool(Message message, String tool) {
		return false;
	}

	public void setBaseMessagingSupport(MessagingSupport baseMessagingSupport) {
		this.baseMessagingSupport = baseMessagingSupport;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<String> getTools() {
		return Collections.emptyList();
	}

	@Override
	public String getToolIconUrl(String tool) {
		return null;
	}

	@Override
	public String getToolLabel(String tool) {
		return null;
	}

	@Override
	public String getMessagePageStyle() {
		return baseMessagingSupport.getMessagePageStyle();
	}

	@Override
	public String getShortMessageText(Message message) {
		return baseMessagingSupport.getShortMessageText(message);
	}
}
