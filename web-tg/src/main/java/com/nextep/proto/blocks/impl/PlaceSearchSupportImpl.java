package com.nextep.proto.blocks.impl;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

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
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.impl.FacetCategoryImpl;
import com.videopolis.smaug.common.model.impl.FacetImpl;
import com.videopolis.smaug.model.FacetCount;

public class PlaceSearchSupportImpl implements SearchSupport {

	private static final Log LOGGER = LogFactory
			.getLog(PlaceSearchSupportImpl.class);
	private static final String KEY_DEFAULT_ICON_PREFIX = "media.default.places.";
	private static final String KEY_TRANSLATION_TITLE = "block.places.facetTitle";
	private static final String KEY_TRANSLATION_TYPE = "searchType.PLACES";
	private static final String KEY_TRANSLATION_CATEGORY = "facets.categories.place";
	private static final String KEY_TRANSLATION_PLACETYPE_PREFIX = "place.type.";
	private static final String KEY_ITEM_TYPE = "item.type.";
	private static final String KEY_CATEGORY_LABEL = "tooltip.placeTypeLocation";

	private SearchSupport commonSearchSupport;

	private SearchType searchType;
	private UrlService urlService;
	private Locale locale;
	private FacetInformation facetInfo;
	private PaginationInfo pagination;
	private CalmObject parentItem;
	private String locationName;
	private List<? extends CalmObject> results;
	private MessageSource messageSource;

	@Override
	public void initialize(SearchType searchType, UrlService urlService,
			Locale locale, CalmObject geoItem, String locationName,
			FacetInformation facetInfo, PaginationInfo pagination,
			List<? extends CalmObject> results) {
		this.searchType = searchType;
		this.urlService = urlService;
		this.locale = locale;
		this.parentItem = geoItem;
		this.locationName = locationName;
		this.facetInfo = facetInfo;
		this.pagination = pagination;
		if (results == null) {
			this.results = Collections.emptyList();
		} else {
			this.results = results;
		}
		commonSearchSupport.initialize(searchType, urlService, locale, geoItem,
				locationName, facetInfo, pagination, results);
	}

	@Override
	public FacetInformation getFacetInformation() {
		return facetInfo;
	}

	@Override
	public String getFacetsBoxTitle() {
		return messageSource.getMessage(KEY_TRANSLATION_TITLE,
				new Object[] { locationName }, locale);
	}

	@Override
	public String getFacetCategoryTitle(FacetCategory facetCategory) {
		// Extracting place type
		final String placeType = searchType.getSubtype();
		// Translating place type
		final String placeTypeKey = KEY_TRANSLATION_PLACETYPE_PREFIX
				+ placeType;
		String placeTypeLabel = null;
		try {
			// The translation should exist, but since it is dynamically
			// invoked, we're never sure...
			placeTypeLabel = messageSource.getMessage(placeTypeKey, null,
					locale);
		} catch (NoSuchMessageException e) {
			LOGGER.error("No message found for search title : " + placeTypeKey);
		}
		// Injecting the place type translation into the category translation
		// title
		return messageSource.getMessage(KEY_TRANSLATION_CATEGORY,
				new Object[] { placeTypeLabel }, locale);
	}

	@Override
	public String getSearchTitle() {
		final Collection<Facet> facets = facetInfo.getFacetFilters();
		return SearchHelper.buildSearchTitle(messageSource, locale,
				"search.places.title", getResultsCount(),
				getSearchLocationName(), facets);

	}

	@Override
	public String getSearchLocationName() {
		return locationName;
	}

	@Override
	public List<FacetCategory> getFacetCategories() {
		return Arrays.asList(SearchHelper.getAmenitiesFacetCategory(),
				SearchHelper.getTagFacetCategory());
	}

	@Override
	public List<FacetCount> getFacets(FacetCategory facetCategory) {
		return commonSearchSupport.getFacets(facetCategory);
	}

	@Override
	public int getResultsCount() {
		return pagination.getItemCount();
	}

	@Override
	public String getResultTitle(CalmObject searchResult) {
		return commonSearchSupport.getResultTitle(searchResult);
	}

