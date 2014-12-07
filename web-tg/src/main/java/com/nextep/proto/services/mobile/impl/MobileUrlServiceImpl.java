package com.nextep.proto.services.mobile.impl;

import java.util.Locale;

import com.nextep.events.model.CalendarType;
import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetRange;

public class MobileUrlServiceImpl implements UrlService {

	private UrlService baseUrlService;

	@Override
	public String getHomepageUrl(Locale locale) {
		return baseUrlService.getHomepageUrl(locale);
	}

	@Override
	public String getXMLSitemapIndexUrl(Locale locale, String pageType,
			SearchType searchType) {
		return baseUrlService.getXMLSitemapIndexUrl(locale, pageType,
				searchType);
	}

	@Override
	public String getXMLSitemapUrl(Locale locale, String pageType,
			SearchType searchType, int page) {
		return baseUrlService.getXMLSitemapUrl(locale, pageType, searchType,
				page);
	}

	public void setBaseUrlService(UrlService baseUrlService) {
		this.baseUrlService = baseUrlService;
	}

	@Override
	public String buildSearchUrl(String targetHtmlElementId, CalmObject geoKey,
			SearchType searchType, FacetInformation currentFacetting,
			Facet newFacet, Facet removedFacet) {
		return baseUrlService.buildSearchUrl(targetHtmlElementId, geoKey,
				searchType, currentFacetting, newFacet, removedFacet);
	}

	@Override
	public String buildSearchUrl(String targetHtmlElementId,
			GeographicItem geoKey, SearchType searchType) {
		return baseUrlService.buildSearchUrl(targetHtmlElementId, geoKey,
				searchType);
	}

	@Override
	public String getMapInfoWindowUrl(ItemKey geoKey) {
		return baseUrlService.getMapInfoWindowUrl(geoKey);
	}

	@Override
	public String buildUserSearchUrl(String targetHtmlElementId,
			CalmObject geoKey, FacetInformation currentFacetting,
			FacetRange range) {
		return baseUrlService.buildUserSearchUrl(targetHtmlElementId, geoKey,
				currentFacetting, range);
	}

	@Override
	public String buildSearchUrl(String targetHtmlElementId, CalmObject geoKey,
			SearchType searchType, FacetInformation currentFacetting, int page) {
		return baseUrlService.buildSearchUrl(targetHtmlElementId, geoKey,
				searchType, currentFacetting, page);
	}

	@Override
	public String getPlaceOverviewUrl(String targetHtmlElementId,
			CalmObject place) {
		return "overview.html?id=" + place.getKey().toString();
	}

	@Override
	public String getEventOverviewUrl(String targetHtmlElementId,
			CalmObject event) {
		return baseUrlService.getEventOverviewUrl(targetHtmlElementId, event);
	}

	@Override
	public String getUserOverviewUrl(String targetHtmlElementId, CalmObject user) {
		return "overview.html?id=" + user.getKey().toString();
	}

	@Override
	public String getILikeUrl(String targetHtmlElementId, ItemKey element) {
		return baseUrlService.getILikeUrl(targetHtmlElementId, element);
	}

	@Override
	public String getMediaAdditionFormUrl(String targetHtmlElementId,
			ItemKey element) {
		return baseUrlService.getMediaAdditionFormUrl(targetHtmlElementId,
				element);
	}

	@Override
	public String getWriteMessageDialogUrl(String targetHtmlElementId,
			ItemKey element) {
		return baseUrlService.getWriteMessageDialogUrl(targetHtmlElementId,
				element);
	}

	@Override
	public String getEventEditionFormUrl(String targetHtmlElementId,
			ItemKey element) {
		return baseUrlService.getEventEditionFormUrl(targetHtmlElementId,
				element);
	}

	@Override
	public String getEventEditionFormUrl(String targetHtmlElementId,
			ItemKey element, CalendarType calendarType) {
		return baseUrlService.getEventEditionFormUrl(targetHtmlElementId,
				element, calendarType);
	}

	@Override
	public String getPlaceEditionFormUrl(String targetHtmlElementId,
			ItemKey element, String placeType) {
		return baseUrlService.getPlaceEditionFormUrl(targetHtmlElementId,
				element, placeType);
	}

	@Override
	public String getOverviewUrl(String targetHtmlElementId, CalmObject item) {
		return baseUrlService.getOverviewUrl(targetHtmlElementId, item);
	}

	@Override
	public String getCommentUrl(String targetHtmlElementId, ItemKey parentKey,
			int page) {
		return baseUrlService.getCommentUrl(targetHtmlElementId, parentKey,
				page);
	}

	@Override
	public String getToggleUserTagUrl(String targetHtmlElementId,
			ItemKey taggedItemKey, ItemKey tag) {
		return baseUrlService.getToggleUserTagUrl(targetHtmlElementId,
				taggedItemKey, tag);
	}

	@Override
	public String getMyMessagesUrl(String targetHtmlElementId,
			CalmObject parent, int page) {
		return baseUrlService.getMyMessagesUrl(targetHtmlElementId, parent,
				page);
	}

	@Override
	public String buildSearchUrl(Locale language, String targetHtmlElementId,
			CalmObject geoItem, SearchType searchType,
			FacetInformation currentFacetting, int page) {
		return baseUrlService.buildSearchUrl(language, targetHtmlElementId,
				geoItem, searchType, currentFacetting, page);
	}

	@Override
	public String buildMapSearchUrl(Locale locale, String targetHtmlElementId,
			GeographicItem geoItem) {
		return baseUrlService.buildMapSearchUrl(locale, targetHtmlElementId,
				geoItem);
	}

	@Override
	public String getPlaceOverviewUrl(Locale locale,
			String targetHtmlElementId, CalmObject place) {
		return baseUrlService.getPlaceOverviewUrl(locale, targetHtmlElementId,
				place);
	}

	@Override
	public String getEventOverviewUrl(Locale locale,
			String targetHtmlElementId, CalmObject event) {
		return baseUrlService.getEventOverviewUrl(locale, targetHtmlElementId,
				event);
	}

	@Override
	public String getUserOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject user) {
		return baseUrlService.getUserOverviewUrl(locale, targetHtmlElementId,
				user);
	}

	@Override
	public String getOverviewUrl(Locale locale, String targetHtmlElementId,
			CalmObject item) {
		return baseUrlService.getOverviewUrl(locale, targetHtmlElementId, item);
	}

	@Override
	public String getActivitiesUrl(String targetHtmlElementId,
			CalmObject target, CalmObject user, GeographicItem geoItem,
			int page, String typeFilter) {
		return baseUrlService.getActivitiesUrl(targetHtmlElementId, target,
				user, geoItem, page, typeFilter);
	}

	@Override
	public String getMediaAdditionFormUrl(String targetHtmlElementId,
			ItemKey element, String redirectUrl) {
		return baseUrlService.getMediaAdditionFormUrl(targetHtmlElementId,
				element, redirectUrl);
	}

	@Override
	public String getPromoteUrl(Locale l) {
		return baseUrlService.getPromoteUrl(l);
	}

	@Override
	public String getSponsorshipUrl(CalmObject object) {
		return baseUrlService.getSponsorshipUrl(object);
	}

	@Override
	public String getSponsorshipActivationUrl() {
		return baseUrlService.getSponsorshipActivationUrl();
	}

	@Override
	public String getMediaUrl(String url) {
		return baseUrlService.getMediaUrl(url);
	}

	@Override
	public String getStaticUrl(String url) {
		return baseUrlService.getStaticUrl(url);
	}
}
