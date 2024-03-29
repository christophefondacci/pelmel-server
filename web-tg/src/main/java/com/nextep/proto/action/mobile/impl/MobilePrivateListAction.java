package com.nextep.proto.action.mobile.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.geo.model.Place;
import com.nextep.json.model.IJsonLightUser;
import com.nextep.json.model.IPrivateListContainer;
import com.nextep.json.model.impl.JsonPrivateListResponse;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.messages.model.MessageType;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.UserHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.MessagingService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.nextep.users.model.UserPrivateListRequestType;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONObject;

public class MobilePrivateListAction extends AbstractAction implements JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_USER = "otherUser";

	// Action constants
	private static final String ACTION_REQUEST = "REQUEST";
	private static final String ACTION_CONFIRM = "CONFIRM";
	private static final String ACTION_CANCEL = "CANCEL";
	private static final String ACTION_INVITE = "INVITE";

	@Autowired
	private CurrentUserSupport currentUserSupport;

	@Autowired
	private MessagingService messagingService;

	@Autowired
	private JsonBuilder jsonBuilder;

	// Dynamic arguments
	private String userKey;
	private String action;
	private boolean highRes;

	// Internal vars
	private List<? extends User> pendingApprovals;
	private List<? extends User> pendingRequests;
	private List<? extends User> networkUsers;

	@Override
	protected String doExecute() throws Exception {

		ApiCompositeResponse response = buildAndExecuteRequest();

		// Getting current user and checking authorization
		User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		final User otherUser = response.getUniqueElement(User.class, APIS_ALIAS_USER);

		// Getting lists
		final List<? extends User> pendingApprovals = user.get(User.class,
				Constants.APIS_ALIAS_NETWORK_PENDING_APPROVAL);
		final List<? extends User> pendingRequests = user.get(User.class, Constants.APIS_ALIAS_NETWORK_REQUEST);
		final List<? extends User> networkUsers = user.get(User.class, Constants.APIS_ALIAS_NETWORK_MEMBER);

		// Hashing keys
		final Map<String, User> pendingApprovalsMap = CalHelper.buildItemKeyMap(pendingApprovals);
		final Map<String, User> pendingRequestsMap = CalHelper.buildItemKeyMap(pendingRequests);
		final Map<String, User> networkUsersMap = CalHelper.buildItemKeyMap(networkUsers);

		// Locating user in any of the 3 lists for further use
		final ItemKey userItemKey = CalmFactory.parseKey(userKey);
		final User pendingApprovalUser = pendingApprovalsMap.get(userKey);
		final User pendingRequestUser = pendingRequestsMap.get(userKey);
		final User inNetworkUser = networkUsersMap.get(userKey);

		// Different checks / updates depending on requested action
		if (action != null) {
			// We are going to make changes
			ContextHolder.toggleWrite();
			switch (action) {
			case ACTION_REQUEST:
				// We can only request if not member of any list yet
				if (pendingApprovalUser == null && pendingRequestUser == null && inNetworkUser == null) {
					// Adding user to the "sent requests" list
					getUsersService().setItemFor(user.getKey(), UserPrivateListRequestType.LIST_REQUESTED, userItemKey);
					// Adding connected user who made the action to the pending
					// approval list of other user
					getUsersService().setItemFor(userItemKey, UserPrivateListRequestType.LIST_PENDING_APPROVAL,
							user.getKey());

					// Notification
					final String pushMsg = MessageFormat.format(getText("message.push.networkRequest"),
							user.getPseudo());
					otherUser.addAll(Constants.APIS_ALIAS_NETWORK_PENDING_APPROVAL, Arrays.asList(user));
					messagingService.sendMessage(user, otherUser, null, pushMsg, MessageType.PRIVATE_NETWORK);
				} else {
					setErrorMessage(
							"A request or connection already exists between " + userItemKey + " and " + user.getKey());
				}
				break;
			case ACTION_CONFIRM:
				// We can only confirm a user who is in pending approval list
				if (pendingApprovalUser != null) {
					getUsersService().deleteItemFor(userItemKey, UserPrivateListRequestType.LIST_REQUESTED,
							user.getKey());
					final boolean wasRequested = getUsersService().deleteItemFor(user.getKey(),
							UserPrivateListRequestType.LIST_PENDING_APPROVAL, userItemKey);
					if (!wasRequested) {
						// If user is no longer in the request list, that means
						// the
						// request might have been cancelled, we exit
						setErrorMessage("Request has been cancelled by sender");
						return ERROR;
					} else {
						// Adding mutual relationships as "IN NETWORK" links
						// between
						// the 2 users
						getUsersService().setItemFor(userItemKey, UserPrivateListRequestType.LIST_PRIVATE_NETWORK,
								user.getKey());
						getUsersService().setItemFor(user.getKey(), UserPrivateListRequestType.LIST_PRIVATE_NETWORK,
								userItemKey);
						final String pushMsg = MessageFormat.format(getText("message.push.networkRequestAccepted"),
								user.getPseudo());
						messagingService.sendMessage(user, otherUser, null, pushMsg, MessageType.PRIVATE_NETWORK);
					}
				} else {
					setErrorMessage(
							"Cannot find any pending approval request between " + userKey + " and " + user.getKey());
					return ERROR;
				}
				break;
			case ACTION_CANCEL:
				getUsersService().deleteItemFor(userItemKey, UserPrivateListRequestType.LIST_PENDING_APPROVAL,
						user.getKey());
				getUsersService().deleteItemFor(userItemKey, UserPrivateListRequestType.LIST_REQUESTED, user.getKey());
				getUsersService().deleteItemFor(userItemKey, UserPrivateListRequestType.LIST_PRIVATE_NETWORK,
						user.getKey());
				getUsersService().deleteItemFor(user.getKey(), UserPrivateListRequestType.LIST_PENDING_APPROVAL,
						userItemKey);
				getUsersService().deleteItemFor(user.getKey(), UserPrivateListRequestType.LIST_REQUESTED, userItemKey);
				getUsersService().deleteItemFor(user.getKey(), UserPrivateListRequestType.LIST_PRIVATE_NETWORK,
						userItemKey);
				break;
			case ACTION_INVITE:
				final Place place = response.getUniqueElement(Place.class, APIS_ALIAS_USER);
				final String msg = MessageFormat.format(getText("message.push.inviteCheckin"), user.getPseudo(),
						place.getName());
				for (User networkUser : networkUsers) {
					messagingService.sendMessage(user, networkUser, null, msg, MessageType.PRIVATE_NETWORK);
				}
				break;
			}

			// Requerying for the response showing new list definitions
			response = buildAndExecuteRequest();
			user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
			// Getting lists
			this.pendingApprovals = user.get(User.class, Constants.APIS_ALIAS_NETWORK_PENDING_APPROVAL);
			this.pendingRequests = user.get(User.class, Constants.APIS_ALIAS_NETWORK_REQUEST);
			this.networkUsers = user.get(User.class, Constants.APIS_ALIAS_NETWORK_MEMBER);
		} else {
			this.pendingApprovals = pendingApprovals;
			this.pendingRequests = pendingRequests;
			this.networkUsers = networkUsers;
		}

		// We are good
		return SUCCESS;
	}

	private ApiCompositeResponse buildAndExecuteRequest()
			throws ApisException, UserLoginTimeoutException, CalException {
		// Building request
		final ApisCriterion userCriterion = currentUserSupport.createApisCriterionFor(getNxtpUserToken(), true);
		UserHelper.addPrivateNetworkCriteria(userCriterion);

		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest().addCriterion(userCriterion)
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(CalmFactory.parseKey(userKey)))
						.aliasedBy(APIS_ALIAS_USER)
						.with((WithCriterion) SearchRestriction
								.with(User.class,
										new UserPrivateListRequestType(
												UserPrivateListRequestType.LIST_PENDING_APPROVAL))
								.aliasedBy(Constants.APIS_ALIAS_NETWORK_PENDING_APPROVAL)
								.with(Media.class, MediaRequestTypes.THUMB)));

		// Executing
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		return response;
	}

	@Override
	public String getJson() {
		final IPrivateListContainer response = new JsonPrivateListResponse();

		final List<IJsonLightUser> jsonPendingApprovals = convertUserListToJsonUserList(pendingApprovals);
		final List<IJsonLightUser> jsonPendingRequests = convertUserListToJsonUserList(pendingRequests);
		final List<IJsonLightUser> jsonNetworkUsers = convertUserListToJsonUserList(networkUsers);

		response.setPendingApprovals(jsonPendingApprovals);
		response.setPendingRequests(jsonPendingRequests);
		response.setNetworkUsers(jsonNetworkUsers);

		return JSONObject.fromObject(response).toString();
	}

	private List<IJsonLightUser> convertUserListToJsonUserList(List<? extends User> users) {
		final List<IJsonLightUser> jsonUsers = new ArrayList<IJsonLightUser>();
		for (User user : users) {
			final IJsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(user, highRes, getLocale());
			jsonUsers.add(jsonUser);
		}
		return jsonUsers;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public String getUserKey() {
		return userKey;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}
}
