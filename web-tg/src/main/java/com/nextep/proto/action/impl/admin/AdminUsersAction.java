package com.nextep.proto.action.impl.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.MediaAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MediaProvider;
import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.blocks.impl.AdminPaginationSupport;
import com.nextep.proto.blocks.impl.HeaderAdminSupportImpl;
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

public class AdminUsersAction extends AbstractAction implements MediaAware,
		CurrentUserAware {

	private static final long serialVersionUID = 5199841368119304685L;

	private static final String APIS_ALIAS_USERS = "u";
	private static final String URL = "/admin/users";

	// Injected supports
	@Autowired
	@Qualifier("mediaProvider")
	private MediaProvider mediaProvider;
	@Autowired
	@Qualifier("userSearchSupport")
	private SearchSupport searchSupport;
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private RightsManagementService rightsManagementService;
	@Autowired
	@Qualifier("adminPaginationSupport")
	private AdminPaginationSupport paginationSupport;

	// Dynamic arguments
	private int page = 0;
	private int pageSize = 30;

	// Internal
	private List<? extends User> users;

	@Override
	protected String doExecute() throws Exception {

		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.list(User.class, null)
								.paginatedBy(pageSize, page)
								.aliasedBy(APIS_ALIAS_USERS).with(Media.class)
								.with(GeographicItem.class))
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);
		currentUserSupport.initialize(getUrlService(), user);
		if (!rightsManagementService.isAdministrator(user)) {
			return unauthorized();
		}

		users = response.getElements(User.class, APIS_ALIAS_USERS);
		searchSupport.initialize(SearchType.MEN, getUrlService(), getLocale(),
				null, null, null, null, users);
		final PaginationInfo paginationInfo = response
				.getPaginationInfo(APIS_ALIAS_USERS);

		// Preparing args map
		Map<String, String> args = new HashMap<String, String>();

		paginationSupport.initialize(paginationInfo, getHeaderSupport()
				.getCanonical(), args);
		((HeaderAdminSupportImpl) getHeaderSupport()).setCanonical(URL);
		return SUCCESS;
	}

	public String getSearchUrl(Place place) {
		final SearchType searchType = SearchType.fromPlaceType(place
				.getPlaceType());
		return getUrlService().buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), place.getCity(),
				searchType);
	}

	public List<? extends User> getUsers() {
		return users;
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

}
