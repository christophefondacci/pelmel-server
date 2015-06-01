package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.impl.AdvertisingRequestTypes;
import com.nextep.json.model.impl.JsonBanner;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisBannerTargetAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.model.Constants;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.cals.factory.ContextFactory;

public class MobileBannerListAction extends AbstractAction implements
		JsonProvider {
	private static final long serialVersionUID = 1L;

	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private JsonBuilder jsonBuilder;

	private List<? extends AdvertisingBanner> banners;

	// Dynamic arguments

	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) currentUserSupport
								.createApisCriterionFor(getNxtpUserToken(),
										true)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.with(AdvertisingBanner.class,
														AdvertisingRequestTypes.USER_BANNERS)
												.addCriterion(
														SearchRestriction
																.adaptKey(
																		new ApisBannerTargetAdapter())
																.aliasedBy(
																		Constants.APIS_ALIAS_BANNER_TARGET))
												.with(Media.class)));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting user and checking credentials
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting banners
		banners = user.get(AdvertisingBanner.class);

		// All good
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final List<JsonBanner> jsonBanners = new ArrayList<JsonBanner>();
		for (AdvertisingBanner banner : banners) {
			final JsonBanner jsonBanner = jsonBuilder.buildJsonBanner(banner,
					true, getLocale());
			jsonBanners.add(jsonBanner);
		}
		return JSONArray.fromObject(jsonBanners).toString();
	}
}
