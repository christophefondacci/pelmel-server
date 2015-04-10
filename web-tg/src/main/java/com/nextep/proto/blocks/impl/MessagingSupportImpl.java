package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.blocks.PaginationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;

public class MessagingSupportImpl implements MessagingSupport,
		PaginationSupport {

	private static final String KEY_TOOL_ICON_PREFIX = "message.tool.icon.";
	private static final String KEY_TOOL_LABEL_PREFIX = "message.tool.label.";
	private static final int PAGES_BEFORE_AFTER = 3;
	private static final int MAX_SHORT_MSG_LENGTH = 100;
	private static final Log log = LogFactory
			.getLog(MessagingSupportImpl.class);
	private static final Map<Locale, DateFormat> DATE_FORMAT_MAP = new HashMap<Locale, DateFormat>();

	private MessageSource messageSource;
	private UrlService urlService;
	private Locale locale;
	private List<? extends Message> messages;
	private ItemKey currentUserKey;
	private String pageStyle;
	private PaginationInfo paginationInfo;
	private final int currentMsgIndex = 0;
	private int newMessages = 0;
	private int messageRefreshIntervalMs = 10000;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			List<? extends Message> messages, PaginationInfo pagination,
			ItemKey currentUserKey, String pageStyle) {
		this.urlService = urlService;
		this.locale = locale;
		this.messages = messages;
		this.paginationInfo = pagination;
		this.currentUserKey = currentUserKey;
		this.pageStyle = pageStyle;
		newMessages = 0;
		for (Message m : messages) {
			if (m.isUnread()) {
				newMessages++;
			}
		}
	}

	@Override
	public int getNewMessagesCount() {
		return newMessages;
	}

	@Override
	public Message getCurrentMessage() {
		if (messages.size() > currentMsgIndex) {
			return messages.get(currentMsgIndex);
		} else {
			return null;
		}
	}

	@Override
	public String getMessageKey(Message m) {
		if (m != null) {
			return m.getKey().toString();
		}
		return "";
	}

	@Override
	public String getFromText(Message m) {
		if (m != null) {
			final User user = getFromUser(m);
			return user.getPseudo();
		}
		return null;
	}

	private User getFromUser(Message m) {
		if (m != null) {
			try {
				final User user = m.getUnique(User.class);
				return user;
			} catch (CalException e) {
				log.error("Unable to retrieve FROM user of message "
						+ m.getKey());
			}
		}
		return null;
	}

	@Override
	public String getDateText(Message m) {
		DateFormat dateFormat = DATE_FORMAT_MAP.get(locale);
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
			DATE_FORMAT_MAP.put(locale, dateFormat);
		}
		return dateFormat.format(m.getMessageDate());
	}

	@Override
	public List<String> getMessageText(Message m) {
		final List<String> messageLines = new ArrayList<String>();

		for (String msgLine : m.getMessage().split("\n")) {
			messageLines.add(DisplayHelper.getHtmlSafe(msgLine));
		}
		return messageLines;
	}

	@Override
	public String getFromUrl(Message m) {
		if (m != null) {
			final User fromUser = getFromUser(m);
			return urlService.getUserOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), fromUser);
		}
		return "";
	}

	@Override
	public String getFromKey(Message m) {
		if (m != null) {
			final User user = getFromUser(m);
			return user.getKey().toString();
		}
		return "";
	}

	@Override
	public String getCurrentUserKey() {
		return currentUserKey.toString();
	}

	@Override
	public String getAjaxNextMessage() {
		return "javascript:" + getRawAjaxNextMessage();
	}

	private String getRawAjaxNextMessage() {
		final Message m = getCurrentMessage();
		final StringBuilder buf = new StringBuilder();
		buf.append("Pelmel.simpleCall('/ajaxInstantMessages.action?toKey="
				+ currentUserKey.toString());
		// Appending message which needs to be marked as read
		if (m != null) {
			buf.append("&readMsg=" + m.getKey().toString());
		}
		buf.append("&pageStyle=" + pageStyle + "','instantUserMsg')");
		return buf.toString();
	}

	@Override
	public String generateHTMLJavascriptRefreshTimer() {
		return "<script type=\"text/javascript\">refreshMessages("
				+ messageRefreshIntervalMs + "); </script>";
	}

	// private String getRawAjaxRefreshBody() {
	// return "simpleCall('ajaxInstantRefreshBody.action','instantMsgBody');";
	// }
	//
	// private String getRawAjaxRefreshTitle() {
	// return "simpleCall('ajaxInstantRefreshTitle.action','instantMsgTitle');";
	// }

	@Override
	public String getCssClassForTitle() {
		// if (getNewMessagesCount() > 0) {
		// return "boxTitleActive";
		// } else {
		return "boxTitle";
		// }
	}

	public void setMessageRefreshIntervalMs(int messageRefreshIntervalMs) {
		this.messageRefreshIntervalMs = messageRefreshIntervalMs;
	}

	@Override
	public List<? extends Message> getMessages() {
		return messages;
	}

	@Override
	public String getShortMessageText(Message message) {
		final String msg = message.getMessage();
		if (msg.length() > MAX_SHORT_MSG_LENGTH) {
			return msg.substring(0, MAX_SHORT_MSG_LENGTH) + "...";
		} else {
			return msg;
		}
	}

	@Override
	public String getFromIconUrl(Message message) {
		final User fromUser = getFromUser(message);
		if (fromUser != null) {
			final Media m = MediaHelper.getSingleMedia(fromUser);
			if (log.isDebugEnabled()) {
				log.debug("User " + fromUser.getKey().toString()
						+ " has media " + m.getKey().toString());
			}
			if (m != null) {
				return urlService.getMediaUrl(m.getMiniThumbUrl());
			}
		}
		return urlService.getStaticUrl(Constants.DEFAULT_IMAGE_PROFILE_URL);
	}

	private int getFirstMessageIndex() {
		return paginationInfo.getCurrentPageNumber()
				* paginationInfo.getItemCountPerPage() + 1;
	}

	private int getLastMessageIndex() {
		return getFirstMessageIndex() + messages.size() - 1;
	}

	private int getMessageCount() {
		return paginationInfo.getItemCount();
	}

	@Override
	public String getMessageTitle() {
		return messageSource.getMessage("message.panel.manageMessagesTitle",
				new Object[] { getFirstMessageIndex(), getLastMessageIndex(),
						getMessageCount() }, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<Integer> getPagesList() {
		return DisplayHelper.buildPagesList(paginationInfo.getPageCount(),
				paginationInfo.getCurrentPageNumber() + 1, PAGES_BEFORE_AFTER);
	}

	@Override
	public Integer getCurrentPage() {
		return paginationInfo.getCurrentPageNumber() + 1;
	}

	@Override
	public String getPageUrl(int page) {
		return "/ajaxMyMessages?page=" + (page - 1);
	}

	@Override
	public String getToolUrl(Message message, String tool) {
		if ("reply".equals(tool)) {
			return "/myMessageReply.action?from="
					+ message.getFromKey().toString();
		} else if ("block".equals(tool)) {
			return "/blockUser.action?id=" + message.getFromKey().toString();
		} else if ("delete".equals(tool)) {
			return "/deleteMessage.action?id=" + message.getKey().toString();
		} else {
			return "#";
		}
	}

	@Override
	public boolean showTool(Message message, String tool) {
		if ("reply".equals(tool)) {
			return !message.getFromKey().equals(currentUserKey);
		} else {
			return true;
		}
	}

	@Override
	public List<String> getTools() {
		return Arrays.asList("reply", "block", "delete");
	}

	@Override
	public String getToolIconUrl(String tool) {
		return messageSource.getMessage(KEY_TOOL_ICON_PREFIX + tool, null,
				locale);
	}

	@Override
	public String getToolLabel(String tool) {
		return messageSource.getMessage(KEY_TOOL_LABEL_PREFIX + tool, null,
				locale);
	}

	@Override
	public String getMessagePageStyle() {
		return pageStyle;
	}
}
