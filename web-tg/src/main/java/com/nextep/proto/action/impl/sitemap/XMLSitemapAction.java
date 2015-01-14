package com.nextep.proto.action.impl.sitemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.SitemapEntry;
import com.nextep.proto.model.SitemapEntryProvider;
import com.nextep.proto.services.SitemapService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class XMLSitemapAction extends AbstractAction implements
		SitemapEntryProvider {

	private static final long serialVersionUID = -4913734730599462359L;
	private static final Log LOGGER = LogFactory.getLog(XMLSitemapAction.class);
	private static final String APIS_ALIAS_CITIES = "cities";
	// Injected information
	private SitemapService sitemapService;
	private int minCityPopulation;
	private int sitemapPageSize;
	private double nearbyRadius;

	// Dynamic page arguments
	private String pageType;
	private String searchType;
	private int page;

	// Internal variables
	private List<SitemapEntry> entries;

	@Override
	protected String doExecute() throws Exception {
		entries = new ArrayList<SitemapEntry>();
		SearchType sType = SearchType.valueOf(searchType);

		// Building APIS request
		ApisRequest request = null;

		if (Constants.PAGE_TYPE_SEARCH.equals(pageType)) {
			final FacetCategory cityCategory = SearchHelper
					.getCityFacetCategory();
			SearchScope scope = SearchScope.CITY;
			switch (sType) {
			case BARS:
				scope = SearchScope.BAR;
				break;
			case ASSOCIATIONS:
				scope = SearchScope.ASSO;
				break;
			case CLUBS:
				scope = SearchScope.CLUB;
				break;
			case RESTAURANTS:
				scope = SearchScope.RESTAURANT;
				break;
			case SAUNAS:
				scope = SearchScope.SAUNA;
				break;
			case SEXCLUBS:
				scope = SearchScope.SEXCLUB;
				break;
			case SHOPS:
				scope = SearchScope.SEXSHOP;
				break;
			}
			final ApisCustomAdapter cityFacetAdapter = new ApisFacetToItemKeyAdapter(
					scope, SearchHelper.getCityFacetCategory(),
					sitemapPageSize, page * sitemapPageSize);
			request = (ApisRequest) ApisFactory
					.createCompositeRequest()
					.addCriterion(
							SearchRestriction.withContained(Place.class, scope,
									10, 0).facettedBy(
									Arrays.asList(cityCategory)))
					.addCriterion(
							SearchRestriction.customAdapt(cityFacetAdapter,
									APIS_ALIAS_CITIES));
		} else if (Constants.PAGE_TYPE_OVERVIEW.equals(pageType)) {
			Class<? extends CalmObject> calClass = null;
			List<Facet> facetFilters = Collections.emptyList();
			switch (sType) {
			case EVENTS:
				calClass = Event.class;
				break;
			default:
				facetFilters = SearchHelper.buildFacetFilters(
						SearchHelper.getTagFacetCategory(), null, sType);
				calClass = Place.class;
				break;
			}
			request = (ApisRequest) ApisFactory.createCompositeRequest()
					.addCriterion(
							SearchRestriction
									.searchAll(calClass, SearchScope.CHILDREN,
											sitemapPageSize, page)
									.filteredBy(facetFilters)
									.aliasedBy(APIS_ALIAS_CITIES));
		}
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final List<? extends CalmObject> elements = response.getElements(
				CalmObject.class, APIS_ALIAS_CITIES);
		for (CalmObject element : elements) {
			String url = null;
			if (Constants.PAGE_TYPE_SEARCH.equals(pageType)) {
				switch (sType) {
				case EVENTS:
					url = getUrlService().buildSearchUrl(
							DisplayHelper.getDefaultAjaxContainer(), element,
							SearchType.EVENTS, null, 0);
					break;
				case MAP:
					url = getUrlService().buildSearchUrl(
							DisplayHelper.getDefaultAjaxContainer(),
							(GeographicItem) element, SearchType.MAP);
					break;
				default:
					// for (String subtype : subtypes) {
					url = getUrlService().buildSearchUrl(
							DisplayHelper.getDefaultAjaxContainer(), element,
							sType, null, 0);
					url = LocalizationHelper.buildUrl(getLocale(),
							getDomainName(), url);
					final SitemapEntry entry = sitemapService.buildEntry(
							pageType, sType, element.getKey(), url);
					entries.add(entry);

					// Adding nearby cities
					// final ApisRequest nearbyRequest = (ApisRequest)
					// ApisFactory
					// .createRequest(GeographicItem.class)
					// .uniqueKey(element.getKey().getId())
					// .withNearest(GeographicItem.class,
					// SearchScope.CITY,
					// Constants.MAX_NEAREST_CITIES, 0,
					// nearbyRadius - 5);
					// final ApiResponse nearbyResponse =
					// getApiService().execute(
					// nearbyRequest,
					// ContextFactory.createContext(getLocale()));
					// final GeographicItem city = (GeographicItem)
					// nearbyResponse
					// .getUniqueElement();
					// final List<? extends GeographicItem> nearbyCities = city
					// .get(GeographicItem.class);
					// for (GeographicItem nearbyCity : nearbyCities) {
					// if (!nearbyCity.getKey().equals(element.getKey())) {
					// url = getUrlService().buildSearchUrl(
					// DisplayHelper.getDefaultAjaxContainer(),
					// nearbyCity, sType, null, 0);
					// url = LocalizationHelper.buildUrl(getLocale(),
					// getDomainName(), url);
					// final SitemapEntry nearbyEntry = sitemapService
					// .buildEntry(pageType, sType,
					// nearbyCity.getKey(), url, true);
					// SearchStatistic stat = nearbyResponse.getStatistic(
					// nearbyCity.getKey(),
					// SearchStatistic.DISTANCE);
					// LOGGER.info(((City) nearbyCity).getName() + " - "
					// + nearbyCity.getKey() + " @ "
					// + stat.getNumericValue());
					// entries.add(nearbyEntry);
					// }
					// }
					// }
					continue;
				}
			} else if (Constants.PAGE_TYPE_OVERVIEW.equals(pageType)) {
				url = getUrlService().getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), element);
			}
			url = LocalizationHelper
					.buildUrl(getLocale(), getDomainName(), url);
			final SitemapEntry entry = sitemapService.buildEntry(pageType,
					sType, element.getKey(), url);
			entries.add(entry);
		}
		return SUCCESS;
	}

	@Override
	public List<SitemapEntry> getSitemapEntries() {
		return entries;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setSitemapPageSize(int sitemapPageSize) {
		this.sitemapPageSize = sitemapPageSize;
	}

	public void setSitemapService(SitemapService sitemapService) {
		this.sitemapService = sitemapService;
	}

	public void setMinCityPopulation(int minCityPopulation) {
		this.minCityPopulation = minCityPopulation;
	}

	public void setNearbyRadius(double nearbyRadius) {
		this.nearbyRadius = nearbyRadius;
	}
}
