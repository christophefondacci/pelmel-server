package com.nextep.proto.blocks.impl;

import java.util.List;

import org.springframework.context.MessageSource;

import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.QuickInfoSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.calm.model.CalmObject;

public class MapQuickInfoSupportImpl implements QuickInfoSupport {

	private static final String KEY_TAG_ICON_PREFIX = "facet.icon.";
	private MessageSource messageSource;

	private UrlService urlService;
	private CalmObject object;

	@Override
	public void initialize(UrlService urlService, CalmObject o) {
		this.object = o;
		this.urlService = urlService;
	}

	@Override
	public String getTitle() {
		return DisplayHelper.getName(object);
	}

	@Override
	public String getDescription() {
		final List<? extends Description> descriptions = object
				.get(Description.class);
		if (descriptions != null && !descriptions.isEmpty()) {
			return descriptions.iterator().next().getDescription();
		}
		return null;
	}

	@Override
	public List<? extends Tag> getTags() {
		return object.get(Tag.class);
	}

	@Override
	public String getTagIconUrl(Tag tag) {
		return urlService.getStaticUrl(messageSource.getMessage(
				KEY_TAG_ICON_PREFIX + tag.getKey().toString(), null, null));
	}

	@Override
	public String getThumbnailUrl() {
		final Media m = MediaHelper.getSingleMedia(object);
		if (m != null) {
			return urlService.getMediaUrl(m.getMiniThumbUrl());
		} else {
			return urlService.getStaticUrl(MediaHelper.getNoThumbUrl(object));
		}
	}

	@Override
	public String getOverviewUrl() {
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), object);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private GeographicItem getLocalizationObject() {
		GeographicItem localization = null;
		if (object instanceof Place) {
			localization = ((Place) object).getCity();
		} else if (object instanceof GeographicItem) {
			localization = (GeographicItem) object;
		}
		return localization;
	}

	@Override
	public String getLocalization() {
		GeographicItem localization = getLocalizationObject();
		if (localization != null) {
			return DisplayHelper.getName(localization);
		}
		return null;
	}

	@Override
	public String getLocalizationUrl() {
		final GeographicItem localization = getLocalizationObject();
		SearchType searchType = SearchType.BARS;
		if (object instanceof Place) {
			searchType = SearchType.fromPlaceType(((Place) object)
					.getPlaceType());
		}
		if (localization != null) {
			return urlService.buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), localization,
					searchType);
		}
		return null;
	}
}
