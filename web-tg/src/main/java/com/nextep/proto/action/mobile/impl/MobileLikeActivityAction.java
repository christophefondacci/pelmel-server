package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.json.JSONObject;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.json.model.impl.JsonActivity;
import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.model.Constants;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

public class MobileLikeActivityAction extends AbstractAction implements
		JsonProvider {

	private static final long serialVersionUID = 2547849110682137684L;

	private static final String APIS_ALIAS_LIKES = "likes";
	private static final String APIS_ALIAS_LIKERS = "likers";
	private static final int MAX_LIKES = 100;
	private static final int MAX_LIKERS = 100;

	// Injected services & support
	private CurrentUserSupport currentUserSupport;
	private JsonBuilder jsonBuilder;

	// Internal
	private ApiCompositeResponse response;
	private User currentUser;

	// Dynamic action arguments
	private int page = 0;
	private int pageSize = MAX_LIKES;
	private boolean highRes;

	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						(ApisCriterion) currentUserSupport
								.createApisCriterionFor(getNxtpUserToken(),
										true)
								.with((WithCriterion) ApisActivitiesHelper
										.withUserActivities(pageSize, page,
												ActivityType.LIKE,
												ActivityType.UNLIKE)
										.aliasedBy(APIS_ALIAS_LIKES)
										.addCriterion(
												(ApisCriterion) SearchRestriction
														.adaptKey(
																new ApisActivityUserAdapter())
														.aliasedBy(
																Constants.ALIAS_ACTIVITY_USER)
														.with(Media.class)))
								.with(ApisActivitiesHelper.withActivities(
										pageSize,
										page,
										new RequestTypeLatestActivities(
												pageSize, ActivityType.LIKE,
												ActivityType.UNLIKE))
										.aliasedBy(APIS_ALIAS_LIKERS)));

		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Getting logged user and checking
		currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);

		return SUCCESS;
	}

	@Override
	public String getJson() {
		final List<? extends Activity> likes = currentUser.get(Activity.class,
				APIS_ALIAS_LIKES);
		final List<? extends Activity> likers = currentUser.get(Activity.class,
				APIS_ALIAS_LIKERS);

		final Locale l = getLocale();
		// Building JSON LIKES
		final List<JsonActivity> likesActivities = new ArrayList<JsonActivity>();
		Map<ItemKey, Activity> activitiesMap = new HashMap<ItemKey, Activity>();
		for (Activity a : likes) {
			// Checking if already defined
			Activity lastActivity = activitiesMap.get(a.getLoggedItemKey());
			// Activities are sorted by date DESC so the first one is the most
			// recent, no need to consider any other
			if (lastActivity == null) {
				if (a.getActivityType() == ActivityType.LIKE) {
					final JsonActivity jsonActivity = jsonBuilder
							.buildJsonActivity(a, highRes, l);
					// No need to set the user for each element (size
					// optimization)
					jsonActivity.setUser(null);
					if (jsonActivity.getActivityPlace() != null
							|| jsonActivity.getActivityUser() != null) {
						likesActivities.add(jsonActivity);
					}
				}
				activitiesMap.put(a.getLoggedItemKey(), a);
			}
		}
		// Building JSON LIKERS
		final List<JsonActivity> likersActivities = new ArrayList<JsonActivity>();
		activitiesMap = new HashMap<ItemKey, Activity>();
		for (Activity a : likers) {
			// Checking if already defined
			Activity lastActivity = activitiesMap.get(a.getUserKey());
			if (lastActivity == null) {
				if (a.getActivityType() == ActivityType.LIKE) {
					final JsonActivity jsonActivity = jsonBuilder
							.buildJsonActivity(a, highRes, l);
					likersActivities.add(jsonActivity);
				}
				activitiesMap.put(a.getUserKey(), a);
			}
		}

		final JsonLikeInfo likeInfo = new JsonLikeInfo();
		likeInfo.setKey(currentUser.getKey().toString());
		likeInfo.setLikers(likersActivities);
		likeInfo.setLikes(likesActivities);
		return JSONObject.fromObject(likeInfo).toString();
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}
}
