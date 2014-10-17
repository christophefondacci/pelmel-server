package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.SearchAware;
import com.nextep.proto.action.model.TagAware;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.TagSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileUserListAction extends AbstractAction implements
		SearchAware, TagAware {

	private static final long serialVersionUID = -8080601517487426813L;
	private static final String APIS_ALIAS_PARENT = "parent";
	private static final SearchType searchType = SearchType.MEN;
	// Injected constants
	private int pageSize;

	// Injected supports
	private SearchSupport searchSupport;
	private TagSupport tagSupport;

	// Dynamic arguments
	private String id;
	private String facets;
	private String type;
	private int page = 0;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(id);
		final List<Facet> facetFilters = SearchHelper.buildFacetFilters(
				SearchHelper.getTagFacetCategory(), facets);
		SearchScope scope = SearchScope.NEARBY_BLOCK;
		if ("like".equalsIgnoreCase(type)) {
			scope = SearchScope.CHILDREN;
		}
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_PARENT)
								.with((WithCriterion) SearchRestriction
										.withContained(User.class, scope,
												pageSize, page)
										.filteredBy(facetFilters)
										.with(Tag.class)
										.with(Media.class,
												MediaRequestTypes.THUMB)
										.with(Description.class,
												DescriptionRequestType.SINGLE_DESC)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		// Extracting root element
		final CalmObject parent = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PARENT);
		// Getting users
		final List<? extends User> users = parent.get(User.class);
		final FacetInformation facetInfo = response.getFacetInformation(scope);
		final PaginationInfo pagination = response
				.getPaginationInfo(User.class);

		// Initialization of various supports
		searchSupport.initialize(searchType, getUrlService(), getLocale(),
				parent, DisplayHelper.getName(parent), facetInfo, pagination,
				users);
		tagSupport.initialize(getLocale(), Collections.EMPTY_LIST);

		return SUCCESS;
	}

	@Override
	public void setSearchSupport(SearchSupport searchSupport) {
		this.searchSupport = searchSupport;
	}

	@Override
	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	@Override
	public SearchType getSearchType() {
		return searchType;
	}

	@Override
	public void setSearchType(SearchType searchType) {

	}

	@Override
	public void setTagSupport(TagSupport tagSupport) {
		this.tagSupport = tagSupport;
	}

	@Override
	public TagSupport getTagSupport() {
		return tagSupport;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setFacets(String facets) {
		this.facets = facets;
	}

	public String getFacets() {
		return facets;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

}
