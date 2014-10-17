package com.nextep.proto.blocks.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

import com.nextep.geo.model.City;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.helpers.UserHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.impl.FacetCategoryImpl;
import com.videopolis.smaug.common.model.impl.FacetImpl;
import com.videopolis.smaug.common.model.impl.FacetRangeImpl;
import com.videopolis.smaug.model.FacetCount;

public class UserSearchSupportImpl implements SearchSupport {

	private static final Log LOGGER = LogFactory
			.getLog(UserSearchSupportImpl.class);
	private static final String KEY_DEFAULT_ICON = "media.default.MEN";
	private static final String TRANSLATION_FACET_RANGE_PREFIX = "facets.range.";
	private static final String TRANSLATION_FACET_RANGE_TITLE = "search.users.titleFacetRange";
	private static final String TRANSLATION_FACET_PREFIX = "facet.label.";
	private static final String TRANSLATION_FACET_SENTENCE_PREFIX = "facet.sentence.";
	private static final String TRANSLATION_WITH = "search.users.titleWithFacets";
	private String translationFacetTitle = "block.facets.users.title";

	private SearchSupport commonSearchSupport;

	private SearchType searchType;
	private UrlService urlService;
	private Locale locale;
	private FacetInformation facetInfo;
	private PaginationInfo pagination;
	private CalmObject parentItem;
	private String parentName;
	private List<? extends CalmObject> results;
	private MessageSource messageSource;
	private boolean hideTitleWhenNoResult = false;

