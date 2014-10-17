package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import com.nextep.events.model.Event;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.ItemsListBoxSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class EventsListBoxSupportImpl implements ItemsListBoxSupport {

	private static final String TRANSLATION_ADD_ACTION = "block.events.addAction";
	private static final String KEY_ICON_ADD = "icon.add";
	private static final Map<Locale, DateFormat> DATE_FORMAT_MAP = new HashMap<Locale, DateFormat>();
	private static final DateFormat HOUR_FORMAT = new SimpleDateFormat(
			"HH'h'mm");
	private MessageSource messageSource;

	private UrlService urlService;
	private Locale locale;
	private List<? extends CalmObject> items;
	private CalmObject parent;
	private String translationKeyTitle = "block.events.title";

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> items) {
		this.urlService = urlService;
		this.locale = locale;
		this.parent = parent;
		this.items = items;
	}

	@Override
	public String getBoxTitle() {
		return messageSource.getMessage(translationKeyTitle, null, locale);
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
	public String getItemDescription(CalmObject item) {
		final Event event = (Event) item;
		final Date startDate = event.getStartDate();
		if (startDate != null) {
			DateFormat format = DATE_FORMAT_MAP.get(locale);
			if (format == null) {
				// Specific reversed format for english locale
				if ("en".equals(locale.getLanguage())) {
					format = new SimpleDateFormat("EEEE, MMMM dd", locale);
				} else {
					format = new SimpleDateFormat("EEEE dd MMMM", locale);
				}
				DATE_FORMAT_MAP.put(locale, format);
			}
			return format.format(startDate);
		} else {
			return "";
		}
	}

	@Override
	public String getItemIconUrl(CalmObject item) {
		final List<? extends Media> media = item.get(Media.class);
		if (media != null && !media.isEmpty()) {
			return media.iterator().next().getThumbUrl();
		} else {
			return MediaHelper.getImageUrl("/images/icon-event-48.png");
		}
	}

	@Override
	public String getItemUrl(CalmObject item) {
		return urlService.getEventOverviewUrl("mainContent", item);
	}

	@Override
	public String getItemTitlePrefix(CalmObject item) {
		final Event event = (Event) item;
		final Date startDate = event.getStartDate();
		final Date endDate = event.getEndDate();
		if (startDate != null) {
			return HOUR_FORMAT.format(startDate) + " - "
					+ HOUR_FORMAT.format(endDate);
		} else {
			return "";
		}
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getActionIconUrl() {
		return messageSource.getMessage(KEY_ICON_ADD, null, locale);
	}

	@Override
	public String getActionUrl() {
		return urlService.getEventEditionFormUrl(
				DisplayHelper.getDefaultAjaxContainer(), parent.getKey());
	}

	@Override
	public String getActionText() {
		return messageSource.getMessage(TRANSLATION_ADD_ACTION, null, locale);
	}

	@Override
	public String getLangAttribute(CalmObject item) {
		return null;
	}

	public void setTranslationKeyTitle(String translationKeyTitle) {
		this.translationKeyTitle = translationKeyTitle;
	}
}
