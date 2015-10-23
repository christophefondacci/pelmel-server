package com.nextep.proto.action.mobile.impl;

import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.media.model.Media;
import com.nextep.media.model.MutableMedia;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisMediaParentKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONObject;

/**
 * This action is dedicated to reports from mobile devices
 * 
 * @author cfondacci
 */
public class MobileReportingAction extends AbstractAction implements JsonProvider {

	// Logger
	private static final Log LOGGER = LogFactory.getLog(MobileReportingAction.class);

	// Serial ID
	private static final long serialVersionUID = -6184880687085681826L;

	// Static constants
	private static final String APIS_ALIAS_ITEM = "item";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	// Injected supports & services
	private CurrentUserSupport currentUserSupport;
	private CalPersistenceService mediaService;
	private CalPersistenceService placeService;
	private CalPersistenceService activitiesService;
	private NotificationService notificationService;
	@Autowired
	private EventManagementService eventManagementService;

	// Dynamic arguments
	private String key;
	private int type;

	// Internal
	private boolean success;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(key);
		final ApisCriterion itemCriterion = buildApisCriterionFor(itemKey);
		// Building a request which retrieves the user and the element
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true))
				.addCriterion(itemCriterion);

		// Querying
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Getting user
		final User currentUser = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		// Getting element
		final CalmObject item = response.getUniqueElement(CalmObject.class, APIS_ALIAS_ITEM);

		String redirectAction = REDIRECT;
		ActivityType reportActivity = null;
		switch (type) {
		case Constants.REPORT_TYPE_ABUSE:
		case Constants.REPORT_TYPE_NOTGAY:
		case Constants.REPORT_TYPE_LOCATION:
		case Constants.REPORT_TYPE_CLOSED:
		case Constants.REPORT_TYPE_REMOVAL_REQUESTED:
			ContextHolder.toggleWrite();

			LOGGER.info(
					"MOBILE_REPORT: User " + currentUser.getKey() + " reported code " + type + " on '" + itemKey + "'");
			reportActivity = ActivityType.REMOVAL_REQUESTED;
			// Registering activity
			final MutableActivity activity = (MutableActivity) activitiesService.createTransientObject();

			activity.setActivityType(reportActivity);
			activity.setDate(new Date());
			activity.setLoggedItemKey(itemKey);
			activity.setUserKey(currentUser.getKey());

			if (Place.CAL_TYPE.equals(item.getKey().getType())) {
				// Getting a mutable place
				final MutablePlace place = (MutablePlace) item;
				// Getting current number of closed reported
				final int closedCount = place.getClosedCount();
				// Adding 1
				place.setClosedCount(closedCount + 1);
				// Storing back to storage
				placeService.saveItem(place);

				// Setting up activity
				activity.add(place.getCity());
			} else if (Event.CAL_ID.equals(item.getKey().getType())
					|| EventSeries.SERIES_CAL_ID.equals(item.getKey().getType())) {
				// Extracting event city to localize activity
				final Event event = (Event) item;
				final City city = eventManagementService.getEventCity(event);
				activity.add(city);
			} else if (Media.CAL_TYPE.equals(item.getKey().getType())) {
				// Getting a mutable media
				final MutableMedia media = (MutableMedia) item;
				// Getting current number of abuses
				final int abuseCount = media.getAbuseCount();
				// Adding 1
				media.setAbuseCount(abuseCount + 1);
				// Storing back to storage
				ContextHolder.toggleWrite();
				mediaService.saveItem(media);
				// Logging
				LOGGER.info(
						"MOBILE_REPORT: User " + currentUser.getKey() + " reported abuse on media " + media.getKey());
				reportActivity = ActivityType.ABUSE;
				redirectAction = "redirectMedia";
			}
			activitiesService.saveItem(activity);
			try {
				notificationService.sendReportEmailNotification(item, currentUser, type);
			} catch (Exception e) {
				LOGGER.error("Problems sending email notification: " + e.getMessage(), e);
			}
			// Now if current user is admin we close it
			if (getRightsManagementService().canDelete(currentUser, item)) {
				return redirectAction;
			}
		}

		success = true;
		return SUCCESS;
	}

	private ApisCriterion buildApisCriterionFor(ItemKey itemKey) throws CalException, ApisException {
		ApisCriterion crit = SearchRestriction.uniqueKeys(Arrays.asList(itemKey)).aliasedBy(APIS_ALIAS_ITEM);
		if (Event.CAL_ID.equals(itemKey.getType()) || EventSeries.SERIES_CAL_ID.equals(itemKey.getType())) {
			crit.addCriterion(SearchRestriction.adaptKey(eventLocationAdapter));
		} else if (Media.CAL_TYPE.equals(itemKey.getType())) {
			crit.addCriterion(
					SearchRestriction.adaptKey(new ApisMediaParentKeyAdapter()).aliasedBy(Constants.APIS_ALIAS_PARENT));
		}
		return crit;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public String getJson() {
		final JsonStatus status = new JsonStatus();
		status.setError(!success);
		status.setMessage(getErrorMessage());
		return JSONObject.fromObject(status).toString();
	}

	public void setMediaService(CalPersistenceService mediaService) {
		this.mediaService = mediaService;
	}

	public void setPlaceService(CalPersistenceService placeService) {
		this.placeService = placeService;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

}
