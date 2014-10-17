package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.ImageConstants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

/**
 * This class provides default, common and generic implementation of the
 * overview support compatible with all kind of CAL object. This class could be
 * used when we don't know which kind of object we are dealing with or to
 * externalize default common behaviour from other specific implementations.
 * 
 * @author cfondacci
 * 
 */
public class CommonOverviewSupportImpl implements OverviewSupport {

	private static final String TRANSLATION_KEY_ACTION_PREFIX = "toolbar.";
	private static final String TRANSLATION_KEY_OWNER_INFO = "owner.place.mine.label";
	private static final DateFormat DATE_FORMATTER = new SimpleDateFormat(
			"dd/MM/yyyy");

	private UrlService urlService;
	private CalmObject calmObject;
	private Locale locale;
	private int likeCount, dislikeCount;
	private User currentUser;
	private MessageSource messageSource;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject object, int likeCount, int dislikeCount, User currentUser) {
		this.urlService = urlService;
		this.locale = locale;
		this.calmObject = object;
		this.likeCount = likeCount;
		this.dislikeCount = dislikeCount;
		this.currentUser = currentUser;
	}

	@Override
	public CalmObject getOverviewObject() {
		return calmObject;
	}

	@Override
	public String getTitle(CalmObject o) {
		return DisplayHelper.getName(o);
	}

	// @Override
	// public String getDescription(CalmObject o) {
	// final Description d = DisplayHelper.getSingleDescription(o, locale);
	// if (d != null) {
	// return d.getDescription();
	// } else {
	// return null;
	// }
	// }

	@Override
	public String getAddress(CalmObject o) {
		return null;
	}

	@Override
	public String getToolbarActionUrl(String action, String targetHtmlId) {
		if ("like".equals(action)) {
			return urlService.getILikeUrl(targetHtmlId, calmObject.getKey());
		} else if ("media".equals(action)) {
			return urlService.getMediaAdditionFormUrl(targetHtmlId,
					calmObject.getKey());
		}
		return null;
	}

	@Override
	public String getTitleIconUrl(CalmObject o) {
		return "";
	}

	@Override
	public String getToolbarLabel(String action) {
		return messageSource.getMessage(TRANSLATION_KEY_ACTION_PREFIX + action,
				null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<String> getAdditionalActions() {
		return Collections.emptyList();
	}

	@Override
	public String getToolbarActionIconUrl(String action) {
		if (Constants.ACTION_CODE_EDIT.equals(action)) {
			return ImageConstants.ICON_EDIT;
		}
		return null;
	}

	@Override
	public int getLikesCount() {
		return likeCount;
	}

	@Override
	public int getDislikesCount() {
		return dislikeCount;
	}

	private AdvertisingBooster getActiveBooster() {
		final List<? extends AdvertisingBooster> boosters = calmObject
				.get(AdvertisingBooster.class);
		final long currentTime = System.currentTimeMillis();
		for (AdvertisingBooster booster : boosters) {
			final long boostStart = booster.getStartDate().getTime();
			final long boostEnd = booster.getEndDate().getTime();
			// Is it an active booster period ?
			if ((currentTime >= boostStart && currentTime < boostEnd)) {
				return booster;
			}
		}
		return null;
	}

	@Override
	public boolean isCurrentOwner(User user) {
		// Getting any active booster
		final AdvertisingBooster activeBooster = getActiveBooster();
		// We check if this is the current user
		return (activeBooster != null && activeBooster.getPurchaserItemKey()
				.equals(user.getKey()));
	}

	@Override
	public boolean isUpdatable() {
		// Getting any active booster
		final AdvertisingBooster activeBooster = getActiveBooster();
		// We can modify this element if nobody owns it OR if we are the owner
		return activeBooster == null
				|| activeBooster.getPurchaserItemKey().equals(
						currentUser.getKey());
	}

	@Override
	public String getOwnershipInfoLabel(User user) {
		final AdvertisingBooster booster = getActiveBooster();
		if (booster != null) {
			final String endDate = DATE_FORMATTER.format(booster.getEndDate());
			return messageSource.getMessage(TRANSLATION_KEY_OWNER_INFO,
					new Object[] { getTitle(calmObject), endDate }, locale);
		} else {
			return null;
		}
	}
}
