package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Date;

import net.sf.json.JSONObject;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.json.model.impl.JsonLikeInfo;
import com.nextep.messages.model.MutableMessage;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.action.model.OverviewAware;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.OverviewSupport;
import com.nextep.proto.model.Constants;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.smaug.solr.model.LikeActionResult;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.cals.service.CalService;
import com.videopolis.smaug.common.model.SearchScope;

public class ILikeAction extends AbstractAction implements CurrentUserAware,
		OverviewAware, JsonProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4079757371018576590L;
	private static final String APIS_ALIAS_LIKED_KEY = "liked";
	private static final String APIS_ALIAS_LIKE_COUNT = "count";
	private static final String APIS_ALIAS_USER_LIKERS = "likers";
	private static final String APIS_ALIAS_LIKED_OBJ = "likedObj";
	private SearchPersistenceService searchPersistenceService;
	private CalPersistenceService messageService;
	private CalPersistenceService activitiesService;

	private CurrentUserSupport currentUserSupport;
	private OverviewSupport overviewSupport;
	private LikeActionResult likeResult;
	private int likes = 0;

	private String id;
	private Integer type;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey likedKey = CalmFactory.parseKey(id);
		final ItemKey userKey = CalmFactory.createKey(User.TOKEN_TYPE,
				getNxtpUserToken());

		// Retrieving user from token
		final ApisRequest userRequest = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(likedKey))
								.aliasedBy(APIS_ALIAS_LIKED_KEY)
								.addCriterion(
										SearchRestriction.withContained(
												User.class,
												SearchScope.CHILDREN,
												Constants.MAX_FAVORITE_MEN, 0)
												.aliasedBy(
														APIS_ALIAS_USER_LIKERS)));
		final ApiCompositeResponse userResponse = (ApiCompositeResponse) getApiService()
				.execute(userRequest, ContextFactory.createContext(getLocale()));
		final User currentUser = userResponse.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		final CalmObject likedItem = userResponse.getUniqueElement(
				CalmObject.class, APIS_ALIAS_LIKED_KEY);
		getHeaderSupport().initialize(getLocale(), likedItem, null, null);
		checkCurrentUser(currentUser);
		// We say that we like ;)
		ContextHolder.toggleWrite();
		final CalService calService = ApisRegistry.getCalService(likedKey
				.getType());
		if (calService instanceof CalPersistenceService) {
			((CalPersistenceService) calService).setItemFor(
					currentUser.getKey(), likedKey);
		}

		ContextHolder.toggleWrite();
		// If we liked a user, then generate a message for this
		final MutableMessage msg = (MutableMessage) messageService
				.createTransientObject();
		msg.setFromKey(currentUser.getKey());
		msg.setToKey(likedKey);
		msg.setMessage("This user likes you!");
		msg.setMessageDate(new Date());
		messageService.saveItem(msg);

		// We load user to update search information
		likeResult = searchPersistenceService.toggleLike(currentUser.getKey(),
				likedKey);
		likes = likedItem.get(User.class, APIS_ALIAS_USER_LIKERS).size();
		if (likeResult.wasLiked()) {
			likes++;
		} else {
			likes--;
		}

		// Initializing our activity entry
		final MutableActivity activity = (MutableActivity) activitiesService
				.createTransientObject();
		activity.setUserKey(currentUser.getKey());
		activity.setLoggedItemKey(likedKey);
		activity.setActivityType(likeResult.wasLiked() ? ActivityType.LIKE
				: ActivityType.UNLIKE);
		activity.setDate(new Date());
		// Saving it
		activitiesService.saveItem(activity);

		// Querying back the new number of liker
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest().addCriterion(
						(ApisCriterion) SearchRestriction
								.uniqueKeys(Arrays.asList(likedKey))
								.aliasedBy(APIS_ALIAS_LIKED_OBJ)
								.with(SearchRestriction.withContained(
										User.class, SearchScope.CHILDREN,
										Constants.MAX_FAVORITE_MEN, 0)
										.aliasedBy(APIS_ALIAS_USER_LIKERS)));
		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		final PaginationInfo paginationInfo = response
				.getPaginationInfo(APIS_ALIAS_USER_LIKERS);
		// The following supports are required to provide the "I like" button
		// update

		// Initializing current user support
		currentUserSupport.initialize(getUrlService(), currentUser);

		// Initializing liked object
		overviewSupport.initialize(getUrlService(), getLocale(), likedItem,
				paginationInfo.getItemCount(), 0, currentUser);
		return SUCCESS;
	}

	public void setSearchPersistenceService(
			SearchPersistenceService searchPersistenceService) {
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

	public void setMessageService(CalPersistenceService messageService) {
		this.messageService = messageService;
	}
}
