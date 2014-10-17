package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.common.model.impl.FacetImpl;

public class AjaxSearchPlaceAction extends AbstractAction implements
		SearchAware, LocalizationAware {

	// Constants
	private static final long serialVersionUID = -7514162123751575502L;
	private static final int PLACES_PER_PAGE = 16;

	// Injected Services
	private SearchSupport searchSupport;
	private LocalizationSupport localizationSupport;
	private SearchSupport placeSearchSupport;

	// Action dynamic parameters
	private String geoKey;
	private String facets;
	private int pageOffset = 0;
	private SearchType searchType;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey geoItemKey = CalmFactory.parseKey(geoKey);
		// Preparing facet categories
		final Collection<FacetCategory> userCategories = SearchHelper
				.buildUserFacetCategories();
		final Collection<FacetCategory> placeCategories = SearchHelper
				.buildPlaceFacetCategories(geoItemKey);

		// Building facet filters
		final List<Facet> facetFilters = buildFacetFilters(placeCategories
				.iterator().next());
		// Querying content
		ApisRequest request = (ApisRequest) ApisFactory
				.createRequest(GeographicItem.class)
				.alternateKey(geoItemKey.getType(), geoItemKey.getId())
				.with(SearchRestriction.withContained(User.class,
						SearchScope.CHILDREN, 0, 0).facettedBy(userCategories))
				.with((WithCriterion) SearchRestriction
						.withContained(Place.class, SearchScope.POI,
								PLACES_PER_PAGE, pageOffset)
						.facettedBy(placeCategories).filteredBy(facetFilters)
						.with(Tag.class).with(Media.class));

		ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		final GeographicItem geopoint = (GeographicItem) response
				.getUniqueElement();
		searchSupport.initialize(searchType, getUrlService(), getLocale(),
				geopoint, DisplayHelper.getName(geopoint),
				response.getFacetInformation(SearchScope.POI),
				response.getPaginationInfo(Place.class),
				geopoint.get(Place.class));
		localizationSupport.initialize(searchType, getUrlService(),
				getLocale(), geopoint,
				response.getFacetInformation(SearchScope.CHILDREN));
		// placesSupport.initialize(geopoint.get(Place.class));
		placeSearchSupport
				.initialize(searchType, getUrlService(), getLocale(), geopoint,
						DisplayHelper.getName(geopoint),
						response.getFacetInformation(SearchScope.POI),
						response.getPaginationInfo(Place.class),
						Collections.EMPTY_LIST);
		return SUCCESS;
	}

	private List<Facet> buildFacetFilters(FacetCategory c) {
		if (facets != null) {
			final List<Facet> facetFilters = new ArrayList<Facet>();
			final String[] facetList = facets.split(",");
			for (String facetCode : facetList) {
				final FacetImpl f = new FacetImpl();
				f.setFacetCategory(c);
				f.setFacetCode(facetCode);
				facetFilters.add(f);
			}
			return facetFilters;
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	public void setGeoKey(String geoKey) {
		this.geoKey = geoKey;
	}

	public String getGeoKey() {
		return geoKey;
	}

	public int getPageOffset() {
		return pageOffset;
	}

	public void setPageOffset(int pageOffset) {
		this.pageOffset = pageOffset;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	public void setFacets(String facets) {
		this.facets = facets;
	}

	public String getFacets() {
		return facets;
	}

	public void setPlaceSearchSupport(SearchSupport placeSearchSupport) {
		this.placeSearchSupport = placeSearchSupport;
	}

	public SearchSupport getPlaceSearchSupport() {
		return placeSearchSupport;
	}

	@Override
	public SearchType getSearchType() {
		return searchType;
	}

	@Override
	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}
}
