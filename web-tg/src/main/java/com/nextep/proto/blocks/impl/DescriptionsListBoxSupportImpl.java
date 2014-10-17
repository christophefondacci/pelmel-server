package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.descriptions.model.Description;
import com.nextep.proto.blocks.ItemsListBoxSupport;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class DescriptionsListBoxSupportImpl implements ItemsListBoxSupport {

	private static final String KEY_DESC_BOX_TITLE = "block.descritions.title";
	private static final String KEY_ICON_LOCALE_PREFIX = "locale.icon.";
	private MessageSource messageSource;

	private Locale locale;
	// private CalmObject parent;
	private List<? extends CalmObject> descriptions;
	private String baseUrl;
	private boolean localizedDescriptionExists = false;
	private List<Description> localizedDescriptions = Collections.emptyList();

	@Override
	public void initialize(UrlService urlService, Locale locale,
			CalmObject parent, List<? extends CalmObject> items) {
		this.locale = locale;
		// this.parent = parent;
		this.descriptions = items;
		// If we are in a supported locale we filter description so that :
		// - Only french descriptions appears on french when at least 1 french
		// desc exists
		// - If none exists, we display everything
		// All this because of SEO
		final String currentLanguage = locale.getLanguage();
		if (LocalizationHelper.isLanguageSupported(locale)) {
			localizedDescriptions = new ArrayList<Description>();
			for (CalmObject o : items) {
				final Description d = (Description) o;
				if (d != null && d.getLocale() != null) {
					final String descLanguage = d.getLocale().getLanguage();
					if (currentLanguage.equals(descLanguage)) {
						localizedDescriptionExists = true;
						localizedDescriptions.add(d);
					}
				}
			}
		}
	}

	@Override
	public String getBoxTitle() {
		return messageSource.getMessage(KEY_DESC_BOX_TITLE, null, locale);
	}

	@Override
	public List<? extends CalmObject> getItems() {
		// Filtering
		if (localizedDescriptionExists) {
			return localizedDescriptions;
		} else {
			return descriptions;
		}
	}

	@Override
	public String getItemTitle(CalmObject item) {
		return null;
	}

	@Override
	public String getItemTitlePrefix(CalmObject item) {
		return null;
	}

	@Override
	public String getItemDescription(CalmObject item) {
		return ((Description) item).getDescription();
	}

	@Override
	public String getItemIconUrl(CalmObject item) {
		final Locale l = ((Description) item).getLocale();
		if (l != null) {
			return baseUrl
					+ messageSource.getMessage(
							KEY_ICON_LOCALE_PREFIX + l.getLanguage(), null,
							locale);
		}
		return "";
	}

	@Override
	public String getItemUrl(CalmObject item) {
		return null;
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

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getLangAttribute(CalmObject item) {
		final Description desc = (Description) item;
		final String lang = desc.getLocale().getLanguage();
		return "lang=\"" + lang + "\"";
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
}
