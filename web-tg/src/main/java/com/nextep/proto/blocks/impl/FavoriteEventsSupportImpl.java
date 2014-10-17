package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.nextep.events.model.Event;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class FavoriteEventsSupportImpl implements FavoritesSupport {

	private MessageSource messageSource;

	private final String TRANSLATION_CATEGORY_PREFIX = "event.type.";
	private final String TRANSLATION_TITLE = "event.favorite.title";

	private static final Map<Locale, DateFormat> DATE_FORMAT_MAP = new HashMap<Locale, DateFormat>();
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
		// No metadata supported
	}

	@Override
	public List<Object> getCategories() {
		if (favorites != null && !favorites.isEmpty()) {
			final List<Object> list = new ArrayList<Object>(1);
			list.add("party");
			return list;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getCategoryTitle(Object category) {
		return messageSource.getMessage(TRANSLATION_CATEGORY_PREFIX + category,
				null, locale);
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(TRANSLATION_TITLE, null, locale);
	}

	@Override
	public List<? extends CalmObject> getFavorites(String category) {
		return favorites;
	}

	@Override
	public String getFavoriteName(CalmObject favoriteObj) {
		return DisplayHelper.getName(favoriteObj);
	}

	@Override
	public String getFavoriteImageUrl(CalmObject favoriteObj) {
		return MediaHelper.getSingleMediaUrl(favoriteObj);
	}

	@Override
	public String getFavoriteNamePrefix(CalmObject favoriteObj) {
		final Event event = (Event) favoriteObj;
		final Date eventDate = event.getStartDate();
		if (eventDate != null) {
			DateFormat dateFormat = DATE_FORMAT_MAP.get(locale);
			if (dateFormat == null) {
				dateFormat = new SimpleDateFormat("MMM-dd", locale);
				DATE_FORMAT_MAP.put(locale, dateFormat);
			}
			return dateFormat.format(eventDate) + "<br>";
		}
		return "";
	}

	@Override
	public String getFavoriteLinkUrl(CalmObject favoriteObj) {
		return urlService.getEventOverviewUrl("mainContent", favoriteObj);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getCategoryStyle(Object category) {
		return null;
	}
}
