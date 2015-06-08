package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;

import net.sf.json.JSONObject;

import com.nextep.activities.model.ActivityType;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonCheckinResponse;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisUserLocationItemKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.services.LocalizationService;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileCheckInAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = -7836539504850573998L;

	private static final String APIS_ALIAS_CHECKIN_PLACE = "cp";
	private static final String APIS_ALIAS_USER_PREVIOUSCHECKEDIN = "pucheckin";
	private static final String APIS_ALIAS_USER_CHECKEDIN = "ucheckin";
	private static final String APIS_ALIAS_USER_LASTPLACE = "lastplace";

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
	private GeographicItem checkedOutPlace;
	private GeographicItem checkedInPlace;
	private ApiCompositeResponse response;

	@Override
	protected String doExecute() throws Exception {
		// Parsing key
		final ItemKey itemKey = CalmFactory.parseKey(checkInKey);
		status = new JsonStatus();
		status.setError(true);
		// Building request for current user and checkin object
		// For user, we go find the last location object and compute the number
		// of people currently checked in there now, for checkin place we
		// recompute the number of checkins now
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) currentUserSupport
								.createApisCriterionFor(getNxtpUserToken(),
										true)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														new ApisUserLocationItemKeyAdapter())
												.aliasedBy(
														APIS_ALIAS_USER_LASTPLACE)
												.addCriterion(
														buildCheckinCountCriterion(APIS_ALIAS_USER_PREVIOUSCHECKEDIN))))

				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_CHECKIN_PLACE)
								.addCriterion(
										buildCheckinCountCriterion(APIS_ALIAS_USER_CHECKEDIN)));

		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Getting user and checking credentials
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Getting checkin place
		checkedInPlace = response.getUniqueElement(GeographicItem.class,
				APIS_ALIAS_CHECKIN_PLACE);
		checkedOutPlace = user.getUnique(GeographicItem.class,
				APIS_ALIAS_USER_LASTPLACE);

		// Checking in
		if (!checkout) {
			localizationService.checkin((MutableUser) user, checkedInPlace,
					ActivityType.CHECKIN, lat, lng);
		} else {
			localizationService.checkout((MutableUser) user, checkedInPlace,
					ActivityType.CHECKOUT, lat, lng);
		}
		// We're done
		status.setError(false);
		return SUCCESS;
	}

	/**
	 * Builds the APIS criterion appending the search for users checked in at
	 * the parent location
	 * 
	 * @return the {@link ApisCriterion} to append to the parent place criterion
	 * @throws ApisException
	 */
	private ApisCriterion buildCheckinCountCriterion(String alias)
			throws ApisException {
		return (ApisCriterion) SearchRestriction
				.withContained(User.class, SearchScope.NEARBY_BLOCK, 1, 0)
				.aliasedBy(alias).with(Media.class, MediaRequestTypes.THUMB);
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
		final JsonCheckinResponse jsonResponse = new JsonCheckinResponse();
		if (checkedOutPlace != null) {
			jsonResponse.setPreviousPlaceKey(checkedOutPlace.getKey()
					.toString());
			final PaginationInfo paginationInfo = response
					.getPaginationInfo(APIS_ALIAS_USER_PREVIOUSCHECKEDIN);
			jsonResponse.setPreviousPlaceUsersCount(paginationInfo
					.getItemCount() - 1);
		}
		if (checkedInPlace != null) {
			jsonResponse.setNewPlaceKey(checkedInPlace.getKey().toString());
			final PaginationInfo paginationInfo = response
					.getPaginationInfo(APIS_ALIAS_USER_CHECKEDIN);
			jsonResponse.setNewPlaceUsersCount(paginationInfo.getItemCount()
					+ (checkout ? -1 : 1));
		}
		return JSONObject.fromObject(jsonResponse).toString();
	}

	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	public boolean isCheckout() {
		return checkout;
	}
}
