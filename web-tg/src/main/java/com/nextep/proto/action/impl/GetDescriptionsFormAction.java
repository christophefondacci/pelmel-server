package com.nextep.proto.action.impl;

import java.util.Arrays;

import com.nextep.descriptions.model.Description;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionsEditionAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.DescriptionsEditionSupport;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class GetDescriptionsFormAction extends AbstractAction implements
		DescriptionsEditionAware, CurrentUserAware {

	private static final long serialVersionUID = 4169053201276904748L;
	private static final String APIS_ALIAS_PARENT = "parent";
	private DescriptionsEditionSupport descriptionsEditionSupport;
	private RightsManagementService rightsManagementService;
	private CurrentUserSupport currentUserSupport;
	private String parentId;

	@Override
	protected String doExecute() throws Exception {
		// Initializing parent key
		final ItemKey parentKey = CalmFactory.parseKey(parentId);

		// Building request that fetches the parent with all its descriptions
		// For display in the description edition form
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(parentKey))
								.aliasedBy(APIS_ALIAS_PARENT)
								.with(Description.class))
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));

		// Executing request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Extracting parent
		final CalmObject parent = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_PARENT);

		getHeaderSupport().initialize(getLocale(), null, null, null);

		// Extracting user & checking
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Checking rights
		if (rightsManagementService.canModify(user, parent)) {
			// Initializing the support for description edition
			descriptionsEditionSupport.initialize(getLocale(), parent);

			// Everything is OK
			return SUCCESS;
		} else {
			return FORBIDDEN;
		}
	}

	@Override
	public void setDescriptionsEditionSupport(
			DescriptionsEditionSupport descEditionSupport) {
		this.descriptionsEditionSupport = descEditionSupport;
	}

	@Override
	public DescriptionsEditionSupport getDescriptionsEditionSupport() {
		return descriptionsEditionSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setRightsManagementService(
			RightsManagementService rightsManagementService) {
		this.rightsManagementService = rightsManagementService;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentId() {
		return parentId;
	}

}
