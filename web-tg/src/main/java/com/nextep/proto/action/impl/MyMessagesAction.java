package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.json.model.impl.JsonRecipientsGroup;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.MessageRecipientsGroup;
import com.nextep.messages.model.MessageRequestTypeAfterId;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.apis.adapters.ApisMessageFromUserAdapter;
import com.nextep.proto.apis.adapters.ApisMessageRecipientsGroupItemKeyAdapter;
import com.nextep.proto.apis.adapters.ApisMessageToUserAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.LocalizationService;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

import net.sf.json.JSONObject;

public class MyMessagesAction extends AbstractAction implements MessagingAware, CurrentUserAware, JsonProvider {

	private static final long serialVersionUID = -6729501217111087845L;
	private static final Log LOGGER = LogFactory.getLog(MyMessagesAction.class);

	private static final String APIS_ALIAS_PAGE_MESSAGES = "pmsg";
	private static final String APIS_ALIAS_NEARBY_PLACES = "nearbyPlaces";
	private static final String APIS_ALIAS_GROUP_USERS = "rcptUsers";
	private static final String APIS_ALIAS_RCPT_GROUP = "rcptGroup";

	private static final String PAGE_STYLE_MSG = "my-messages";
	private static final ApisItemKeyAdapter APIS_MESSAGE_FROM_USER_ADAPTER = new ApisMessageFromUserAdapter();
	private static final ApisItemKeyAdapter APIS_MESSAGE_TO_USER_ADAPTER = new ApisMessageToUserAdapter();

	private CurrentUserSupport currentUserSupport;

	// Messaging support for the active instant messaging box in the left nav
	// bar
	private MessagingSupport instantMessagingSupport;
	// Messaging support for the page : listing all messages
	private MessagingSupport myMessagingSupport;
	private LocalizationService localizationService;
	private JsonBuilder jsonBuilder;
	private Double lat, lng;

	private int messagesPerPage;
	private int page;
	private boolean highRes;
	private double radius;
	private Long fromMessageId;

	private PaginationInfo msgPagination;

	@Override
	protected String doExecute() throws Exception {
		RequestType requestType = null;
		if (fromMessageId != null) {
			requestType = new MessageRequestTypeAfterId(CalmFactory.createKey(Message.CAL_TYPE, fromMessageId));
		}
		final ApisCriterion userCriterion = (ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false)
				.with((WithCriterion) SearchRestriction.with(Message.class, requestType)
						.paginatedBy(messagesPerPage, page).aliasedBy(APIS_ALIAS_PAGE_MESSAGES).with(Media.class)
						.addCriterion((ApisCriterion) SearchRestriction.adaptKey(APIS_MESSAGE_FROM_USER_ADAPTER)
								.aliasedBy(Constants.ALIAS_FROM).with(Media.class, MediaRequestTypes.THUMB))
						.addCriterion((ApisCriterion) SearchRestriction.adaptKey(APIS_MESSAGE_TO_USER_ADAPTER)
								.aliasedBy(Constants.ALIAS_TO).with(Media.class, MediaRequestTypes.THUMB)));
		// If localization is provided, we fetch the nearest place
		if (lat != null && lng != null) {
			userCriterion.addCriterion(
					SearchRestriction.searchNear(Place.class, SearchScope.NEARBY_BLOCK, lat, lng, radius, 5, 0)
							.aliasedBy(APIS_ALIAS_NEARBY_PLACES));
		}
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest().addCriterion(userCriterion);

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		getHeaderSupport().initialize(getLocale(), user, null, null);
		checkCurrentUser(user);
		currentUserSupport.initialize(getUrlService(), user);

		// Localizing user if information is provided
		if (lat != null && lng != null) {
			// Getting the nearest places from lat / lng
			final List<? extends Place> places = user.get(Place.class, APIS_ALIAS_NEARBY_PLACES);

			// Localizing user
			localizationService.localize(user, places, response, lat, lng);
		}

		// Initializing instant messaging
		final List<? extends Message> instantMessages = user.get(Message.class);
		final PaginationInfo instantMsgPagination = response.getPaginationInfo(Message.class);
		instantMessagingSupport.initialize(getUrlService(), getLocale(), instantMessages, instantMsgPagination,
				user.getKey(), PAGE_STYLE_MSG);

		// Initilizing message page
		final List<? extends Message> messages = user.get(Message.class, APIS_ALIAS_PAGE_MESSAGES);
		msgPagination = response.getPaginationInfo(APIS_ALIAS_PAGE_MESSAGES);
		myMessagingSupport.initialize(getUrlService(), getLocale(), messages, msgPagination, user.getKey(),
				PAGE_STYLE_MSG);

		return SUCCESS;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.instantMessagingSupport = messagingSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return instantMessagingSupport;
	}

