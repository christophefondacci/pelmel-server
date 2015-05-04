package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;

import net.sf.json.JSONObject;

import com.nextep.activities.model.ActivityType;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.services.LocalizationService;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class MobileCheckInAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = -7836539504850573998L;

	private static final String APIS_ALIAS_CHECKIN_PLACE = "cp";

	// Injected services & support
	private CurrentUserSupport currentUserSupport;
	private LocalizationService localizationService;

	// Dynamic action arguments
	private String checkInKey;
	private double lat;
	private double lng;
	private boolean checkout;

	// Internal
	private JsonStatus status;

	@Override
	protected String doExecute() throws Exception {
		// Parsing key
		final ItemKey itemKey = CalmFactory.parseKey(checkInKey);
		status = new JsonStatus();
		status.setError(true);
		// Building request for current user and checkin object
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						SearchRestriction.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_CHECKIN_PLACE));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting user and checking credentials
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting checkin place
		final GeographicItem checkinPlace = response.getUniqueElement(
				GeographicItem.class, APIS_ALIAS_CHECKIN_PLACE);

		// Checking in
		if (!checkout) {
			localizationService.checkin((MutableUser) user, checkinPlace,
					ActivityType.CHECKIN, lat, lng);
		} else {
			localizationService.checkout((MutableUser) user, checkinPlace,
					ActivityType.CHECKOUT, lat, lng);
		}
		// We're done
		status.setError(false);
		return SUCCESS;
	}

	public void setCheckInKey(String checkInKey) {
		this.checkInKey = checkInKey;
	}

	public String getCheckInKey() {
		return checkInKey;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	@Override
	public String getJson() {
		return JSONObject.fromObject(status).toString();
	}

	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	public boolean isCheckout() {
		return checkout;
	}
}
