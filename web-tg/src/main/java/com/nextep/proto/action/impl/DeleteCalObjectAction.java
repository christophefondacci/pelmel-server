package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

/**
 * Deletes a CAL object. Currently supports Place and Activity. Place are
 * removed by setting its ONLINE state to false and removing it from SOLR index.
 * Activities are removed from index. (ADMIN action only)
 * 
 * @author cfondacci
 * 
 */
public class DeleteCalObjectAction extends AbstractAction implements
		JsonProviderWithError {

	private static final long serialVersionUID = 6190406774419036820L;
	private static final String APIS_ALIAS_OBJECT = "obj";
	private static final String REDIRECT_ACTION_PLACE_OVERVIEW = "placeOverview";

	// Injected dependencies
	private CurrentUserSupport currentUserSupport;
	private SearchPersistenceService searchPersistenceService;
	private CalPersistenceService placesService;
	private CalPersistenceService activitiesService;

	// Dynamic arguments
	private String key;
	private boolean confirmed;

	// Internal variables
	private String redirectAction;
	private String redirectId;
	private String messageTextKey;
	private String buttonTextKey;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(key);
		Assert.notNull(itemKey, "No item key");

		// Retrieving place
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_OBJECT)
								.with(Activity.class));
		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting place
		final CalmObject item = response.getUniqueElement(CalmObject.class,
				APIS_ALIAS_OBJECT);
		Assert.notNull(item, "Object not found");

		// Getting user
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		if (getRightsManagementService().canDelete(user, item)) {
			if (!confirmed) {
				if (item instanceof Place) {
					final Place place = (Place) item;

					messageTextKey = place.isOnline() ? "object.deletion.text"
							: "activities.deletion.text";
					buttonTextKey = place.isOnline() ? "object.deletion.button"
							: "activities.deletion.button";
				}
				return CONFIRM;
			} else {
				ContextHolder.toggleWrite();

				if (item instanceof MutablePlace) {
					final MutablePlace place = (MutablePlace) item;
					if (place.isOnline()) {
						// Toggling place to offline and persisting
						place.setOnline(false);
						placesService.saveItem(item);

						// Removing from index
						searchPersistenceService.remove(item);
					} else {
						// Unindexing activities
						final List<? extends Activity> activities = place
								.get(Activity.class);
						for (Activity activity : activities) {
							// Removing from index
							searchPersistenceService.remove(activity);
							// Setting a date in the past so that it does not
							// pop in latest changes
							((MutableActivity) activity).setDate(new Date(
									100000000));
							activitiesService.saveItem(activity);
						}
					}
				}

				redirectAction = REDIRECT_ACTION_PLACE_OVERVIEW;
				redirectId = key;

				// Done
				return SUCCESS;
			}
		} else {
			if (item == null) {
				setErrorMessage(getMessageSource().getMessage(
						"object.deletion.notFound", null, getLocale()));
			} else {
				setErrorMessage(getMessageSource().getMessage(
						"object.deletion.notAllowed", null, getLocale()));
			}
			return FORBIDDEN;
		}
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setPlacesService(CalPersistenceService placeService) {
		this.placesService = placeService;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setSearchPersistenceService(
			SearchPersistenceService searchPersistenceService) {
		this.searchPersistenceService = searchPersistenceService;
	}

	public void setConfirmed(boolean confirm) {
		this.confirmed = confirm;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	@Override
	public String getJson() {
		JsonStatus json = new JsonStatus();
		return JSONObject.fromObject(json).toString();
	}

	@Override
	public String getJsonError() {
		JsonStatus jsonError = new JsonStatus();
		jsonError.setError(true);
		jsonError.setMessage(getErrorMessage());
		return JSONObject.fromObject(jsonError).toString();
	}

	public String getMessageText() {
		return getText(messageTextKey);
	}

	public String getButtonText() {
		return getText(buttonTextKey);
	}

	public String getRedirectAction() {
		return redirectAction;
	}

	public String getRedirectId() {
		return redirectId;
	}
}
