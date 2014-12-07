package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.geo.model.Country;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class PopularCountriesSupportImpl implements PopularSupport {

	private static final String KEY_TITLE = "block.popular.countries";
	private PopularSupport basePopularSupport;
	private MessageSource messageSource;
	private UrlService urlService;
	private Locale locale;

	@Override
	public void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularElements, Object countObject) {
		this.locale = locale;
		this.urlService = urlService;
		basePopularSupport.initialize(searchType, locale, urlService, parent,
				popularElements, countObject);
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

	@Override
	public String getUrl(CalmObject element) {
		return basePopularSupport.getUrl(element);
	}

	@Override
	public String getIconUrl(CalmObject element) {
		final Country country = (Country) element;
		return urlService.getStaticUrl("/images/flags/"
				+ country.getCode().toLowerCase() + ".png");
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(KEY_TITLE, null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setBasePopularSupport(PopularSupport basePopularSupport) {
		this.basePopularSupport = basePopularSupport;
	}
}
