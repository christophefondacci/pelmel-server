package com.nextep.proto.action.impl;

import java.util.Arrays;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.services.RightsManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

/**
 * Deletes an event
 * 
 * @author cfondacci
 *
 */
public class EventDeleteAction extends AbstractAction implements
		JsonProviderWithError {

	// Constants
	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_EVENT = "event";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	// Services
	@Autowired
	@Qualifier("eventSeriesService")
	private CalExtendedPersistenceService eventSeriesService;
	@Autowired
	@Qualifier("eventsService")
	private CalExtendedPersistenceService eventService;
	@Autowired
	private RightsManagementService rightsManagementService;
	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private SearchPersistenceService searchPersistenceService;

	private String eventKey;

	@Override
	protected String doExecute() throws Exception {

		// Building event key
		final ItemKey itemKey = CalmFactory.parseKey(eventKey);

		// Creating request to get user
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_EVENT)
								.addCriterion(
										SearchRestriction
												.adaptKey(eventLocationAdapter)));

		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Getting user
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		// Getting event
		final Event event = response.getUniqueElement(Event.class,
				APIS_ALIAS_EVENT);

		// Getting place for notification
		final Place place = event.getUnique(Place.class);

		ContextHolder.toggleWrite();
		if (EventSeries.SERIES_CAL_ID.equals(itemKey.getType())) {
			eventSeriesService.delete(itemKey);
			searchPersistenceService.remove(event);
			notificationService.sendEventDeletedNotification(event, place,
					currentUser);
		} else if (Event.CAL_ID.equals(itemKey.getType())) {
			if (rightsManagementService.canDelete(currentUser, event)) {
				eventService.delete(itemKey);
				searchPersistenceService.remove(event);
				notificationService.sendEventDeletedNotification(event, place,
						currentUser);
			}
		}
		//
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonStatus status = new JsonStatus();
		status.setError(false);
		return JSONObject.fromObject(status).toString();
	}

	@Override
	public String getJsonError() {
		return null;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getEventKey() {
		return eventKey;
	}
}
