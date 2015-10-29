package com.nextep.proto.action.impl.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.RequestTypeListPlaces;
import com.nextep.geo.model.impl.RequestTypeListPlaces.FilterField;
import com.nextep.geo.model.impl.RequestTypeListPlaces.SortField;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.impl.AdminPaginationSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SuggestScope;

public class AdminPlacesAction extends AbstractAction implements MediaAware, CurrentUserAware {

	private static final long serialVersionUID = 5199841368119304685L;

	private static final String APIS_ALIAS_PLACES = "p";
	private static final String URL = "/admin/places";

	// Injected supports
	@Autowired
	@Qualifier("mediaProvider")
	private MediaProvider mediaProvider;
	@Autowired
	@Qualifier("placeSearchSupport")
	private SearchSupport searchSupport;
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private RightsManagementService rightsManagementService;
	@Autowired
	@Qualifier("adminPaginationSupport")
	private AdminPaginationSupport paginationSupport;

	// Dynamic arguments
	private String sortField = "UDATE";
	private String sortDirection = "DESC";
	private String filterOnline;
	private String filterIndexed;
	private String parentKey;
	private String searchTerm;
	private int page = 0;
	private int pageSize = 30;

	// Internal
	private List<? extends Place> places;

	@Override
	protected String doExecute() throws Exception {
		// setLoginRedirectUrl(URL);
		SortField requestSortField = null;
		try {
			requestSortField = SortField.valueOf(sortField);
		} catch (IllegalArgumentException e) {

		}
		boolean sortAsc = "ASC".equalsIgnoreCase(sortDirection);
		Collection<FilterField> filters = new ArrayList<FilterField>();
		if (filterOnline != null && !filterOnline.isEmpty()) {
			filters.add(Boolean.valueOf(filterOnline) ? FilterField.ONLINE : FilterField.OFFLINE);
		}

		if (filterIndexed != null && !filterIndexed.isEmpty()) {
			filters.add(Boolean.valueOf(filterIndexed) ? FilterField.INDEXED : FilterField.UNINDEXED);
		}

		final RequestTypeListPlaces requestType = new RequestTypeListPlaces(pageSize, page, requestSortField, sortAsc,
				filters);

		final ApisRequest request = ApisFactory.createCompositeRequest();
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			request.addCriterion((ApisCriterion) SearchRestriction.list(Place.class, requestType)
					.aliasedBy(APIS_ALIAS_PLACES).with(Media.class));
		} else {
			request.addCriterion((ApisCriterion) SearchRestriction
					.searchFromText(GeographicItem.class, Arrays.asList(SuggestScope.GEO_FULLTEXT, SuggestScope.PLACE),
							searchTerm.trim())
					.paginatedBy(pageSize, page).aliasedBy(APIS_ALIAS_PLACES).with(Media.class));
		}
		request.addCriterion(currentUserSupport.createApisCriterionFor(getNxtpUserToken(), false));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);
		currentUserSupport.initialize(getUrlService(), user);
		if (!rightsManagementService.isAdministrator(user)) {
			return unauthorized();
		}

		places = response.getElements(Place.class, APIS_ALIAS_PLACES);
		searchSupport.initialize(SearchType.BARS, getUrlService(), getLocale(), null, null, null, null, places);
		final PaginationInfo paginationInfo = response.getPaginationInfo(APIS_ALIAS_PLACES);

		// Preparing args map
		Map<String, String> args = new HashMap<String, String>();
		args.put("filterIndexed", filterIndexed);
		args.put("filterOnline", filterOnline);
		args.put("sortDirection", sortDirection);
		args.put("sortField", sortField);
		args.put("parentKey", parentKey);
		args.put("searchTerm", searchTerm);
		paginationSupport.initialize(paginationInfo, getHeaderSupport().getCanonical(), args);
		return SUCCESS;
	}

	public String getHtmlName(Place place) {
		String name = place.getName();
		if (searchTerm != null && !searchTerm.trim().isEmpty()) {
			int index = place.getName().toLowerCase().indexOf(searchTerm.toLowerCase());
			if (index >= 0) {
				name = name.substring(0, index) + "<strong>" + name.substring(index, index + searchTerm.length())
						+ "</strong>" + name.substring(index + searchTerm.length());
			}
		}
		return name;
	}

	public String getSearchUrl(Place place) {
		final SearchType searchType = SearchType.fromPlaceType(place.getPlaceType());
		return getUrlService().buildSearchUrl(DisplayHelper.getDefaultAjaxContainer(), place.getCity(), searchType);
	}

	public List<? extends Place> getPlaces() {
		return places;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public String getFilterOnline() {
		return filterOnline;
	}

	public void setFilterOnline(String filterOnline) {
		this.filterOnline = filterOnline;
	}

	public String getFilterIndexed() {
		return filterIndexed;
	}

	public void setFilterIndexed(String filterIndexed) {
		this.filterIndexed = filterIndexed;
	}

	public String getParentKey() {
		return parentKey;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	@Override
	public MediaProvider getMediaProvider() {
		return mediaProvider;
	}

	@Override
	public void setMediaProvider(MediaProvider mediaProvider) {
		this.mediaProvider = mediaProvider;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public SearchSupport getSearchSupport() {
		return searchSupport;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public AdminPaginationSupport getPaginationSupport() {
		return paginationSupport;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getSearchTerm() {
		return searchTerm;
	}
}
