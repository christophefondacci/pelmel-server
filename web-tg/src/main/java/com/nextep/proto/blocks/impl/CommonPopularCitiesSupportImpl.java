package com.nextep.proto.blocks.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.proto.blocks.PopularSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.model.FacetCount;

public class CommonPopularCitiesSupportImpl implements PopularSupport {

	private static final Log LOGGER = LogFactory
			.getLog(CommonPopularCitiesSupportImpl.class);
	private String translationKeyTitle = "block.popular.cities.title";

	private MessageSource messageSource;

	private SearchType searchType;
	private Locale locale;
	private UrlService urlService;
	private CalmObject parent;
	private List<? extends CalmObject> popularElements;
	private Map<ItemKey, Integer> popularCountMap;

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(SearchType searchType, Locale locale,
			UrlService urlService, CalmObject parent,
			List<? extends CalmObject> popularElements, Object countObject) {
		this.searchType = searchType;
		this.locale = locale;
		this.urlService = urlService;
		this.parent = parent;
		this.popularElements = popularElements;
		final List<FacetCount> facetCounts = (List<FacetCount>) countObject;
		popularCountMap = new HashMap<ItemKey, Integer>();
		try {
			if (facetCounts != null) {
				for (FacetCount facetCount : facetCounts) {
					final String facetCode = facetCount.getFacet()
							.getFacetCode();
					final ItemKey itemKey = CalmFactory.parseKey(facetCode);
					popularCountMap.put(itemKey, facetCount.getCount());
				}
			}
		} catch (CalException e) {
			LOGGER.error("Unable to extract cities facet count for popular cities block : "
					+ e.getMessage());
		}
	}

	@Override
	public List<? extends CalmObject> getPopularElements() {
		return popularElements;
	}

	@Override
	public String getName(CalmObject element) {
		return DisplayHelper.getName(element);
	}

	@Override
	public int getCount(CalmObject element) {
		return popularCountMap.get(element.getKey());
	}

	@Override
	public String getUrl(CalmObject element) {
		// Default URL is places in city list
		return urlService.buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), element, searchType,
				null, 0);
	}

	@Override
	public String getIconUrl(CalmObject element) {
		return urlService.getStaticUrl("/images/icon-city.gif");
	}

	@Override
	public String getTitle() {
		return messageSource.getMessage(translationKeyTitle,
				new Object[] { DisplayHelper.getName(parent) }, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setTranslationKeyTitle(String translationKeyTitle) {
		this.translationKeyTitle = translationKeyTitle;
	}
}
