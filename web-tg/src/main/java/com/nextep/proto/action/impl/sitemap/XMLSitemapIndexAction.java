package com.nextep.proto.action.impl.sitemap;

import java.util.ArrayList;
import java.util.List;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.impl.RequestTypeMinPopulation;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.model.SitemapEntry;
import com.nextep.proto.model.SitemapEntryProvider;
import com.nextep.proto.services.SitemapService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;

public class XMLSitemapIndexAction extends AbstractAction implements
		SitemapEntryProvider {

	private static final long serialVersionUID = -6388380734942903047L;

	private static final String APIS_ALIAS_CITY = "city";
	private SitemapService sitemapService;
	private int minCityPopulation;
	private int sitemapPageSize;

	private String searchType;
	private String pageType;

	private List<SitemapEntry> entries;

	@Override
	protected String doExecute() throws Exception {
		final SearchType sType = SearchType.valueOf(searchType);

		// Building APIS request
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						SearchRestriction
								.list(GeographicItem.class,
										new RequestTypeMinPopulation(
												minCityPopulation))
								.paginatedBy(1, 0).aliasedBy(APIS_ALIAS_CITY));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final PaginationInfo cityPagination = response
				.getPaginationInfo(APIS_ALIAS_CITY);
		final int citiesCount = cityPagination.getItemCount();
		int pages = citiesCount / sitemapPageSize;
		if (citiesCount % sitemapPageSize > 0) {
			pages++;
		}
		entries = new ArrayList<SitemapEntry>();
		for (int i = 0; i < pages; i++) {
			final String url = getUrlService().getXMLSitemapUrl(getLocale(),
					pageType, sType, i);
			final SitemapEntry entry = sitemapService.buildEntry(pageType,
					sType, null, url);
			entries.add(entry);
		}
		return SUCCESS;
	}

	@Override
	public List<SitemapEntry> getSitemapEntries() {
		return entries;
	}

	public void setMinCityPopulation(int minCityPopulation) {
		this.minCityPopulation = minCityPopulation;
	}

	public void setSitemapPageSize(int sitemapPageSize) {
		this.sitemapPageSize = sitemapPageSize;
	}

	public void setSitemapService(SitemapService sitemapService) {
		this.sitemapService = sitemapService;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getPageType() {
		return pageType;
	}
}