	@Override
	public void initialize(SearchType searchType, UrlService urlService,
			Locale locale, CalmObject geoItem, String locationName,
			FacetInformation facetInfo, PaginationInfo pagination,
			List<? extends CalmObject> results) {
		this.urlService = urlService;
		this.locale = locale;
		this.parentItem = geoItem;
		this.parentName = locationName;
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
	public String getFacetsBoxTitle() {
		int count = pagination != null ? pagination.getItemCount() : 0;
		return messageSource.getMessage(translationFacetTitle, new Object[] {
				getSearchLocationName(), count, "", "" }, locale);
	}

	@Override
	public String getSearchTitle() {
		if (!hideTitleWhenNoResult || getResultsCount() > 0) {
			final Collection<Facet> facets = facetInfo.getFacetFilters();
			return SearchHelper.buildSearchTitle(messageSource, locale,
					translationFacetTitle, getResultsCount(),
					getSearchLocationName(), facets);
		} else {
			return null;
		}
	}

	@Override
	public FacetInformation getFacetInformation() {
		return facetInfo;
	}

	@Override
	public String getSearchLocationName() {
		return parentName;
	}

	@Override
	public List<FacetCategory> getFacetCategories() {
		final FacetCategory c = SearchHelper.getTagFacetCategory();
		// Checking non empty category
		if (!facetInfo.getFacetCounts(c).isEmpty()) {
			return Arrays.asList(c);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<FacetCount> getFacets(FacetCategory facetCategory) {
		return commonSearchSupport.getFacets(facetCategory);
	}

	@Override
	public String getFacetCategoryTitle(FacetCategory facetCategory) {
		return commonSearchSupport.getFacetCategoryTitle(facetCategory);
	}

	@Override
	public int getResultsCount() {
		return pagination.getItemCount();
	}

	@Override
	public String getResultTitle(CalmObject searchResult) {
		return ((User) searchResult).getPseudo();
	}

	@Override
	public String getResultThumbnailUrl(CalmObject searchResult) {
		final String thumbUrl = commonSearchSupport
				.getResultThumbnailUrl(searchResult);
		// Adding user default thumb
		if (thumbUrl == null) {
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
		final String desc = commonSearchSupport
				.getResultDescription(searchResult);
		if (desc == null || "".equals(desc.trim())) {
			final String status = ((User) searchResult).getStatusMessage();
			if (status != null && status.length() > 100) {
				return status.substring(0, 100) + "...";
			} else {
				return status;
			}
		} else {
			return desc;
		}
	}

	@Override
	public List<? extends CalmObject> getSearchResults() {
		return results;
	}

	@Override
	public String getResultUrl(CalmObject searchResult) {
		return urlService.getUserOverviewUrl("mainContent", searchResult);
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
		final FacetCategory c = SearchHelper.getFacetCategory("age"
				.equals(category) ? "birthyear" : category);
		final List<FacetCount> counts = facetInfo.getFacetCounts(c);
		if (counts != null && counts.size() == 1) {
			final FacetCount count = counts.iterator().next();
			if (count.getFacet() instanceof FacetRange) {
				if (!"age".equals(category)) {
					return (FacetRange) count.getFacet();
				} else {
					// The range is expressed by birth year, we need to convert
					// it to age
					FacetRange birthRange = (FacetRange) count.getFacet();
					return convertYearToAge(birthRange);
				}
			}
		}
		return null;
	}

	@Override
	public FacetRange getCurrentFacetRange(String category) {
		final FacetCategory c = SearchHelper.getFacetCategory("age"
				.equals(category) ? "birthyear" : category);
		final Collection<Facet> facets = facetInfo.getFacetFilters();
		for (Facet f : facets) {
			if (f.getFacetCategory().equals(c) && f instanceof FacetRange) {
				if (!"age".equals(category)) {
					return (FacetRange) f;
				} else {
					return convertYearToAge((FacetRange) f);
				}
			}
		}
		return getFacetRange(category);
	}

	private FacetRange convertYearToAge(FacetRange birthRange) {
		FacetRangeImpl ageRange = new FacetRangeImpl();
		final FacetCategory c = SearchHelper.getFacetCategory("age");
		ageRange.setFacetCategory(c);
		// Current year
		final Calendar calendar = Calendar.getInstance();
		final int currentYear = calendar.get(Calendar.YEAR);
		// Computing ages
		ageRange.setHigherBound(currentYear - birthRange.getLowerBound());
		ageRange.setLowerBound(currentYear - birthRange.getHigherBound());
		return ageRange;
	}

	@Override
	public String getOverlayText(CalmObject result) {
		final Integer age = UserHelper.getAge((User) result);
		if (age != null) {
			return String.valueOf(age);
		} else {
			return "";
		}
	}

	@Override
	public String getFacetRangeUrl(String category, String minPlaceHolder,
			String maxPlaceholder) {
		final FacetCategory c = SearchHelper.getFacetCategory(category);
		final FacetRange facetRange = FacetFactory.createPlaceHodlerFacetRange(
				c, minPlaceHolder, maxPlaceholder);
		return urlService.buildUserSearchUrl("mainContent", parentItem,
				facetInfo, facetRange);
	}

	@Override
	public String getResultLocalizationName(CalmObject result) {
		return commonSearchSupport.getResultLocalizationName(result);
	}

	@Override
	public String getResultLocalizationUrl(CalmObject result) {
		try {
			final City city = result.getUnique(City.class);
			if (city != null) {
				return urlService.buildSearchUrl("mainContent", city,
						SearchType.MEN, null, 0);
			}
		} catch (CalException e) {
			LOGGER.error("Unable to extract city search URL from user search result "
					+ result.getKey() + " : " + e.getMessage());
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
		final User user = (User) searchResult;
		final boolean isOnline = UserHelper.isOnline(user);
		return messageSource.getMessage("user.icon."
				+ (isOnline ? "online" : "offline") + ".16", null, locale);
	}

	public void setTranslationFacetTitle(String translationFacetTitle) {
		this.translationFacetTitle = translationFacetTitle;
	}

	@Override
	public String getResultThumbnailStyle(CalmObject searchResult) {
		return commonSearchSupport.getResultThumbnailStyle(searchResult);
	}

	public void setHideTitleWhenNoResult(boolean hideTitleWhenNoResult) {
		this.hideTitleWhenNoResult = hideTitleWhenNoResult;
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
