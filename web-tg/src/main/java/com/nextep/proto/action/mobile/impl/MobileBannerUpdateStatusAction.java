package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.BannerStatus;
import com.nextep.advertising.model.MutableAdvertisingBanner;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonBanner;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileBannerUpdateStatusAction extends AbstractAction implements
		JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_BANNER = "banner";

	@Autowired
	private CurrentUserSupport currentUserSupport;

	@Autowired
	@Qualifier("bannersService")
	private CalPersistenceService bannersService;

	@Autowired
	private SearchPersistenceService searchPersistenceService;

	@Autowired
	private JsonBuilder jsonBuilder;

	// Dynamic arguments
	private String bannerKey;
	private String status;

	// Internal variable
	private AdvertisingBanner banner;

	@Override
	protected String doExecute() throws Exception {

		// Parsing banner key
		final ItemKey bannerItemKey = CalmFactory.parseKey(bannerKey);

		// Building request that fetches banner and user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						SearchRestriction.uniqueKeys(
								Arrays.asList(bannerItemKey)).aliasedBy(
								APIS_ALIAS_BANNER));

		// Executing request
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Extracting user
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Extracting banner
		banner = response.getUniqueElement(AdvertisingBanner.class,
				APIS_ALIAS_BANNER);

		// The only way to change a "pending payment" state is to pay
		if (banner.getStatus() == BannerStatus.PENDING_PAYMENT) {
			return ERROR;
		} else {
			ContextHolder.toggleWrite();
			try {
				((MutableAdvertisingBanner) banner).setStatus(BannerStatus
						.valueOf(status));
			} catch (IllegalArgumentException | NullPointerException e) {
				setErrorMessage("Invalid banner status '" + status + "'");
				return ERROR;
			}

			// Saving banner
			bannersService.saveItem(banner);
			searchPersistenceService.storeCalmObject(banner,
					SearchScope.CHILDREN);
		}
		// Done
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonBanner jsonBanner = jsonBuilder.buildJsonBanner(banner, true,
				getLocale());
		return JSONObject.fromObject(jsonBanner).toString();
	}

	public void setBannerKey(String bannerKey) {
		this.bannerKey = bannerKey;
	}

	public String getBannerKey() {
		return bannerKey;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
