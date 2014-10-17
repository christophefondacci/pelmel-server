package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.videopolis.calm.model.CalmObject;

public class HeaderTitledSupportImpl implements HeaderSupport {

	private static final String KEY_ITEM_TYPE = "item.type.";

	private Locale locale;
	private CalmObject element;
	private String titleMessageKey;
	private String descriptionMessageKey;
	private MessageSource messageSource;
	private SearchType searchType;
	private String pageStyle;
	private String canonical;
	private String robotsTags = "NOINDEX,NOFOLLOW";

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		this.locale = locale;
		this.element = element;
		this.searchType = searchType;
	}

	@Override
	public String getRobotsTags() {
		return robotsTags;
	}

	@Override
	public CalmObject getElement() {
		return element;
	}

	@Override
	public String getDescription() {
		try {
			return messageSource.getMessage(descriptionMessageKey,
					new Object[] { DisplayHelper.getName(element) }, locale);
		} catch (NoSuchMessageException e) {
			return "";
		}
	}

	@Override
	public String getTitle() {
		try {
			return messageSource.getMessage(titleMessageKey,
					new Object[] { DisplayHelper.getName(element) }, locale);
		} catch (NoSuchMessageException e) {
			return "";
		}
	}

	@Override
	public String getLanguage() {
		if (LocalizationHelper.isLanguageSupported(locale)) {
			return locale.getLanguage();
		}
		return Constants.DEFAULT_LANGUAGE;
	}

	@Override
	public List<String> getAvailableLanguages() {
		return LocalizationHelper.getSupportedLanguages();
	}

	@Override
	public String getCanonical() {
		return canonical;
	}

	@Override
	public String getAlternate(String language) {
		return null;
	}

	public void setTitleMessageKey(String titleMessageKey) {
		this.titleMessageKey = titleMessageKey;
	}

	public void setDescriptionMessageKey(String descriptionMessageKey) {
		this.descriptionMessageKey = descriptionMessageKey;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getFacebookType() {
		return null;
	}

	@Override
	public String getThumbUrl() {
		return null;
	}

	@Override
	public String getSearchType() {
		return searchType.name();
	}

	@Override
	public String getPageStyle() {
		if (pageStyle == null) {
			return GeoHelper.getPageStyle(element);
		} else {
			return pageStyle;
		}
	}

	@Override
	public String getPageTypeLabel() {
		return messageSource.getMessage(KEY_ITEM_TYPE + getPageStyle(), null,
				locale);
	}

	public void setPageStyle(String pageStyle) {
		this.pageStyle = pageStyle;
	}

	public void setCanonical(String canonical) {
		this.canonical = canonical;
	}

	public void setRobotsTags(String robotsTags) {
		this.robotsTags = robotsTags;
	}
}
