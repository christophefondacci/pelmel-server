package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.events.model.Event;
import com.nextep.proto.blocks.OverviewListSupport;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class EventOverviewListBoxSupportImpl implements OverviewListSupport {
	private static final Map<Locale, DateFormat> DATE_FORMAT_MAP = new HashMap<Locale, DateFormat>();
	private OverviewListSupport baseListBoxSupport;
	private Locale locale;

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> items) {
		this.locale = locale;
		baseListBoxSupport.initialize(urlService, locale, parent, items);
	}

	@Override
	public String getBoxTitle() {
		return baseListBoxSupport.getBoxTitle();
	}

	@Override
	public List<? extends CalmObject> getItems() {
		return baseListBoxSupport.getItems();
	}

	@Override
	public String getItemTitle(CalmObject item) {
		return baseListBoxSupport.getItemTitle(item);
	}

	@Override
	public String getItemTitlePrefix(CalmObject item) {
		final Event event = (Event) item;
		final Date eventDate = event.getStartDate();
		if (eventDate != null) {
			DateFormat dateFormat = DATE_FORMAT_MAP.get(locale);
			if (dateFormat == null) {
				dateFormat = new SimpleDateFormat("EEEE dd MMMM", locale);
				DATE_FORMAT_MAP.put(locale, dateFormat);
			}
			return dateFormat.format(eventDate).toUpperCase();
		} else {
			return "";
		}
	}

	@Override
	public String getItemDescription(CalmObject item) {
		return baseListBoxSupport.getItemDescription(item);
	}

	@Override
	public String getItemIconUrl(CalmObject item) {
		return baseListBoxSupport.getItemIconUrl(item);
	}

	@Override
	public String getItemUrl(CalmObject item) {
		return baseListBoxSupport.getItemUrl(item);
	}

	@Override
	public String getActionIconUrl() {
		return baseListBoxSupport.getActionIconUrl();
	}

	@Override
	public String getActionUrl() {
		return baseListBoxSupport.getActionUrl();
	}

	@Override
	public String getActionText() {
		return baseListBoxSupport.getActionText();
	}

	@Override
	public String getLangAttribute(CalmObject item) {
		return baseListBoxSupport.getLangAttribute(item);
	}

	public void setBaseListBoxSupport(OverviewListSupport baseListBoxSupport) {
		this.baseListBoxSupport = baseListBoxSupport;
	}

	@Override
	public String getLocalization(CalmObject item) {
		return baseListBoxSupport.getLocalization(item);
	}

	@Override
	public String getLocalizationUrl(CalmObject item) {
		return baseListBoxSupport.getLocalizationUrl(item);
	}
}
