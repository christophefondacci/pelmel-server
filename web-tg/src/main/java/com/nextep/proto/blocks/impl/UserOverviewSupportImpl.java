package com.nextep.proto.blocks.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.UserHelper;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

/**
 * Default user overview page implementation.
 * 
 * @author cfondacci
 * 
 */
public class UserOverviewSupportImpl implements OverviewSupport {

	private static final String TRANSLATION_KEY_ACTION_PREFIX = "toolbar.";
	private User user;
	private UrlService urlService;
	private MessageSource messageSource;
	private String baseUrl;
	private Locale locale;
	private int likesCount, dislikesCount;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject user, int likesCount, int dislikesCount, User currentUser) {
		this.urlService = urlService;
		this.locale = locale;
		this.user = (User) user;
		this.likesCount = likesCount;
		this.dislikesCount = dislikesCount;
	}

	@Override
	public User getOverviewObject() {
		return user;
	}

	@Override
	public String getTitle(CalmObject o) {
		return DisplayHelper.getHtmlSafe(((User) o).getPseudo());
	}

	@Override
	public String getAddress(CalmObject o) {
		return null;
	}

	@Override
	public String getToolbarActionUrl(String action, String targetHtmlId) {
		if ("msg".equals(action)) {
			return urlService.getWriteMessageDialogUrl(targetHtmlId,
					user.getKey());
		} else if ("like".equals(action)) {
			return urlService.getILikeUrl(targetHtmlId, user.getKey());
		}
		return null;
	}

	@Override
	public String getTitleIconUrl(CalmObject o) {
		final User user = (User) o;
		final boolean isOnline = UserHelper.isOnline(user);
		return baseUrl
				+ messageSource
						.getMessage("user.icon."
								+ (isOnline ? "online" : "offline") + ".32",
								null, null);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getToolbarLabel(String action) {
		return messageSource.getMessage(TRANSLATION_KEY_ACTION_PREFIX + action,
				null, locale);
	}

	@Override
	public List<String> getAdditionalActions() {
		return Collections.emptyList();
	}

	@Override
	public String getToolbarActionIconUrl(String action) {
		return null;
	}

	@Override
	public int getLikesCount() {
		return likesCount;
	}

	@Override
	public int getDislikesCount() {
		return dislikesCount;
	}

	@Override
	public String getOwnershipInfoLabel(User user) {
		return null;
	}

	@Override
	public boolean isCurrentOwner(User user) {
		return false;
	}

	@Override
	public boolean isUpdatable() {
		return false;
	}
}
