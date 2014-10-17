package com.nextep.proto.action.impl;

import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.HeaderAware;
import com.nextep.proto.action.model.LocalizationAware;
import com.nextep.proto.action.model.PaymentAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.LocalizationSupport;
import com.nextep.proto.blocks.PaymentSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;

public class PromoteAction extends AbstractAction implements HeaderAware,
		LocalizationAware, PaymentAware, CurrentUserAware {

	// Static constants declaration
	private static final long serialVersionUID = -2020420814477478126L;

	// Injected services
	private HeaderSupport headerSupport;
	private LocalizationSupport localizationSupport;
	private PaymentSupport paymentSupport;
	private CurrentUserSupport currentUserSupport;

	@Override
	protected String doExecute() throws Exception {

		// Building request (only qureying current user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), false));

		// Executing request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting current user
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		try {
			checkCurrentUser(currentUser);
		} catch (UserLoginTimeoutException e) {
			// We accept non logged users to access the promotion page so we
			// skip
		}
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Initializing supports for header and localization
		headerSupport.initialize(getLocale(), null, null, SearchType.BARS);
		localizationSupport.initialize(SearchType.BARS, getUrlService(),
				getLocale(), null, null);

		// Initializing payment
		paymentSupport.initialize(getLocale(), getUrlService(), currentUser);

		return SUCCESS;
	}

	@Override
	public void setHeaderSupport(HeaderSupport headerSupport) {
		this.headerSupport = headerSupport;
	}

	@Override
	public HeaderSupport getHeaderSupport() {
		return headerSupport;
	}

	@Override
	public void setLocalizationSupport(LocalizationSupport localizationSupport) {
		this.localizationSupport = localizationSupport;
	}

	@Override
	public LocalizationSupport getLocalizationSupport() {
		return localizationSupport;
	}

	@Override
	public PaymentSupport getPaymentSupport() {
		return paymentSupport;
	}

	@Override
	public void setPaymentSupport(PaymentSupport paymentSupport) {
		this.paymentSupport = paymentSupport;
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
