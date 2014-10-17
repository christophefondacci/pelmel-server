package com.nextep.proto.action.impl;

import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;

/**
 * This action disconnects the currently logged user
 * 
 * @author cfondacci
 * 
 */
public class DisconnectAction extends AbstractAction {

	private static final long serialVersionUID = -4344237640885614980L;

	private CurrentUserSupport currentUserSupport;
	private SearchPersistenceService searchService;

	@Override
	protected String doExecute() throws Exception {
		// Preparing APIS request that fetches our current user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));

		// Executing and retrieving current user
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);

		// Updating it with void token
		ContextHolder.toggleWrite();
		((UsersService) getUsersService()).refreshUserOnlineTimeout(
				currentUser, null);
		// Updating user in search results
		searchService.updateUserOnlineStatus(currentUser);
		return SUCCESS;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}
}
