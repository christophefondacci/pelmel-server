package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;

public class SubGeoItemPopularSupportImpl implements PopularSupport {

	private static final String TRANSLATION_KEY_PREFIX = "search.places.subgeotitle.";
	private PopularSupport basePopularSupport;
	private CalmObject parent;
	private Locale locale;
	private MessageSource messageSource;
	private FacetInformation facetInfo;

	@Override
	public void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularElements, Object countObject) {
		this.locale = locale;
		this.parent = parent;
		// this.facetInfo = (FacetInformation) countObject;
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
		return basePopularSupport.getIconUrl(element);
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(TRANSLATION_KEY_PREFIX
				+ parent.getKey().getType(), null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setBasePopularSupport(PopularSupport basePopularSupport) {
		this.basePopularSupport = basePopularSupport;
	}
}
