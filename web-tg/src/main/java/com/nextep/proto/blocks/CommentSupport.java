package com.nextep.proto.blocks;

import java.util.List;
import java.util.Locale;

import com.nextep.comments.model.Comment;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;

public interface CommentSupport extends PaginationSupport {

	void initialize(UrlService urlService, Locale locale,
			List<? extends Comment> comments, PaginationInfo paginationInfo,
			User currentUser, ItemKey parentKey);

	/**
	 * Provides the title of the comment box
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Provides the list of all comments
	 * 
	 * @return all comments to display
	 */
	List<? extends Comment> getComments();

	/**
	 * The URL pointing to the icon to display next to this comment
	 * 
	 * @param comment
	 *            the comment to get icon for
	 * @return the URL of the icon
	 */
	String getCommentIconUrl(Comment comment);

	/**
	 * The link to put when displaying the author of this comment
	 * 
	 * @param comment
	 *            the comment to get the author link for.
	 * @return the link
	 */
	String getAuthorLinkUrl(Comment comment);

	/**
	 * The name of the author of this comment
	 * 
	 * @param comment
	 *            the {@link Comment} to get the author name for
	 * @return the author name
	 */
	String getAuthor(Comment comment);

	/**
	 * The message of this comment.
	 * 
	 * @param comment
	 *            the comment to get message
	 * @return the message of this comment
	 */
	String getMessage(Comment comment);

	/**
	 * Provides the formatted date string of this comment
	 * 
	 * @param comment
	 *            the {@link Comment} to get the date string for
	 * @return the formatted date string
	 */
	String getDate(Comment comment);

	String getCurrentUserThumbUrl();

	String getCommentedItemKey();

	boolean isEnabled();
}
