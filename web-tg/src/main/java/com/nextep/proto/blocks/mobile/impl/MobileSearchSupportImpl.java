package com.nextep.proto.blocks.mobile.impl;

import java.util.List;
import java.util.Locale;

import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.nextep.tags.model.Tag;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.model.FacetCount;

public class MobileSearchSupportImpl implements SearchSupport {

	private SearchSupport baseSearchSupport;
	private String baseUrl;

	@Override
	public List<Integer> getPagesList() {
		return baseSearchSupport.getPagesList();
	}

	@Override
	public Integer getCurrentPage() {
		return baseSearchSupport.getCurrentPage();
	}

	@Override
	public String getPageUrl(int page) {
		return baseSearchSupport.getPageUrl(page);
	}

	@Override
	public void initialize(SearchType searchType, UrlService urlService,
			Locale locale, CalmObject geoItem, String locationName,
			FacetInformation facetInfo, PaginationInfo pagination,
			List<? extends CalmObject> results) {
		baseSearchSupport.initialize(searchType, urlService, locale, geoItem,
				locationName, facetInfo, pagination, results);
	}

	@Override
	public FacetInformation getFacetInformation() {
		return baseSearchSupport.getFacetInformation();
	}

	@Override
	public String getSearchTitle() {
		return baseSearchSupport.getSearchTitle();
	}

	@Override
	public String getFacetsBoxTitle() {
		return baseSearchSupport.getFacetsBoxTitle();
	}

	@Override
	public String getSearchLocationName() {
		return baseSearchSupport.getSearchLocationName();
	}

	@Override
	public List<FacetCategory> getFacetCategories() {
		return baseSearchSupport.getFacetCategories();
	}

	@Override
	public String getFacetCategoryTitle(FacetCategory facetCategory) {
		return baseSearchSupport.getFacetCategoryTitle(facetCategory);
	}

	@Override
	public List<FacetCount> getFacets(FacetCategory facetCategory) {
		return baseSearchSupport.getFacets(facetCategory);
	}

	@Override
	public FacetRange getFacetRange(String category) {
		return baseSearchSupport.getFacetRange(category);
	}

	@Override
	public FacetRange getCurrentFacetRange(String category) {
		return baseSearchSupport.getCurrentFacetRange(category);
	}

	@Override
	public int getResultsCount() {
		return baseSearchSupport.getResultsCount();
	}

	@Override
	public List<? extends CalmObject> getSearchResults() {
		return baseSearchSupport.getSearchResults();
	}

	@Override
	public String getResultTitle(CalmObject searchResult) {
		return baseSearchSupport.getResultTitle(searchResult);
	}

	@Override
	public String getResultTitleIconUrl(CalmObject searchResult) {
		return baseUrl + baseSearchSupport.getResultTitleIconUrl(searchResult);
	}

	@Override
	public String getResultThumbnailUrl(CalmObject searchResult) {
		return baseUrl + baseSearchSupport.getResultThumbnailUrl(searchResult);
	}

	@Override
	public String getResultMiniThumbUrl(CalmObject searchResult) {
		return baseUrl + baseSearchSupport.getResultMiniThumbUrl(searchResult);
	}

	@Override
	public String getResultDescription(CalmObject searchResult) {
		return baseSearchSupport.getResultDescription(searchResult);
	}

	@Override
	public String getResultUrl(CalmObject searchResult) {
		return baseSearchSupport.getResultUrl(searchResult);
	}

	@Override
	public List<? extends Tag> getTags(CalmObject searchResult) {
		return baseSearchSupport.getTags(searchResult);
	}

	@Override
	public String getFacetTranslation(String facetCode) {
		return baseSearchSupport.getFacetTranslation(facetCode);
	}

	@Override
	public String getFacetIconUrl(String itemKey) {
		return baseUrl + baseSearchSupport.getFacetIconUrl(itemKey);
	}

	@Override
	public String getFacetAddedUrl(Facet facet) {
		return baseSearchSupport.getFacetAddedUrl(facet);
	}

	@Override
	public String getFacetRemovedUrl(Facet facet) {
		return baseSearchSupport.getFacetRemovedUrl(facet);
	}

	@Override
	public boolean isSelected(Facet facet) {
		return baseSearchSupport.isSelected(facet);
	}

	@Override
	public boolean isTagSelected(Tag tag) {
		return baseSearchSupport.isTagSelected(tag);
	}

	@Override
	public String getTagUrl(Tag tag) {
		return baseUrl + baseSearchSupport.getTagUrl(tag);
	}

	@Override
	public String getRemoveTagUrl(Tag tag) {
		return baseSearchSupport.getRemoveTagUrl(tag);
	}

	@Override
	public String getOverlayText(CalmObject result) {
		return baseSearchSupport.getOverlayText(result);
	}

	@Override
	public String getFacetRangeUrl(String category, String minPlaceHolder,
			String maxPlaceholder) {
		return baseSearchSupport.getFacetRangeUrl(category, minPlaceHolder,
				maxPlaceholder);
	}

	@Override
	public String getResultLocalizationName(CalmObject result) {
		return baseSearchSupport.getResultLocalizationName(result);
	}

	@Override
	public String getResultLocalizationUrl(CalmObject result) {
		return baseSearchSupport.getResultLocalizationUrl(result);
	}

	@Override
	public String getCustomText(CalmObject result, String key) {
		return baseSearchSupport.getCustomText(result, key);
	}

	public void setBaseSearchSupport(SearchSupport baseSearchSupport) {
		this.baseSearchSupport = baseSearchSupport;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public String getResultThumbnailStyle(CalmObject searchResult) {
		return baseSearchSupport.getResultThumbnailStyle(searchResult);
	}

	@Override
	public String getSearchDescription() {
		return baseSearchSupport.getSearchDescription();
	}

	@Override
	public String getResultCategoryLinkLabel(CalmObject result) {
		return baseSearchSupport.getResultCategoryLinkLabel(result);
	}
}