package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.FavoritesSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;

public class FavoritePlacesSupportImpl implements FavoritesSupport {

	private static final Log LOGGER = LogFactory
			.getLog(FavoritePlacesSupportImpl.class);
	private static final String ICON_PLACE_TYPE_PREFIX = "facet.icon.";
	private static final String TRANSLATION_PLACE_TYPE_PREFIX = "facet.label.";
	private static final String TRANSLATION_TITLE = "user.like";
	private String titleTranslationKey;
	private MessageSource messageSource;
	private UrlService urlService;
	private Locale locale;
	private CalmObject parent;
	private Map<String, List<CalmObject>> favoritesCategoryMap;
	private List<Object> categories;
	private List<CalmObject> allFavorites;

	@Override
	public void initilialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> favorites) {
		this.urlService = urlService;
		this.locale = locale;
		this.parent = parent;
		categories = new ArrayList<Object>();
		allFavorites = new ArrayList<CalmObject>();
		favoritesCategoryMap = new HashMap<String, List<CalmObject>>();
		for (CalmObject o : favorites) {
			final Place p = (Place) o;
			allFavorites.add(p);
			// Getting favorites list for this category entry
			List<CalmObject> favoritesList = favoritesCategoryMap.get(p
					.getPlaceType());
			if (favoritesList == null) {
				favoritesList = new LinkedList<CalmObject>();
				favoritesCategoryMap.put(p.getPlaceType(), favoritesList);
				categories.add(p.getPlaceType());
			}
			// Adding favorite
			favoritesList.add(p);
		}
	}

	@Override
	public void setMetadata(Object metadata) {
		// No metadata supported
	}

	@Override
	public String getTitle() {
		final String parentUserName = ((User) parent).getPseudo();
		return messageSource.getMessage(titleTranslationKey,
				new Object[] { parentUserName }, locale);
	}

	@Override
	public List<Object> getCategories() {
		return categories;
	}

	@Override
	public String getCategoryTitle(Object category) {
		// Extracting place type
		final String placeType = (String) category;
		// Translating place type
		final String placeTypeKey = TRANSLATION_PLACE_TYPE_PREFIX + placeType;
		String placeTypeLabel = null;
		try {
			// The translation should exist, but since it is dynamically
			// invoked, we're never sure...
			placeTypeLabel = messageSource.getMessage(placeTypeKey, null,
					locale);
		} catch (NoSuchMessageException e) {
			LOGGER.error("No message found for search title : " + placeTypeKey);
		}
		// Injecting the place type translation into the category translation
		// title
		return messageSource.getMessage(TRANSLATION_TITLE,
				new Object[] { placeTypeLabel }, locale);

	}

	@Override
	public List<? extends CalmObject> getFavorites(String category) {
		if (category == null) {
			return allFavorites;
		} else {
			return favoritesCategoryMap.get(category);
		}
	}

	@Override
	public String getFavoriteName(CalmObject favoriteObj) {
		return DisplayHelper.getName(favoriteObj);
	}

	@Override
	public String getFavoriteImageUrl(CalmObject favoriteObj) {
		final Media media = MediaHelper.getSingleMedia(favoriteObj);
		if (media != null) {
			return urlService.getMediaUrl(media.getThumbUrl());
		} else {
			return urlService.getStaticUrl(MediaHelper
					.getNoThumbUrl(favoriteObj));
		}
	}

	@Override
	public String getFavoriteLinkUrl(CalmObject favoriteObj) {
		return urlService.getPlaceOverviewUrl("mainContent", favoriteObj);
	}

	public void setTitleTranslationKey(String titleTranslationKey) {
		this.titleTranslationKey = titleTranslationKey;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getFavoriteNamePrefix(CalmObject favoriteObj) {
		if (favoriteObj instanceof Place) {
			return ((Place) favoriteObj).getCity().getName();
		}
		return "";
	}

	@Override
	public String getCategoryStyle(Object category) {
		return (String) category;
	}
}
