package com.nextep.proto.action.impl;

import java.util.Arrays;

import com.nextep.advertising.model.Subscription;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.SponsorshipAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.SponsorshipSupport;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.SearchType;
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

public class SponsorshipAction extends AbstractAction implements
		CurrentUserAware, SponsorshipAware {

	private static final long serialVersionUID = 3720800489441280885L;
	private static final String APIS_ALIAS_OBJECT = "object";

	private CurrentUserSupport currentUserSupport;
	private SponsorshipSupport sponsorshipSupport;

	private String id;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(id);

		// Querying user and sponsored object
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_OBJECT)
								.with(Subscription.class));

		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Retrieving data & initializing supports
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		currentUserSupport.initialize(getUrlService(), user);

		// Header initialization
		final CalmObject sponsoredObject = response.getUniqueElement(
				CalmObject.class, APIS_ALIAS_OBJECT);
		final SearchType searchType = SearchHelper
				.getSearchType(sponsoredObject);
		getHeaderSupport().initialize(getLocale(), sponsoredObject, null,
				searchType);

		checkCurrentUser(user);

		// Initializing sponsorship
		sponsorshipSupport.initialize(getLocale(), getUrlService(),
				sponsoredObject, user);
		return SUCCESS;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public SponsorshipSupport getSponsorshipSupport() {
		return sponsorshipSupport;
	}

	@Override
	public void setSponsorshipSupport(SponsorshipSupport sponsorshipSupport) {
		this.sponsorshipSupport = sponsorshipSupport;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
