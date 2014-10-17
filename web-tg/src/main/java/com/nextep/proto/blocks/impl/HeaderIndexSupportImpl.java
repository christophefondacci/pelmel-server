package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SeoHelper;
import com.nextep.proto.model.SearchType;
import com.videopolis.calm.model.CalmObject;

public class HeaderIndexSupportImpl implements HeaderSupport {

	private static final String KEY_DESCRIPTION = "header.description.index";
	private static final String KEY_TITLE = "header.title.index";
	private Locale locale;
	private MessageSource messageSource;
	private String domainName;
	private CalmObject obj;

	@Override
	public void initialize(Locale locale, CalmObject element,
			SearchSupport searchSupport, SearchType searchType) {
		this.locale = locale;
		this.obj = element;
	}

	@Override
	public CalmObject getElement() {
		return obj;
	}

	@Override
	public String getRobotsTags() {
		if (SeoHelper.isSeoBlocked()) {
			return "NOINDEX,NOFOLLOW";
		} else {
			return "INDEX,FOLLOW";
		}
	}

	@Override
	public String getDescription() {
		return messageSource.getMessage(KEY_DESCRIPTION, null, locale);
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(KEY_TITLE, null, locale);
	}

	@Override
	public String getLanguage() {
		if (LocalizationHelper.isLanguageSupported(locale)) {
			return locale.getLanguage();
		}
		return "en";
	}

	@Override
	public List<String> getAvailableLanguages() {
		return LocalizationHelper.getSupportedLanguages();
	}

	@Override
	public String getCanonical() {
		return LocalizationHelper.buildUrl(locale, domainName, "/");
	}

	@Override
	public String getAlternate(String language) {
		return LocalizationHelper.buildUrl(language, domainName, "/");
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@Override
	public String getFacebookType() {
		return "website";
	}

	@Override
	public String getThumbUrl() {
		return MediaHelper.getImageUrl("/images/fb-logo.png");
	}

	@Override
	public String getSearchType() {
		return SearchType.BARS.name();
	}

	@Override
	public String getPageStyle() {
		return "homepage";
	}

	@Override
	public String getPageTypeLabel() {
		return null;
	}
}
