package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityRequestTypes;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.messages.model.Message;
import com.nextep.messages.model.impl.MessageRequestTypeFactory;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.OverviewAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisExpirableLikesCustomAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.MessagingService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.smaug.solr.model.LikeActionResult;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.impl.ExpirableItemKeyImpl;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.common.model.SearchScope;

import net.sf.json.JSONObject;

public class ILikeAction extends AbstractAction implements CurrentUserAware, OverviewAware, JsonProvider {

	// private static final Log LOGGER = LogFactory.getLog(ILikeAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 4079757371018576590L;
	private static final String APIS_ALIAS_LIKED_KEY = "liked";
	private static final String APIS_ALIAS_USER_LIKERS = "likers";
	private static final String APIS_ALIAS_USER_EXPIRABLE_LIKERS = "expLikers";
	private static final String APIS_ALIAS_LIKED_OBJ = "likedObj";

	private static final ApisItemKeyAdapter eventLocalizationAdapter = new ApisEventLocationAdapter();

	private SearchPersistenceService searchPersistenceService;
	private CalPersistenceService activitiesService;
	@Autowired
	private EventManagementService eventManagementService;
	private CurrentUserSupport currentUserSupport;
	private OverviewSupport overviewSupport;
	@Autowired
	private MessagingService messagingService;
	private LikeActionResult likeResult;
	private int likes = 0;

	private String id;
	private Integer type;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey likedKey = CalmFactory.parseKey(id);

