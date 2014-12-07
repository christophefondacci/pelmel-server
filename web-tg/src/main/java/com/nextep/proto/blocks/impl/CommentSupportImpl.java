package com.nextep.proto.blocks.impl;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.comments.model.Comment;
import com.nextep.proto.blocks.CommentSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.model.PaginationInfo;

/**
 * Default implementation of the support for comment blocks
 * 
 * @author cfondacci
 * 
 */
public class CommentSupportImpl implements CommentSupport {

	// Constants
	private static final Log LOGGER = LogFactory
			.getLog(CommentSupportImpl.class);
	private static final String TRANSLATION_AGO = "time.ago";

	// Injected services
	private DistanceDisplayService distanceService;

	// Local dynamic variables
	private UrlService urlService;
	private Locale locale;
	private List<? extends Comment> comments;
	private PaginationInfo paginationInfo;
	private User currentUser;
	private ItemKey parentKey;
	private MessageSource messageSource;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			List<? extends Comment> comments, PaginationInfo paginationInfo,
			User currentUser, ItemKey parentKey) {
		this.urlService = urlService;
		this.locale = locale;
		this.comments = comments;
		this.paginationInfo = paginationInfo;
		this.currentUser = currentUser;
		this.parentKey = parentKey;
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage("block.comments.title", null, locale);
	}

	@Override
	public List<? extends Comment> getComments() {
		return comments;
	}

	@Override
	public String getCommentIconUrl(Comment comment) {
		try {
			final User user = comment.getUnique(User.class);
			return urlService.getMediaUrl(MediaHelper.getSingleMedia(user)
					.getMiniThumbUrl());
		} catch (CalException e) {
			LOGGER.error("Unable to retrieve comment's author for key "
					+ comment.getAuthorItemKey());
			return null;
		}
	}

	@Override
	public String getAuthorLinkUrl(Comment comment) {
		// TODO : Check if ok
		try {
			final User user = comment.getUnique(User.class);
			return urlService.getUserOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), user);
		} catch (CalException e) {
			LOGGER.error("Unable to extract user from comment ["
					+ comment.getKey().toString() + "]: " + e.getMessage());
			return null;
		}
	}

	@Override
	public String getAuthor(Comment comment) {
		try {
			final User user = comment.getUnique(User.class);
			return user.getPseudo();
		} catch (CalException e) {
			LOGGER.error("Unable to retrieve comment's author for key "
					+ comment.getAuthorItemKey());
			return null;
		}
	}

	@Override
	public String getMessage(Comment comment) {
		return comment.getMessage();
	}

	@Override
	public String getDate(Comment comment) {
		// DateFormat dateFormat = DATE_FORMAT_MAP.get(locale);
		// if (dateFormat == null) {
		// dateFormat = new SimpleDateFormat("dd MMM yyyy", locale);
		// DATE_FORMAT_MAP.put(locale, dateFormat);
		// }
		// String commentDate = dateFormat.format(comment.getDate());
		// final String currentDate = dateFormat.format(new Date());
		// if (currentDate.equals(commentDate)) {
		// commentDate = HOUR_FORMAT.format(comment.getDate());
		// }
		// return commentDate;

		final String timeLabel = distanceService.getTimeBetweenDates(
				new Date(), comment.getDate(), locale, false);

		final String fullLabel = messageSource.getMessage(TRANSLATION_AGO,
				new Object[] { timeLabel }, locale);
		return fullLabel;
	}

	@Override
	public String getCurrentUserThumbUrl() {
		return urlService.getMediaUrl(MediaHelper.getSingleMedia(currentUser)
				.getMiniThumbUrl());
	}

	@Override
	public String getCommentedItemKey() {
		return parentKey.toString();
	}

	@Override
	public Integer getCurrentPage() {
		return paginationInfo.getCurrentPageNumber() + 1;
	}

	@Override
	public List<Integer> getPagesList() {
		return DisplayHelper.buildPagesList(paginationInfo.getPageCount(),
				paginationInfo.getCurrentPageNumber() + 1, 2);
	}

	@Override
	public String getPageUrl(int page) {
		return "javascript:call('"
				+ urlService.getCommentUrl("comments-contents", parentKey,
						page - 1) + "','comments-contents')";
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public boolean isEnabled() {
		return currentUser != null;
	}

	public void setDistanceService(DistanceDisplayService distanceService) {
		this.distanceService = distanceService;
	}
}