	public MessagingSupport getMyMessagingSupport() {
		return myMessagingSupport;
	}

	public void setMyMessagingSupport(MessagingSupport myMessagingSupport) {
		this.myMessagingSupport = myMessagingSupport;
	}

	public void setMessagesPerPage(int messagesPerPage) {
		this.messagesPerPage = messagesPerPage;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public int getMessagesPerPage() {
		return messagesPerPage;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
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

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	@Override
	public String getJson() {
		// Getting the list of messages to convert to JSON
		List<? extends Message> messages = myMessagingSupport.getMessages();
		if (messages == null) {
			messages = Collections.emptyList();
		}
		Collections.reverse(messages);
		// Building the JSON wrapper
		final JsonManyToOneMessageList messagesList = jsonBuilder.buildJsonManyToOneMessages(messages, highRes,
				getLocale(), currentUserSupport.getCurrentUser());
		int unreadCount = instantMessagingSupport.getMessages() == null ? 0
				: instantMessagingSupport.getMessages().size();
		messagesList.setUnreadMsgCount(unreadCount);
		messagesList.setPage(page);
		messagesList.setPageSize(messagesPerPage);
		messagesList.setTotalMsgCount(msgPagination.getItemCount());

		// Building recipients group definition
		final Set<ItemKey> recipientsGroupKeys = new HashSet<ItemKey>();
		for (Message m : messages) {
			if (m.getRecipientsGroupKey() != null) {
				recipientsGroupKeys.add(m.getRecipientsGroupKey());
			}
		}
		// Querying all groups (non optimized, should not be too many groups to
		// query for now
		final ApisMessageRecipientsGroupItemKeyAdapter groupUsersAdapter = new ApisMessageRecipientsGroupItemKeyAdapter();
		for (ItemKey groupKey : recipientsGroupKeys) {
			try {
				// Querying this recipients group
				ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
						.addCriterion((ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(groupKey))
								.aliasedBy(APIS_ALIAS_RCPT_GROUP)
								.addCriterion((ApisCriterion) SearchRestriction
										.customAdapt(groupUsersAdapter, APIS_ALIAS_GROUP_USERS)
										.with(Media.class, MediaRequestTypes.THUMB)));
				// Executing
				final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
						ContextFactory.createContext(getLocale()));

				// Getting recipient group
				final MessageRecipientsGroup group = response.getUniqueElement(MessageRecipientsGroup.class,
						APIS_ALIAS_RCPT_GROUP);
				final List<? extends User> groupUsers = response.getElements(User.class, APIS_ALIAS_GROUP_USERS);
				group.addAll(groupUsers);

				// Converting to JSON
				final JsonRecipientsGroup jsonGroup = jsonBuilder.buildJsonRecipientsGroup(group, highRes, getLocale());

				// Adding to our response
				messagesList.addRecipientsGroup(jsonGroup);
			} catch (ApisException e) {
				LOGGER.error("Unable to extract recipients group definition for '" + groupKey + "': " + e.getMessage(),
						e);
			}

		}

		// Sending JSON serialized string
		return JSONObject.fromObject(messagesList).toString();
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setFromMessageId(Long fromMessageId) {
		this.fromMessageId = fromMessageId;
	}

	public Long getFromMessageId() {
		return fromMessageId;
	}
}
