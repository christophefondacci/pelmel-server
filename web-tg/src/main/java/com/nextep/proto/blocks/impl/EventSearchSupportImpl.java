package com.nextep.proto.blocks.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.model.FacetCount;

public class EventSearchSupportImpl implements SearchSupport {

	private static final Log LOGGER = LogFactory
			.getLog(EventSearchSupportImpl.class);
	private static final String KEY_DEFAULT_ICON = "media.default.events";
	private static final Map<Locale, DateFormat> DATE_FORMAT_MAP = new HashMap<Locale, DateFormat>();
	private static final String TRANSLATION_KEY_FACET_TITLE = "block.facets.events.title";
	private SearchSupport commonSearchSupport;
	private MessageSource messageSource;

	private UrlService urlService;
	private Locale locale;
	private CalmObject parentItem;
	private String locationName;
	private FacetInformation facetInfo;
	private PaginationInfo paginationInfo;
	private List<Event> events;

	@Override
	public void initialize(SearchType searchType, UrlService urlService,
			Locale locale, CalmObject geoItem, String locationName,
			FacetInformation facetInfo, PaginationInfo pagination,
			List<? extends CalmObject> results) {
		this.urlService = urlService;
		this.locale = locale;
		this.parentItem = geoItem;
		this.locationName = locationName;
		this.facetInfo = facetInfo;
		this.paginationInfo = pagination;
		this.events = (List<Event>) results;
		commonSearchSupport.initialize(searchType, urlService, locale, geoItem,
				locationName, facetInfo, pagination, results);
	}

	@Override
	public FacetInformation getFacetInformation() {
		return facetInfo;
	}

	@Override
	public String getSearchTitle() {
		return messageSource.getMessage("search.events.title", new Object[] {
				getResultsCount(), getSearchLocationName() }, locale);
	}

	@Override
	public String getSearchLocationName() {
		return locationName;
	}

