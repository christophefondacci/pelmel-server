package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonOneToOneMessageList;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.impl.MessageRequestTypeListConversation;
import com.nextep.messages.services.MessageService;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.apis.adapters.ApisMessageFromUserAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

public class MyMessageReplyAction extends AbstractAction implements
		CurrentUserAware, MessagingAware, JsonProvider {

	private static final Log LOGGER = LogFactory
			.getLog(MyMessageReplyAction.class);
	private static final long serialVersionUID = 7255915412851663750L;
	private static final String PAGE_STYLE_MSG = "my-messages";
	private static final int MESSAGES_PER_PAGE = 15;
	private static final String APIS_ALIAS_PAGE_MESSAGES = "pmsg";
	private static final String APIS_ALIAS_FROM_USER = "from";
	private static final String APIS_ALIAS_NEARBY_PLACES = "nearbyPlaces";
	private static final ApisItemKeyAdapter MSG_FROM_USER_ADAPTER = new ApisMessageFromUserAdapter();

	// Injected services
	private CurrentUserSupport currentUserSupport;
	private MessagingSupport messagingSupport;
	private MessagingSupport myMessagingSupport;
	private LocalizationService localizationService;
	private MessageService messageService;
	private JsonBuilder jsonBuilder;
	private double radius;

	// Dynamic variables
	private String from;
	private boolean highRes;
	private int page = 0;
	private Double lat, lng;

	// Internal variable
	private User currentUser;
	private User fromUser;
	private int readMsgCount = 0;
	private PaginationInfo myPaginationInfo;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey fromKey = CalmFactory.parseKey(from);
		// We need the user key
		ApisRequest request = ApisFactory.createCompositeRequest();
		request.addCriterion(currentUserSupport.createApisCriterionFor(
				getNxtpUserToken(), true));
		final CalContext context = ContextFactory.createContext(getLocale());
		ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, context);

		// Extracting user
		User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);
		final RequestType msgRequestType = new MessageRequestTypeListConversation(
				fromKey, user.getKey());

		// Querying user
		request = ApisFactory.createCompositeRequest();
		final ApisCriterion userCriterion = currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false);
		// If localization is provided, we fetch the nearest place
		if (lat != null && lng != null) {
			userCriterion.addCriterion(SearchRestriction.searchNear(
					Place.class, SearchScope.NEARBY_BLOCK, lat, lng, radius, 5,
					0).aliasedBy(APIS_ALIAS_NEARBY_PLACES));
		}

		// Adding the user criterion
		request.addCriterion(userCriterion);

		// Now querying from User
		request.addCriterion((ApisCriterion) SearchRestriction
				.uniqueKeys(Arrays.asList(fromKey))
				.aliasedBy(APIS_ALIAS_FROM_USER)
				.with(Media.class, MediaRequestTypes.THUMB));

		// Querying messages
		request.addCriterion((ApisCriterion) SearchRestriction
				.list(Message.class, msgRequestType)
				.paginatedBy(MESSAGES_PER_PAGE, page)
				.aliasedBy(APIS_ALIAS_PAGE_MESSAGES)
				.with(Media.class)
				.addCriterion(
						(ApisCriterion) SearchRestriction.adaptKey(
								MSG_FROM_USER_ADAPTER).with(Media.class,
								MediaRequestTypes.THUMB)));
		response = (ApiCompositeResponse) getApiService().execute(request,
				context);

		// Extracting user
		currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		currentUserSupport.initialize(getUrlService(), currentUser);
		// Localizing user if information is provided
		if (lat != null && lng != null) {
			// Getting the nearest places from lat / lng
			final List<? extends Place> places = currentUser.get(Place.class,
					APIS_ALIAS_NEARBY_PLACES);

			// Localizing user
			localizationService.localize(currentUser, places, response, lat,
					lng);
		}

		fromUser = response.getUniqueElement(User.class, APIS_ALIAS_FROM_USER);

		// Initializing instant messaging
		final PaginationInfo paginationInfo = response
				.getPaginationInfo(Message.class);
		final List<? extends Message> messages = currentUser.get(Message.class);
		messagingSupport.initialize(getUrlService(), getLocale(), messages,
				paginationInfo, user.getKey(), PAGE_STYLE_MSG);

		// Initializing page messages
		final List<? extends Message> myMessages = response.getElements(
				Message.class, APIS_ALIAS_PAGE_MESSAGES);
		myPaginationInfo = response.getPaginationInfo(APIS_ALIAS_PAGE_MESSAGES);
		myMessagingSupport.initialize(getUrlService(), getLocale(), myMessages,
				myPaginationInfo, currentUser.getKey(), PAGE_STYLE_MSG);

		// Marking messages as read
		final List<ItemKey> keysToMark = new ArrayList<ItemKey>();
		for (Message message : messages) {
			if (message.isUnread()) {
				keysToMark.add(message.getKey());
			}
		}
		// Marking messages
		if (!keysToMark.isEmpty()) {
			ContextHolder.toggleWrite();
			messageService.markRead(keysToMark);
			readMsgCount = keysToMark.size();
		}

		return SUCCESS;
	}

	@Override
	public String getJson() {
		final List<? extends Message> messages = myMessagingSupport
				.getMessages();
		final JsonOneToOneMessageList messageList = jsonBuilder
				.buildJsonOneToOneMessages(messages, highRes, getLocale(),
						currentUser, fromUser);
		int unreadMsg = messagingSupport.getMessages().size();
		unreadMsg -= readMsgCount;
		messageList.setUnreadMsgCount(unreadMsg);
		messageList.setTotalMsgCount(myPaginationInfo.getItemCount());
		messageList.setPage(page);
		messageList.setPageSize(MESSAGES_PER_PAGE);
		return JSONObject.fromObject(messageList).toString();
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
	}

	@Override
	public void setMessagingSupport(MessagingSupport messagingSupport) {
		this.messagingSupport = messagingSupport;
	}

	public MessagingSupport getMyMessagingSupport() {
		return myMessagingSupport;
	}

	public void setMyMessagingSupport(MessagingSupport myMessagingSupport) {
		this.myMessagingSupport = myMessagingSupport;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return from;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
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

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
	}
}
