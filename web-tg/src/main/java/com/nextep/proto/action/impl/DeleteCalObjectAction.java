package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalExtendedPersistenceService;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.events.model.MutableEvent;
import com.nextep.events.model.MutableEventSeries;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonStatus;
import com.nextep.messages.model.MutableMessage;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
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
	private static final String TRANSLATION_OBJECT_TYPE_PREFIX = "activity.objType.";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	// Injected dependencies
	private CurrentUserSupport currentUserSupport;
	private SearchPersistenceService searchPersistenceService;
	private CalPersistenceService placesService;
	private CalPersistenceService activitiesService;
	@Autowired
	@Qualifier("messagesService")
	private CalPersistenceService messageService;
	@Autowired
	@Qualifier("eventSeriesService")
	private CalExtendedPersistenceService eventSeriesService;
	@Autowired
	@Qualifier("eventsService")
	private CalExtendedPersistenceService eventsService;
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private EventManagementService eventManagementService;
	@Autowired
	@Qualifier("globalMessages")
	private MessageSource messageSource;
	@Resource(mappedName = "systemMessageUserKey")
	private String systemUserKey;

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
								.with(Activity.class)
								.addCriterion(
										SearchRestriction
												.adaptKey(eventLocationAdapter)));
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
				} else {
					messageTextKey = "object.deletion.text";
					buttonTextKey = "object.deletion.button";
				}
				return CONFIRM;
			} else {
				ContextHolder.toggleWrite();
				// For sending message
				ItemKey itemAuthorKey = null;
				String itemName = DisplayHelper.getName(item);
				String itemType = getItemTypeLabel(item);
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
							((MutableActivity) activity).setVisible(false);
							activitiesService.saveItem(activity);
						}
					}
					redirectAction = REDIRECT_ACTION_PLACE_OVERVIEW;
					redirectId = key;
				} else if (item instanceof EventSeries) {
					redirectAction = REDIRECT_ACTION_PLACE_OVERVIEW;
					redirectId = ((EventSeries) item).getLocationKey()
							.toString();

					// Removing by switching online flag
					final MutableEventSeries series = (MutableEventSeries) item;
					if (series.isOnline()) {
						series.setOnline(false);
						eventSeriesService.saveItem(series);
					} else if (getRightsManagementService().isAdministrator(
							user)) {
						eventSeriesService.delete(series.getKey());
					}

					// Unindexing from Search
					searchPersistenceService.remove(item);

					// Sending notification
					final Place place = item.getUnique(Place.class);
					notificationService.sendEventDeletedNotification(series,
							place, user);
					itemAuthorKey = series.getAuthorKey();
					itemName = DisplayHelper.getName(series);
					if (itemName == null || itemName.trim().isEmpty()) {
						String timeframe = eventManagementService
								.buildReadableTimeframe(series, getLocale());
						String seriesTypeLabel = messageSource.getMessage(
								"calendarType."
										+ series.getCalendarType().name(),
								null, getLocale());
						itemName = seriesTypeLabel + " " + timeframe;
					}
				} else if (item instanceof Event) {
					redirectAction = REDIRECT_ACTION_PLACE_OVERVIEW;
					redirectId = ((Event) item).getLocationKey().toString();

					// Removing by switching online flag
					final MutableEvent event = (MutableEvent) item;
					event.setOnline(false);
					eventsService.saveItem(event);

					// Unindexing from Search
					searchPersistenceService.remove(item);

					// Sending notification
					final Place place = item.getUnique(Place.class);
					notificationService.sendEventDeletedNotification(event,
							place, user);
					itemAuthorKey = event.getAuthorKey();
					itemName = DisplayHelper.getName(event);
				}

				// Sending in-app message
				sendMessage(user.getKey(), user, itemName, itemType, user);
				if (itemAuthorKey != null
						&& !user.getKey().equals(itemAuthorKey)) {
					sendMessage(itemAuthorKey, user, itemName, itemType, user);
				}
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

	private String getItemTypeLabel(CalmObject object) {
		final String targetType = object.getKey().getType();
		String targetTypeLabel = messageSource.getMessage(
				TRANSLATION_OBJECT_TYPE_PREFIX + targetType, null, getLocale());
		return targetTypeLabel;
	}

	private void sendMessage(ItemKey recipient, CalmObject sender,
			String itemName, String itemType, User deletionAuthor)
			throws CalException {
		ContextHolder.toggleWrite();
		final MutableMessage m = (MutableMessage) messageService
				.createTransientObject();

		// Computing sender of message (avoiding to send message from ourselves
		ItemKey senderKey = sender.getKey();
		if (deletionAuthor.getKey().equals(senderKey)) {
			senderKey = CalmFactory.parseKey(systemUserKey);
		}
		m.setFromKey(senderKey);
		m.setToKey(recipient);
		m.setMessageDate(new Date());
		final String message = messageSource.getMessage(
				"deleteCalObject.message", new String[] { itemType, itemName,
						deletionAuthor.getPseudo() }, getLocale());
		m.setMessage(message);
		messageService.saveItem(m);
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
