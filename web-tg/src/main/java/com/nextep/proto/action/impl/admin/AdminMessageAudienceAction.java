package com.nextep.proto.action.impl.admin;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityRequestTypes;
import com.nextep.activities.model.ActivityType;
import com.nextep.advertising.model.Subscription;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonMessageAudienceResponse;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProviderWithError;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.services.MessagingService;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONObject;

public class AdminMessageAudienceAction extends AbstractAction implements JsonProviderWithError {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_PLACE = "place";
	private static final String APIS_ALIAS_SYSTEM_USER = "sysuser";

	@Autowired
	private CurrentUserSupport currentUserSupport;

	@Autowired
	private MessagingService messagingService;

	@Resource(mappedName = "announcement.reach.days")
	private Integer reachDays;

	@Resource(mappedName = "announcement.minDays")
	private Integer announcementMinDays;

	@Resource(mappedName = "systemMessageUserKey")
	private String systemUserKey;

	// Internal variables
	private ApisItemKeyAdapter activityUserAdapter = new ApisActivityUserAdapter();
	private Set<User> users;
	private JsonMessageAudienceResponse errorResponse;
	private Date nextAnnouncementDate;

	// Dynamic arguments
	private String placeKey;
	private boolean countReach;
	private String message;

	@Override
	protected String doExecute() throws Exception {

		final ItemKey itemKey = CalmFactory.parseKey(placeKey);
		final long fromTime = System.currentTimeMillis() - ((long) reachDays * 24 * 60 * 60 * 1000);
		final Date fromDate = new Date(fromTime);
		final ItemKey systemUserItemKey = CalmFactory.parseKey(systemUserKey);

		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(getNxtpUserToken(),
								true))
				.addCriterion(
						(ApisCriterion) SearchRestriction.uniqueKey(itemKey)
								.aliasedBy(APIS_ALIAS_PLACE).with(
										Subscription.class)
								.with((WithCriterion) SearchRestriction
										.with(Activity.class,
												ActivityRequestTypes.forTypeFromDate(ActivityType.CHECKIN, fromDate))
										.addCriterion(SearchRestriction.adaptKey(activityUserAdapter))))
				.addCriterion(SearchRestriction.uniqueKey(systemUserItemKey).aliasedBy(APIS_ALIAS_SYSTEM_USER));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user access
		final User user = response.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Checking ownership
		final Place place = response.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
		final List<? extends Subscription> subscriptions = place.get(Subscription.class);
		Subscription currentSubscription = null;
		for (Subscription subscription : subscriptions) {
			if (subscription.getRelatedItemKey().equals(itemKey)
					&& subscription.getPurchaserItemKey().equals(user.getKey())
					&& subscription.getStartDate().getTime() < System.currentTimeMillis()
					&& subscription.getEndDate().getTime() > System.currentTimeMillis()) {
				currentSubscription = subscription;
				break;
			}
		}

		// If no subscription, then access is not authorized
		if (currentSubscription == null) {
			errorResponse = new JsonMessageAudienceResponse();
			errorResponse.setOwnershipError(true);
			return error(403);
		} else {
			Long nextTime = null;
			if (currentSubscription.getLastAnnouncementDate() != null) {
				nextTime = currentSubscription.getLastAnnouncementDate().getTime()
						+ (announcementMinDays.longValue() * 24 * 60 * 60 * 1000);
			}
			nextAnnouncementDate = nextTime == null ? null : new Date(nextTime);
		}

		users = new HashSet<>();
		final List<? extends Activity> activities = place.get(Activity.class);
		for (Activity activity : activities) {
			final User activityUser = activity.getUnique(User.class);
			users.add(activityUser);
		}

		// If not count only we proceed with sending message
		if (!countReach) {

			// First checking eligibility
			if (nextAnnouncementDate != null && nextAnnouncementDate.getTime() > System.currentTimeMillis()) {
				errorResponse = new JsonMessageAudienceResponse();
				errorResponse.setNextAnnouncementDate(nextAnnouncementDate);
				errorResponse.setUsersReached(users.size());
				return error(403);
			}

			// Sending message to all users
			for (User u : users) {
				messagingService.sendMessage(place, u, null, message);
			}

			// System confirmation message
			final User systemUser = response.getUniqueElement(User.class, APIS_ALIAS_SYSTEM_USER);
			final String template = getText("announcement.feedback");
			final String sysMessage = MessageFormat.format(template, new Object[] { users.size(), message, reachDays });
			messagingService.sendMessage(systemUser, user, null, sysMessage);
		}
		//
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final JsonMessageAudienceResponse json = new JsonMessageAudienceResponse();
		json.setMessageSent(!countReach);
		json.setUsersReached(users.size());
		json.setNextAnnouncementDate(nextAnnouncementDate);
		return JSONObject.fromObject(json).toString();
	}

	@Override
	public String getJsonError() {
		if (errorResponse == null) {
			errorResponse = new JsonMessageAudienceResponse();
		}
		return JSONObject.fromObject(errorResponse).toString();
	}

	public void setPlaceKey(String placeKey) {
		this.placeKey = placeKey;
	}

	public String getPlaceKey() {
		return placeKey;
	}

	public void setCountReach(boolean countReach) {
		this.countReach = countReach;
	}

	public boolean isCountReach() {
		return countReach;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
