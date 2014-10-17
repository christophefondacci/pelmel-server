package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.GeoHelper;
import com.nextep.proto.helpers.SeoHelper;
import com.nextep.proto.model.SearchType;
import com.videopolis.calm.model.CalmObject;

public class HeaderMapSupportImpl implements HeaderSupport {

	private static final String KEY_TRANSLATION_DESC = "header.description.map";
	private static final String KEY_ITEM_TYPE = "item.type.";
	private MessageSource messageSource;

	private HeaderSupport baseHeaderSupport;
	private SearchSupport searchSupport;
	private CalmObject element;
	private Locale locale;

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		this.locale = locale;
		this.element = element;
		this.searchSupport = searchSupport;
		baseHeaderSupport
				.initialize(locale, element, searchSupport, searchType);
	}

	@Override
	public CalmObject getElement() {
		return element;
	}

	@Override
	public String getRobotsTags() {
		if (SeoHelper.isSeoBlocked()) {
			return "NOINDEX,NOFOLLOW";
		} else {
			return baseHeaderSupport.getRobotsTags();
		}
	}

	@Override
	public String getDescription() {
		final int results = searchSupport.getResultsCount();
		final String localization = GeoHelper.buildShortLocalizationString(
				element, DisplayHelper.getName(element));
		final String cityName = DisplayHelper.getName(element);
		return messageSource.getMessage(KEY_TRANSLATION_DESC, new Object[] {
				cityName, localization, results }, locale);
	}

	@Override
	public String getTitle() {
		return baseHeaderSupport.getTitle();
	}

	@Override
	public String getThumbUrl() {
		return baseHeaderSupport.getThumbUrl();
	}

	@Override
	public String getFacebookType() {
		return baseHeaderSupport.getFacebookType();
	}

	@Override
	public String getLanguage() {
		return baseHeaderSupport.getLanguage();
	}

	@Override
	public List<String> getAvailableLanguages() {
		return baseHeaderSupport.getAvailableLanguages();
	}

	@Override
	public String getCanonical() {
		return baseHeaderSupport.getCanonical();
	}

	@Override
	public String getAlternate(String language) {
		return baseHeaderSupport.getAlternate(language);
	}

	@Override
	public String getSearchType() {
		return baseHeaderSupport.getSearchType();
	}

	public void setBaseHeaderSupport(HeaderSupport baseHeaderSupport) {
		this.baseHeaderSupport = baseHeaderSupport;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getPageStyle() {
		return baseHeaderSupport.getPageStyle();
	}

	@Override
	public String getPageTypeLabel() {
		return messageSource.getMessage(KEY_ITEM_TYPE + getPageStyle(), null,
				locale);
	}
}
