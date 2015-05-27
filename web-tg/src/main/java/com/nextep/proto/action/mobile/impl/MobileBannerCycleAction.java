package com.nextep.proto.action.mobile.impl;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.json.model.impl.JsonBanner;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.BannerDisplayService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class MobileBannerCycleAction extends AbstractAction implements
		JsonProvider {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private BannerDisplayService bannerDisplayService;
	@Autowired
	private JsonBuilder jsonBuilder;

	// Dynamic arguments
	private String currentBannerKey;
	private double lat;
	private double lng;

	// Internal variables
	private AdvertisingBanner banner;

	@Override
	protected String doExecute() throws Exception {

		// Parsing banner item key
		final ItemKey currentBannerItemKey = currentBannerKey == null ? null
				: CalmFactory.parseKey(currentBannerKey);

		// Querying user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true));
		// And adding banner request
		bannerDisplayService.addBannersRequest(request, lat, lng);

		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Security check
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Selecting the banner
		banner = bannerDisplayService.getBannerSelection(response,
				currentBannerItemKey);
		if (banner != null) {
			bannerDisplayService.displayBanner(banner);
		}
		return SUCCESS;
	}

	@Override
	public String getJson() {
		if (banner != null) {
			final JsonBanner jsonBanner = jsonBuilder.buildJsonBanner(banner,
					true, getLocale());
			return JSONObject.fromObject(jsonBanner).toString();
		} else {
			return "{}";
		}

	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLat() {
		return lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLng() {
		return lng;
	}

	public String getCurrentBannerKey() {
		return currentBannerKey;
	}

	public void setCurrentBannerKey(String currentBannerKey) {
		this.currentBannerKey = currentBannerKey;
	}
}
