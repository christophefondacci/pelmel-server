package com.nextep.proto.action.impl;

import java.util.Arrays;

import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentObjectAware;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.helpers.DisplayHelper;
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

/**
 * This action provides the form for media addition.
 * 
 * @author cfondacci
 * 
 */
public class GetMediaFormAction extends AbstractAction implements
		CurrentObjectAware, CurrentUserAware {

	// Constants declaration
	private static final long serialVersionUID = -764572939072088279L;
	private static final String APIS_ALIAS_OBJECT = "obj";

	// Injected supports & services
	private CurrentUserSupport currentUserSupport;
	private RightsManagementService rightsManagementService;

	// Dynamic arguments
	private String id;
	private ItemKey key;
	private String redirectUrl;

	@Override
	protected String doExecute() throws Exception {
		key = CalmFactory.parseKey(id);
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(key))
								.aliasedBy(APIS_ALIAS_OBJECT)
								.with(AdvertisingBooster.class));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));
		final CalmObject object = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_OBJECT);
		getHeaderSupport().initialize(getLocale(), object, null, null);
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		// Checking if user can modify element
		if (!rightsManagementService.canModify(currentUser, object)) {
			return FORBIDDEN;
		}

		if (redirectUrl == null) {
			if (object.getKey().equals(currentUser.getKey())) {
				setRedirectUrl("/myProfile");
			} else {
				setRedirectUrl(getUrlService().getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), object));
			}
		}
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

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
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
}
