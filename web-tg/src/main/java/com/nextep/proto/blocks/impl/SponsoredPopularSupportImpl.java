package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class SponsoredPopularSupportImpl implements PopularSupport {

	// Constants declarations
	private static final String KEY_DEFAULT_ICON_PREFIX = "media.default.places.";

	// Injected services
	private MessageSource messageSource;

	// Internal variables
	private Locale locale;
	private UrlService urlService;
	private List<? extends CalmObject> elements;

	@Override
	public void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularElements, Object countObject) {
		this.locale = locale;
		this.urlService = urlService;
		this.elements = popularElements;
	}

	@Override
	public List<? extends CalmObject> getPopularElements() {
		return elements;
	}

	@Override
	public String getName(CalmObject element) {
		return DisplayHelper.getName(element, locale);
	}

	@Override
	public int getCount(CalmObject element) {
		return 0;
	}

	@Override
	public String getUrl(CalmObject element) {
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), element);
	}

	@Override
	public String getIconUrl(CalmObject element) {
		final Media m = MediaHelper.getSingleMedia(element);
		// Adding a default place URL
		if (m == null) {
			final Place place = (Place) element;
			return messageSource.getMessage(
					KEY_DEFAULT_ICON_PREFIX + place.getPlaceType(), null,
					locale);
		} else {
			return urlService.getMediaUrl(m.getThumbUrl());
		}
	}

	@Override
	public String getTitle() {
		return null;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
