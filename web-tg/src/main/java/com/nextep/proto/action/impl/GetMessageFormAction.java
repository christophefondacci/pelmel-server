package com.nextep.proto.action.impl;

import java.util.Arrays;

import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentObjectAware;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

/**
 * This action provides the form for media addition.
 * 
 * @author cfondacci
 * 
 */
public class GetMessageFormAction extends AbstractAction implements
		CurrentObjectAware, CurrentUserAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -764572939072088279L;

	private static final String APIS_ALIAS_DESTINATION_ELT = "to";
	private CurrentUserSupport currentUserSupport;
	private String to;
	private ItemKey key;

	@Override
	protected String doExecute() throws Exception {
		key = CalmFactory.parseKey(to);
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false))
				.addCriterion(
						SearchRestriction.uniqueKeys(Arrays.asList(key))
								.aliasedBy(APIS_ALIAS_DESTINATION_ELT));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		currentUserSupport.initialize(getUrlService(), currentUser);
		return SUCCESS;
	}

	@Override
	protected int getLoginRequiredErrorCode() {
		return 200;
	}

	@Override
	public ItemKey getCurrentObjectKey() {
		return key;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}
}
