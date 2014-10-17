package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class PlacePopularCitiesSupportImpl implements PopularSupport {

	private static final String TRANSLATION_KEY_TITLE = "block.popular.citiesPlace.title";

	private PopularSupport basePopularSupport;
	private UrlService urlService;
	private Locale locale;
	private MessageSource messageSource;

	@Override
	public void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularElements, Object countObject) {
		basePopularSupport.initialize(searchType, locale, urlService, parent,
				popularElements, countObject);
		this.urlService = urlService;
		this.locale = locale;
	}

	@Override
	public String getUrl(CalmObject element) {
		return basePopularSupport.getUrl(element);
	}

	@Override
	public String getIconUrl(CalmObject element) {
		return basePopularSupport.getIconUrl(element);
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(TRANSLATION_KEY_TITLE, null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public List<? extends CalmObject> getPopularElements() {
		return basePopularSupport.getPopularElements();
	}

	@Override
	public String getName(CalmObject element) {
		return basePopularSupport.getName(element);
	}

	@Override
	public int getCount(CalmObject element) {
		return basePopularSupport.getCount(element);
	}

	public void setBasePopularSupport(PopularSupport basePopularSupport) {
		this.basePopularSupport = basePopularSupport;
	}
}