		final ApisCriterion likedCriterion = (ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(likedKey))
				.aliasedBy(APIS_ALIAS_LIKED_KEY)
				.addCriterion(SearchRestriction.with(Message.class, MessageRequestTypeFactory.UNREAD))
				.addCriterion(SearchRestriction.withContained(User.class, SearchScope.CHILDREN, 1, 0)
						.aliasedBy(APIS_ALIAS_USER_LIKERS))
				// We need event localization for timezone
				// computation
				.addCriterion(SearchRestriction.adaptKey(eventLocalizationAdapter))
				.addCriterion(SearchRestriction.customAdapt(new ApisExpirableLikesCustomAdapter(eventManagementService,
						APIS_ALIAS_USER_EXPIRABLE_LIKERS, 1, 0), APIS_ALIAS_USER_EXPIRABLE_LIKERS));
		if (likedKey.getType().equals(User.CAL_TYPE)) {
			likedCriterion.with(GeographicItem.class);
		}
		// Retrieving user from token
		final ApisRequest userRequest = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion((ApisCriterion) currentUserSupport.createApisCriterionFor(getNxtpUserToken(), false)
						.with(Activity.class, ActivityRequestTypes.fromUser(-1, ActivityType.LIKE)))
				.addCriterion(likedCriterion);
		final ApiCompositeResponse userResponse = (ApiCompositeResponse) getApiService().execute(userRequest,
				ContextFactory.createContext(getLocale()));
		final User currentUser = userResponse.getUniqueElement(User.class, CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		final CalmObject likedItem = userResponse.getUniqueElement(CalmObject.class, APIS_ALIAS_LIKED_KEY);
		getHeaderSupport().initialize(getLocale(), likedItem, null, null);
		checkCurrentUser(currentUser);
		// We say that we like ;)
		ContextHolder.toggleWrite();
		final CalService calService = ApisRegistry.getCalService(likedKey.getType());

		// We load user to update search information
		final ItemKey searchLikeKey = computeSearchLikeKey(likedItem);

		if (calService instanceof CalPersistenceService) {
			((CalPersistenceService) calService).setItemFor(currentUser.getKey(), searchLikeKey);
		}

		ContextHolder.toggleWrite();

		likeResult = searchPersistenceService.toggleLike(currentUser.getKey(), searchLikeKey);
		PaginationInfo likePaginationInfo = userResponse.getPaginationInfo(APIS_ALIAS_USER_EXPIRABLE_LIKERS);
		if (likePaginationInfo == null) {
			likePaginationInfo = userResponse.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		}
		likes = likePaginationInfo.getItemCount();
		if (likeResult.wasLiked()) {
			likes++;
			// Initializing our activity entry
			final MutableActivity activity = (MutableActivity) activitiesService.createTransientObject();
			activity.setUserKey(currentUser.getKey());
			activity.setLoggedItemKey(likedKey);
			activity.setActivityType(likeResult.wasLiked() ? ActivityType.LIKE : ActivityType.UNLIKE);
			activity.setDate(new Date());

			// Adding localization to activity
			if (likedItem instanceof GeographicItem) {
				activity.add(likedItem);
			} else {
				final GeographicItem geoItem = likedItem.getUnique(GeographicItem.class);
				activity.add(geoItem);
			}
			// Saving it
			activitiesService.saveItem(activity);
			searchPersistenceService.storeCalmObject(activity, SearchScope.CHILDREN);

			// If we liked a user, then generate a message for this
			if (User.CAL_TYPE.equals(likedKey.getType())) {

				messagingService.sendMessage(currentUser, (User) likedItem, null, "This user likes you!");
			}

		} else {
			likes--;
			final List<? extends Activity> activities = currentUser.get(Activity.class);
			for (Activity activity : activities) {
				if (activity.getLoggedItemKey().equals(likedKey)) {
					((MutableActivity) activity).setVisible(false);
					activitiesService.saveItem(activity);
					searchPersistenceService.storeCalmObject(activity, SearchScope.CHILDREN);
				}
			}
		}

		// Querying back the new number of liker
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion((ApisCriterion) SearchRestriction.uniqueKeys(Arrays.asList(likedKey))
						.aliasedBy(APIS_ALIAS_LIKED_OBJ)
						.with(SearchRestriction
								.withContained(User.class, SearchScope.CHILDREN, Constants.MAX_FAVORITE_MEN, 0)
								.aliasedBy(APIS_ALIAS_USER_LIKERS)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		final PaginationInfo paginationInfo = response.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		// The following supports are required to provide the "I like" button
		// update

		// Initializing current user support
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Initializing liked object
		overviewSupport.initialize(getUrlService(), getLocale(), likedItem, paginationInfo.getItemCount(), 0,
				currentUser);
		return SUCCESS;
	}

	/**
	 * Only here to handle occurrences of an event series
	 * 
	 * @param likedItem
	 *            the {@link CalmObject} being liked / unliked
	 * @return the {@link ItemKey} to store in the search system as the like key
	 */
	private ItemKey computeSearchLikeKey(CalmObject likedItem) {
		if (likedItem instanceof EventSeries) {
			final EventSeries series = (EventSeries) likedItem;
			final City city = eventManagementService.getEventCity(series);
			final Date nextEnd = eventManagementService.computeNext(series, city.getTimezoneId(), false);
			return new ExpirableItemKeyImpl(series.getKey(), nextEnd.getTime());
		}
		return likedItem.getKey();
	}

	public void setSearchPersistenceService(SearchPersistenceService searchPersistenceService) {
		this.searchPersistenceService = searchPersistenceService;
	}

	@Override
	public String getJson() {
		final JsonLikeInfo likeInfo = new JsonLikeInfo();
		likeInfo.setKey(id);
		likeInfo.setLikeCount(likes);
		likeInfo.setDislikeCount(overviewSupport.getDislikesCount());
		likeInfo.setLiked(likeResult.wasLiked());
		return JSONObject.fromObject(likeInfo).toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public CurrentUserSupport getCurrentUserSupport() {
		return currentUserSupport;
	}

	@Override
	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public OverviewSupport getOverviewSupport() {
		return overviewSupport;
	}

	@Override
	public void setOverviewSupport(OverviewSupport overviewSupport) {
		this.overviewSupport = overviewSupport;
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return type == null ? -1 : type;
	}

}
