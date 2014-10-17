package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.media.model.Media;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

public class FavoriteUsersSupportImpl implements FavoritesSupport {

	private static final String KEY_TITLE = "user.favorite.title";
	private MessageSource messageSource;

	private UrlService urlService;
	private Locale locale;
	private CalmObject parent;
	private List<? extends CalmObject> favorites;

	@Override
	public void initilialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> favorites) {
		this.urlService = urlService;
		this.locale = locale;
		this.parent = parent;
		this.favorites = favorites;
	}

	@Override
	public void setMetadata(Object metadata) {

	}

	@Override
	public List<Object> getCategories() {
		if (favorites != null && !favorites.isEmpty()) {
			final List<Object> list = new ArrayList<Object>(1);
			list.add(1);
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategoryTitle(Object category) {
		return null;
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(KEY_TITLE, null, locale);
	}

	@Override
	public List<? extends CalmObject> getFavorites(String category) {
		return favorites;
	}

	@Override
	public String getFavoriteName(CalmObject favoriteObj) {
		return ((User) favoriteObj).getPseudo();
	}

	@Override
	public String getFavoriteNamePrefix(CalmObject favoriteObj) {
		return null;
	}

	@Override
	public String getFavoriteImageUrl(CalmObject favoriteObj) {
		final Media media = MediaHelper.getSingleMedia(favoriteObj);
		if (media != null) {
			return media.getThumbUrl();
		} else {
			return MediaHelper.getNoThumbUrl(favoriteObj);
		}
	}

	@Override
	public String getFavoriteLinkUrl(CalmObject favoriteObj) {
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), favoriteObj);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getCategoryStyle(Object category) {
		return null;
	}
}