	@Override
	public String getResultThumbnailStyle(CalmObject searchResult) {
		return commonSearchSupport.getResultThumbnailStyle(searchResult);
	}

	@Override
	public String getResultThumbnailUrl(CalmObject searchResult) {
		final String thumbUrl = commonSearchSupport
				.getResultThumbnailUrl(searchResult);
		// Adding a default place URL
		// if (thumbUrl == null) {
		// return messageSource.getMessage("media.default.PLACES", null,
		// locale);
		// } else {
		return thumbUrl;
		// }
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
	public List<? extends CalmObject> getSearchResults() {
		return results;
	}

	@Override
	public String getResultUrl(CalmObject searchResult) {
		return urlService.getOverviewUrl("mainContent", searchResult);
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

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String getFacetAddedUrl(Facet facet) {
		return commonSearchSupport.getFacetAddedUrl(facet);
	}

	@Override
	public boolean isSelected(Facet facet) {
		return commonSearchSupport.isSelected(facet);
	}

	@Override
	public String getFacetRemovedUrl(Facet facet) {
		return commonSearchSupport.getFacetRemovedUrl(facet);
	}

	@Override
	public String getTagUrl(Tag tag) {
		final Facet f = buildFacetFromTag(tag);
		return getFacetAddedUrl(f);
	}

	private Facet buildFacetFromTag(Tag tag) {
		final FacetImpl f = new FacetImpl();
		final FacetCategoryImpl c = new FacetCategoryImpl();
		c.setCategoryCode("tags");
		f.setFacetCategory(c);
		f.setFacetCode(tag.getKey().toString());
		return f;
	}

	@Override
	public String getRemoveTagUrl(Tag tag) {
		final Facet f = buildFacetFromTag(tag);
		return getFacetRemovedUrl(f);
	}

	@Override
	public boolean isTagSelected(Tag tag) {
		return commonSearchSupport.isTagSelected(tag);
	}

	@Override
	public String getFacetIconUrl(String itemKey) {
		return commonSearchSupport.getFacetIconUrl(itemKey);
	}

	@Override
	public FacetRange getFacetRange(String category) {
		// No range support for places
		return null;
	}

	@Override
	public String getOverlayText(CalmObject result) {
		return null;
	}

	@Override
	public String getFacetRangeUrl(String category, String minPlaceHolder,
			String maxPlaceholder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FacetRange getCurrentFacetRange(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResultLocalizationName(CalmObject result) {
		return DisplayHelper.getName(((Place) result).getCity(), locale);
	}

	@Override
	public String getResultLocalizationUrl(CalmObject result) {
		final Place place = (Place) result;
		final City city = place.getCity();
		if (city != null) {
			return urlService.buildSearchUrl(
					DisplayHelper.getDefaultAjaxContainer(), city,
					SearchType.fromPlaceType(place.getPlaceType()), null, 0);
		}
		return "#";
	}

	public void setCommonSearchSupport(SearchSupport commonSearchSupport) {
		this.commonSearchSupport = commonSearchSupport;
	}

	@Override
	public String getCustomText(CalmObject result, String key) {
		return commonSearchSupport.getCustomText(result, key);
	}

	@Override
	public String getResultTitleIconUrl(CalmObject searchResult) {
		return getFacetIconUrl(((Place) searchResult).getPlaceType());
	}

	@Override
	public String getSearchDescription() {
		return commonSearchSupport.getSearchDescription();
	}

	@Override
	public String getResultCategoryLinkLabel(CalmObject result) {
		String type = null;
		if (result instanceof Place) {
			type = ((Place) result).getPlaceType();
		} else if (result instanceof Event) {
			type = "events";
		}
		final String name = messageSource.getMessage(KEY_ITEM_TYPE + type,
				null, locale);
		final String localization = getResultLocalizationName(result);
		final String template = messageSource.getMessage(KEY_CATEGORY_LABEL,
				null, locale);
		final String label = MessageFormat.format(template,
				DisplayHelper.titleCased(name), localization);
		return label;
	}
}