	@Override
	public List<FacetCategory> getFacetCategories() {
		final FacetCategory c = SearchHelper.getTagFacetCategory();
		if (!facetInfo.getFacetCounts(c).isEmpty()) {
			return Arrays.asList(c);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public String getFacetCategoryTitle(FacetCategory facetCategory) {
		return commonSearchSupport.getFacetCategoryTitle(facetCategory);
	}

	@Override
	public List<FacetCount> getFacets(FacetCategory facetCategory) {
		return commonSearchSupport.getFacets(facetCategory);
	}

	@Override
	public String getFacetsBoxTitle() {
		return messageSource.getMessage(TRANSLATION_KEY_FACET_TITLE, null,
				locale);
	}

	@Override
	public FacetRange getFacetRange(String category) {
		final FacetCategory c = SearchHelper.getFacetCategory(category);
		final List<FacetCount> counts = facetInfo.getFacetCounts(c);
		if (counts != null && counts.size() == 1) {
			final FacetCount count = counts.iterator().next();
			if (count.getFacet() instanceof FacetRange) {
				return (FacetRange) count.getFacet();
			}
		}
		return null;
	}

	@Override
	public FacetRange getCurrentFacetRange(String category) {
		final FacetCategory c = SearchHelper.getFacetCategory(category);
		final Collection<Facet> facets = facetInfo.getFacetFilters();
		for (Facet f : facets) {
			if (f.getFacetCategory().equals(c) && f instanceof FacetRange) {
				return (FacetRange) f;
			}
		}
		return getFacetRange(category);
	}

	@Override
	public int getResultsCount() {
		return commonSearchSupport.getResultsCount();
	}

	@Override
	public List<? extends CalmObject> getSearchResults() {
		return events;
	}

	@Override
	public String getResultTitle(CalmObject searchResult) {
		final String name = commonSearchSupport.getResultTitle(searchResult);
		final String[] words = name.split(" ");
		if (words.length < 4) {
			return name;
		} else {
			return words[0] + ' ' + words[1] + ' ' + words[2] + ' ' + words[3];
		}
	}

	@Override
	public String getResultThumbnailUrl(CalmObject searchResult) {
		final String thumbUrl = commonSearchSupport
				.getResultThumbnailUrl(searchResult);
		// Adding a default place URL
		if (thumbUrl == null) {
			try {
				final Place localization = searchResult.getUnique(Place.class);
				if (localization != null) {
					return getResultThumbnailUrl(localization);
				}
			} catch (CalException e) {
				LOGGER.error(
						"Unable to extract event localization : "
								+ e.getMessage(), e);
			}
			return messageSource.getMessage(KEY_DEFAULT_ICON, null, locale);
		} else {
			return thumbUrl;
		}
	}

	@Override
	public String getResultMiniThumbUrl(CalmObject searchResult) {
		return commonSearchSupport.getResultMiniThumbUrl(searchResult);
	}

	@Override
	public String getResultDescription(CalmObject searchResult) {
		return commonSearchSupport.getResultDescription(searchResult);
	}

	@Override
	public String getResultUrl(CalmObject searchResult) {
		return urlService.getEventOverviewUrl("mainContent", searchResult);
	}

	@Override
	public List<? extends Tag> getTags(CalmObject searchResult) {
		return commonSearchSupport.getTags(searchResult);
	}

	@Override
	public List<Integer> getPagesList() {
		return commonSearchSupport.getPagesList();
	}

	@Override
	public Integer getCurrentPage() {
		return commonSearchSupport.getCurrentPage();
	}

	@Override
	public String getPageUrl(int page) {
		return commonSearchSupport.getPageUrl(page);
	}

	@Override
	public String getFacetTranslation(String facetCode) {
		return commonSearchSupport.getFacetTranslation(facetCode);
	}

	@Override
	public String getFacetIconUrl(String itemKey) {
		return commonSearchSupport.getFacetIconUrl(itemKey);
	}

	@Override
	public String getFacetAddedUrl(Facet facet) {
		return commonSearchSupport.getFacetAddedUrl(facet);
	}

	@Override
	public String getFacetRemovedUrl(Facet facet) {
		return commonSearchSupport.getFacetRemovedUrl(facet);
	}

	@Override
	public boolean isSelected(Facet facet) {
		return commonSearchSupport.isSelected(facet);
	}

	@Override
	public boolean isTagSelected(Tag tag) {
		return commonSearchSupport.isTagSelected(tag);
	}

	@Override
	public String getTagUrl(Tag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRemoveTagUrl(Tag tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOverlayText(CalmObject result) {
		if (result != null) {
			DateFormat dateFormat = DATE_FORMAT_MAP.get(locale);
			if (dateFormat == null) {
				dateFormat = new SimpleDateFormat("MMM-dd", locale);
				DATE_FORMAT_MAP.put(locale, dateFormat);
			}
			return dateFormat.format(((Event) result).getStartDate());
		}
		return "";
	}

	@Override
	public String getFacetRangeUrl(String category, String minPlaceHolder,
			String maxPlaceholder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResultLocalizationName(CalmObject result) {
		final Event event = (Event) result;
		try {
			final Place p = event.getUnique(Place.class);
			if (p != null) {
				return DisplayHelper.getName(p);
			}
		} catch (CalException e) {
			LOGGER.error("Cannot extract place information from event "
					+ event.getKey() + ": " + e.getMessage());
		}
		return commonSearchSupport.getResultLocalizationName(result);
	}

	@Override
	public String getResultLocalizationUrl(CalmObject result) {
		final Event event = (Event) result;
		try {
			final Place p = event.getUnique(Place.class);
			if (p != null) {
				return urlService.getPlaceOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), p);
			} else {
				final City c = event.getUnique(City.class);
				if (c != null) {
					return urlService.buildSearchUrl(
							DisplayHelper.getDefaultAjaxContainer(), c,
							SearchType.EVENTS, facetInfo, 0);
				}
			}
		} catch (CalException e) {
			LOGGER.error("Cannot extract place information from event "
					+ event.getKey() + ": " + e.getMessage());
		}
		return "#";
	}

	public void setCommonSearchSupport(SearchSupport commonSearchSupport) {
		this.commonSearchSupport = commonSearchSupport;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getCustomText(CalmObject result, String key) {
		DateFormat dateFormat = DATE_FORMAT_MAP.get(locale);
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("MMM-dd", locale);
			DATE_FORMAT_MAP.put(locale, dateFormat);
		}
		return dateFormat.format(((Event) result).getStartDate());
	}

	@Override
	public String getResultTitleIconUrl(CalmObject searchResult) {
		return null;
	}

	@Override
	public String getResultThumbnailStyle(CalmObject searchResult) {
		return commonSearchSupport.getResultThumbnailStyle(searchResult);
	}

	@Override
	public String getSearchDescription() {
		return commonSearchSupport.getSearchDescription();
	}

	@Override
	public String getResultCategoryLinkLabel(CalmObject result) {
		return commonSearchSupport.getResultCategoryLinkLabel(result);
	}
}
