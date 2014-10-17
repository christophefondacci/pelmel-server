package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.messages.model.Message;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;

/**
 * Provides the interface to access the messaging features.
 * 
 * @author cfondacci
 * 
 */
public interface MessagingSupport {

	void initialize(UrlService urlService, Locale l,
			List<? extends Message> messages, PaginationInfo pagination,
			ItemKey currentUserKey, String pageStyle);

	int getNewMessagesCount();

	String getFromText(Message message);

	/**
	 * Provides the key of the message's author
	 * 
	 * @param message
	 *            the Message to extract the author's unique key from
	 * @return the message's author key
	 */
	String getFromKey(Message message);

	/**
	 * Provides the current message, generally the first unread message, or
	 * <code>null</code> if no such message exists.
	 * 
	 * @return the current {@link Message}
	 */
	Message getCurrentMessage();

	String getMessageKey(Message message);

	String getCurrentUserKey();

	String getDateText(Message message);

	/**
	 * Provides the text of a message splitted at every line to that we could
	 * easily display line feeds properly.
	 * 
	 * @param message
	 *            message to get the text of
	 * @return the list of message lines
	 */
	List<String> getMessageText(Message message);

	/**
	 * Provides the short version of a message. It could be the entire message
	 * or a subset of the message followed by "..."
	 * 
	 * @param message
	 *            the {@link Message} to get the short text for
	 * @return the short text of this message
	 */
	String getShortMessageText(Message message);

	// String getShortMessageText(Message message);

	/**
	 * URL pointing to the overview page of the author of the provided message.
	 * 
	 * @param message
	 *            the message to process
	 * @return the URL to the author's overview page
	 */
	String getFromUrl(Message message);

	/**
	 * URL of the icon of the author of this message
	 * 
	 * @param message
	 *            the message to extract icon URL for
	 * @return the author's thumb url
	 */
	String getFromIconUrl(Message message);

	String getAjaxNextMessage();

	String generateHTMLJavascriptRefreshTimer();

	String getCssClassForTitle();

	/**
	 * Provides the list of messages
	 * 
	 * @return the messages to display
	 */
	List<? extends Message> getMessages();

	/**
	 * Provides the title of the myMessages page
	 * 
	 * @return title of the myMessages page
	 */
	String getMessageTitle();

	String getToolUrl(Message message, String tool);

	List<String> getTools();

	String getToolIconUrl(String tool);

	String getToolLabel(String tool);

	boolean showTool(Message message, String tool);

	/**
	 * Provides the styling for the message box
	 * 
	 * @return
	 */
	String getMessagePageStyle();
}
