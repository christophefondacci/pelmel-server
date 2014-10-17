package com.nextep.proto.action.impl.sitemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nextep.events.model.Event;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.SitemapEntry;
import com.nextep.proto.model.SitemapEntryProvider;
import com.nextep.proto.services.SitemapService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.impl.FacetImpl;

/**
 * This class is the root entry point for XML sitemap generation, providing
 * access to the hierarchy of sitemap indexes
 * 
 * @author cfondacci
 * 
 */
public class XMLSitemapRootIndexAction extends AbstractAction implements
		SitemapEntryProvider {

	private static final long serialVersionUID = 6938062482144781901L;

	private static final String APIS_ALIAS_PLACES = "places";
	private static final String APIS_ALIAS_EVENTS = "events";

	private SitemapService sitemapService;
	private List<SitemapEntry> entries;
	private int sitemapPageSize;
	private int minCityPopulation;

	@Override
	protected String doExecute() throws Exception {
		// Facetting by place type
		final List<FacetCategory> facetCategories = Arrays.asList(SearchHelper
				.getPlaceTypeCategory());
		final FacetCategory cityCategory = SearchHelper.getCityFacetCategory();
		// Preparing APIS request to get number of places and events available
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						SearchRestriction
								.searchAll(Place.class, SearchScope.CHILDREN,
										1, 0).facettedBy(facetCategories)
								.aliasedBy(APIS_ALIAS_PLACES))
				.addCriterion(
						SearchRestriction.searchAll(Event.class,
								SearchScope.LISTVIEW, 1, 0).aliasedBy(
								APIS_ALIAS_EVENTS))
				// We facet places by their city
				.addCriterion(
						SearchRestriction.withContained(Place.class,
								SearchScope.CITY, 10, 0).facettedBy(
								Arrays.asList(cityCategory)));

		// SearchRestriction
		// .list(GeographicItem.class,
		// new RequestTypeMinPopulation(
		// minCityPopulation))
		// .paginatedBy(1, 0).aliasedBy(APIS_ALIAS_CITY));
		;

		// Processing APIS request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting counts
		final PaginationInfo placesPagination = response
				.getPaginationInfo(APIS_ALIAS_PLACES);
		final PaginationInfo eventsPagination = response
				.getPaginationInfo(APIS_ALIAS_EVENTS);
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.CHILDREN);
		final int placesCount = placesPagination != null ? placesPagination
				.getItemCount() : 0;
		final int eventsCount = eventsPagination != null ? eventsPagination
				.getItemCount() : 0;

		// Building sitemap entries
		entries = new ArrayList<SitemapEntry>();
		final String homepageUrl = getUrlService().getHomepageUrl(getLocale());

		// Homepage entry
		// SitemapEntry entry = sitemapService.buildEntry(
		// Constants.PAGE_TYPE_HOMEPAGE, null, null, homepageUrl);
		// entries.add(entry);
		// final PaginationInfo cityPagination = response
		// .getPaginationInfo(APIS_ALIAS_CITY);

		// We extract facets (which corresponds to our cities since we facetted
		// places by their city) to determine how many cities we have
		final FacetInformation cityFacetInfo = response
				.getFacetInformation(SearchScope.CITY);
		final int citiesCount = cityFacetInfo.getFacetCounts(
				SearchHelper.getCityFacetCategory()).size();
		int pages = citiesCount / sitemapPageSize;
		if (citiesCount % sitemapPageSize > 0) {
			pages++;
		}
		final List<SearchType> sTypes = Arrays.asList(SearchType.ASSOCIATIONS,
				SearchType.BARS, SearchType.CLUBS, SearchType.RESTAURANTS,
				SearchType.SAUNAS, SearchType.SEXCLUBS, SearchType.SHOPS);
		for (int i = 0; i < pages; i++) {
			for (SearchType stype : sTypes) {
				final String url = getUrlService().getXMLSitemapUrl(
						getLocale(), Constants.PAGE_TYPE_SEARCH, stype, i);
				final SitemapEntry entry = sitemapService.buildEntry(
						Constants.PAGE_TYPE_SEARCH, stype, null, url);
				entries.add(entry);
			}
		}

		// Place overview entries
		// int placesPages = placesCount / sitemapPageSize;
		// if (placesCount % sitemapPageSize > 0) {
		// placesPages++;
		// }
		for (SearchType searchType : sTypes) {
			// Building a facet from the place type
			final String placeType = searchType.getSubtype();
			final FacetImpl f = new FacetImpl();
			f.setFacetCode(placeType);
			f.setFacetCategory(SearchHelper.getPlaceTypeCategory());

			// Getting count with this facet from facetting info
			final int typeCount = facetInfo.getFacetCount(f);
			int typePages = typeCount / sitemapPageSize;
			if (typeCount % sitemapPageSize > 0) {
				typePages++;
			}

			// Now building every page link
			for (int i = 0; i < typePages; i++) {
				final String sitemapUrl = getUrlService().getXMLSitemapUrl(
						getLocale(), Constants.PAGE_TYPE_OVERVIEW, searchType,
						i);
				final SitemapEntry entry = sitemapService.buildEntry(
						Constants.PAGE_TYPE_OVERVIEW, searchType, null,
						sitemapUrl);
				entries.add(entry);
			}
		}

		// Event overview entries
		int eventPages = eventsCount / sitemapPageSize;
		if (eventsCount % sitemapPageSize > 0) {
			eventPages++;
		}
		for (int i = 0; i < eventPages; i++) {
			final String sitemapUrl = getUrlService().getXMLSitemapUrl(
					getLocale(), Constants.PAGE_TYPE_OVERVIEW,
					SearchType.EVENTS, i);
			final SitemapEntry entry = sitemapService.buildEntry(
					Constants.PAGE_TYPE_OVERVIEW, SearchType.EVENTS, null,
					sitemapUrl);
			entries.add(entry);
		}

		return SUCCESS;
	}

	@Override
	public List<SitemapEntry> getSitemapEntries() {
		return entries;
	}

	public void setSitemapService(SitemapService sitemapService) {
		this.sitemapService = sitemapService;
	}

	public void setSitemapPageSize(int sitemapPageSize) {
		this.sitemapPageSize = sitemapPageSize;
	}

	public void setMinCityPopulation(int minCityPopulation) {
		this.minCityPopulation = minCityPopulation;
	}
}
