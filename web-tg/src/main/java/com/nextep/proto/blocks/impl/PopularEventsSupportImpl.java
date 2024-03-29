package com.nextep.proto.blocks.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import com.nextep.media.model.Media;
import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;

public class PopularEventsSupportImpl implements PopularSupport {
	private static final String TRANSLATION_KEY_TITLE = "block.popular.events.title";

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
		return urlService.getEventOverviewUrl("mainContent", element);
	}

	@Override
	public String getIconUrl(CalmObject element) {
		final Media m = MediaHelper.getSingleMedia(element);
		if (m != null) {
			return urlService.getMediaUrl(m.getMiniThumbUrl());
		}
		return urlService.getStaticUrl("/images/icon-event-24.png");
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
