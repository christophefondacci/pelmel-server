package com.nextep.proto.action.impl;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.descriptions.model.Description;
import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.DescriptionsEditionAware;
import com.nextep.proto.action.model.EventEditionAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.DescriptionsEditionSupport;
import com.nextep.proto.blocks.EventEditionSupport;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class GetEventFormAction extends AbstractAction implements
		EventEditionAware, DescriptionsEditionAware, CurrentUserAware {

	private static final Log LOGGER = LogFactory
			.getLog(GetEventFormAction.class);
	private static final long serialVersionUID = -6947000149621794819L;
	private static final String APIS_ALIAS_EVENT = "event";
	private static final ApisItemKeyAdapter eventLocationAdapter = new ApisEventLocationAdapter();

	private CurrentUserSupport currentUserSupport;
	private EventEditionSupport eventEditionSupport;
	private DescriptionsEditionSupport descriptionsEditionSupport;
	private String id;
	private String calendarType;

	@Override
	public EventEditionSupport getEventEditionSupport() {
		return eventEditionSupport;
	}

	@Override
	public void setEventEditionSupport(EventEditionSupport eventEditionSupport) {
		this.eventEditionSupport = eventEditionSupport;
	}

	@Override
	protected String doExecute() throws Exception {
		if (id != null) {
			final ItemKey itemKey = CalmFactory.parseKey(id);
			CalendarType formCalendarType = null;
			if (calendarType != null) {
				try {
					formCalendarType = CalendarType.valueOf(calendarType);
				} catch (IllegalArgumentException e) {
					LOGGER.error("Invalid calendar type : " + calendarType);
				}
			}
			final ApisRequest request = ApisFactory.createCompositeRequest();
			final ApisCriterion eventCriterion = SearchRestriction.uniqueKeys(
					Arrays.asList(itemKey)).aliasedBy(APIS_ALIAS_EVENT);
			request.addCriterion(eventCriterion).addCriterion(
					currentUserSupport.createApisCriterionFor(
							getNxtpUserToken(), true));
			// Fetching event location if event ID specified
			// if (Event.CAL_ID.equals(itemKey.getType())) {
			eventCriterion.addCriterion(
					SearchRestriction.adaptKey(eventLocationAdapter)).with(
					Description.class);
			// }
			final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
					.execute(request, ContextFactory.createContext(getLocale()));

			// Fetching event and initializing header
			final CalmObject calObject = response.getUniqueElement(
					CalmObject.class, APIS_ALIAS_EVENT);
			getHeaderSupport().initialize(getLocale(), calObject, null,
					SearchType.EVENTS);

			// Checking is user is logged in as we would display a login page
			// instead of the form
			final User currentUser = response.getUniqueElement(User.class,
					CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
			checkCurrentUser(currentUser);

			// Standard support initialization
			eventEditionSupport.initialize(calObject, getUrlService(),
					getLocale());
			if (formCalendarType != null) {
				eventEditionSupport.setCalendarType(formCalendarType);
			}
			descriptionsEditionSupport.initialize(getLocale(),
					calObject instanceof Event ? calObject : null,
					getUrlService());
		} else {
			eventEditionSupport.initialize(null, getUrlService(), getLocale());
			descriptionsEditionSupport.initialize(getLocale(), null,
					getUrlService());
		}
		return SUCCESS;
	}

	@Override
	protected int getLoginRequiredErrorCode() {
		return 200;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public void setDescriptionsEditionSupport(
			DescriptionsEditionSupport descriptionEditionSupport) {
		this.descriptionsEditionSupport = descriptionEditionSupport;
	}

	@Override
	public DescriptionsEditionSupport getDescriptionsEditionSupport() {
		return descriptionsEditionSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	public String getCalendarType() {
		return calendarType;
	}
}
