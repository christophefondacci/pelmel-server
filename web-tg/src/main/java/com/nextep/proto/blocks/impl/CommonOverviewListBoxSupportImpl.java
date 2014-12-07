package com.nextep.proto.blocks.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.nextep.descriptions.model.Description;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.OverviewListSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class CommonOverviewListBoxSupportImpl implements OverviewListSupport {

	private MessageSource messageSource;
	private String titleKey;
	private UrlService urlService;
	private Locale locale;
	// private CalmObject parent;
	private List<? extends CalmObject> items;
	private Map<ItemKey, Description> descriptionMap;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> items) {
		this.urlService = urlService;
		this.locale = locale;
		// this.parent = parent;
		this.items = items;
		this.descriptionMap = new HashMap<ItemKey, Description>();
		// Pre-building descriptions map as it avoids to generate it twice (for
		// language and text computation)
		for (CalmObject item : items) {
			final Description description = DisplayHelper.getSingleDescription(
					item, locale);
			descriptionMap.put(item.getKey(), description);
		}
	}

	@Override
	public String getBoxTitle() {
		return messageSource.getMessage(titleKey, null, locale);
	}

	@Override
	public List<? extends CalmObject> getItems() {
		return items;
	}

	@Override
	public String getItemTitle(CalmObject item) {
		return DisplayHelper.getName(item);
	}

	@Override
	public String getItemTitlePrefix(CalmObject item) {
		return null;
	}

	@Override
	public String getItemDescription(CalmObject item) {
		final Description description = descriptionMap.get(item.getKey());
		String desc = (description != null ? description.getDescription() : "");
		if (desc.length() > 400) {
			return desc.substring(0, 400) + "...";
		} else {
			return desc;
		}
	}

	@Override
	public String getItemIconUrl(CalmObject item) {
		final Media m = MediaHelper.getSingleMedia(item);
		if (m != null) {
			return urlService.getMediaUrl(m.getThumbUrl());
		} else {
			return null;
		}
	}

	@Override
	public String getItemUrl(CalmObject item) {
		return urlService.getOverviewUrl(
				DisplayHelper.getDefaultAjaxContainer(), item);
	}

	@Override
	public String getActionIconUrl() {
		return null;
	}

	@Override
	public String getActionUrl() {
		return null;
	}

	@Override
	public String getActionText() {
		return null;
	}

	@Override
	public String getLangAttribute(CalmObject item) {
		final Description description = descriptionMap.get(item.getKey());
		if (description != null) {
			return "lang=\"" + description.getLocale().getLanguage() + "\"";
		} else {
			return null;
		}
	}

	@Override
	public String getLocalization(CalmObject item) {
		final GeographicItem geo = GeoHelper.extractLocalization(item);
		if (item instanceof Place) {
			return ((Place) item).getAddress1();
		} else {

			return geo != null ? DisplayHelper.getName(geo) : null;
		}
	}

	@Override
	public String getLocalizationUrl(CalmObject item) {
		final GeographicItem geo = GeoHelper.extractLocalization(item);
		if (geo != item && geo != null) {
			return urlService.getOverviewUrl(
					DisplayHelper.getDefaultAjaxContainer(), geo);
		} else {
			return null;
		}
	}

	public void setTitleKey(String title) {
		this.titleKey = title;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}
