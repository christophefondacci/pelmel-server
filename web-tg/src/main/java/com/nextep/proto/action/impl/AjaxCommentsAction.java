package com.nextep.proto.action.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import com.nextep.comments.model.Comment;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.Commentable;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisCommentUserKeyAdapter;
import com.nextep.proto.apis.model.impl.ApisCommentsHelper;
import com.nextep.proto.blocks.CommentSupport;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.SelectableTagSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.services.LocalizationService;
import com.nextep.tags.model.Tag;
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
import com.videopolis.cals.model.PaginationInfo;
import com.videopolis.smaug.common.model.SearchScope;

public class AjaxCommentsAction extends AbstractAction implements Commentable,
		JsonProvider {

	private static final long serialVersionUID = -5577138128198119138L;
	private static final String APIS_ALIAS_COMMENTS = "comments";
	private static final String APIS_ALIAS_TAGS = "tags";
	private static final String APIS_ALIAS_COMMENTED_ITEM = "item";
	private static final String APIS_ALIAS_NEARBY_PLACES = "places";
	private static final ApisItemKeyAdapter COMMENT_USER_ADAPTER = new ApisCommentUserKeyAdapter();

	// Injected supports & builders
	private CommentSupport commentSupport;
	private SelectableTagSupport commentTagSupport;
	private CurrentUserSupport currentUserSupport;
	private JsonBuilder jsonBuilder;
	private LocalizationService localizationService;
	private double radius;

	// Dynamic page arguments
	private String id;
	private int page;
	private boolean highRes;
	private Double lat, lng;

	// Internal variables
	private List<? extends Comment> comments;

	@SuppressWarnings("unchecked")
	@Override
	protected String doExecute() throws Exception {
		final ItemKey itemKey = CalmFactory.parseKey(id);

		// Building user criterion
		final ApisCriterion userCriterion = (ApisCriterion) currentUserSupport
				.createApisCriterionFor(getNxtpUserToken(), false).with(
						ApisCommentsHelper.getUserTagsFor(itemKey));

		// If localization is provided, we fetch the nearest place
		if (lat != null && lng != null) {
			userCriterion.addCriterion(SearchRestriction.searchNear(
					Place.class, SearchScope.NEARBY_BLOCK, lat, lng, radius, 5,
					0).aliasedBy(APIS_ALIAS_NEARBY_PLACES));
		}

		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(userCriterion)
				.addCriterion(
						ApisCommentsHelper.getCommentsFor(itemKey, page)
								.aliasedBy(APIS_ALIAS_COMMENTS))
				.addCriterion(
						ApisCommentsHelper.listCommentTags(itemKey.getType())
								.aliasedBy(APIS_ALIAS_TAGS))
				.addCriterion(
						SearchRestriction.uniqueKeys(Arrays.asList(itemKey))
								.aliasedBy(APIS_ALIAS_COMMENTED_ITEM));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService()
				.execute(request, ContextFactory.createContext(getLocale()));

		// Header initialization for style customization
		final CalmObject commentedItem = response.getUniqueElement(
				CalmObject.class, APIS_ALIAS_COMMENTED_ITEM);
		getHeaderSupport().initialize(getLocale(), commentedItem, null, null);

		// Initializing current user
		final User currentUser = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		currentUserSupport.initialize(getUrlService(), currentUser);

		// User authorization and timeout management
		// checkCurrentUser(currentUser);

		// Localizing user if information is provided
		if (lat != null && lng != null) {
			// Getting the nearest places from lat / lng
			final List<? extends Place> places = currentUser.get(Place.class,
					APIS_ALIAS_NEARBY_PLACES);

			// Localizing user
			localizationService.localize(currentUser, places, response, lat,
					lng);
		}

		// Retrieving comments, pagination and initializing supports
		comments = response.getElements(Comment.class, APIS_ALIAS_COMMENTS);

		final PaginationInfo paginationInfo = response
				.getPaginationInfo(APIS_ALIAS_COMMENTS);
		commentSupport.initialize(getUrlService(), getLocale(), comments,
				paginationInfo, currentUser, itemKey);
		final List<Tag> tags = (List<Tag>) response.getElements(Tag.class,
				APIS_ALIAS_TAGS);
		commentTagSupport.initialize(getLocale(), tags);
		commentTagSupport.initializeSelection(getUrlService(), itemKey,
				currentUser);

		return SUCCESS;
	}

	@Override
	public void setCommentSupport(CommentSupport commentSupport) {
		this.commentSupport = commentSupport;
	}

	@Override
	public CommentSupport getCommentSupport() {
		return commentSupport;
	}

	@Override
	public String getJson() {
		// More recent is last for proper mobile display
		Collections.reverse(comments);

		// Building the response as a message response
		final JsonManyToOneMessageList msgList = jsonBuilder
				.buildJsonMessagesFromComments(comments, highRes, getLocale());

		// Setting the number of unread messages
		final List<? extends Message> unreadMessages = currentUserSupport
				.getCurrentUser().get(Message.class);
		msgList.setUnreadMsgCount(unreadMessages.size());

		// Returning JSON representation
		return JSONObject.fromObject(msgList).toString();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}

	@Override
	public void setCommentTagSupport(SelectableTagSupport commentTagSupport) {
		this.commentTagSupport = commentTagSupport;
	}

	@Override
	public SelectableTagSupport getCommentTagSupport() {
		return commentTagSupport;
	}

	public void setJsonBuilder(JsonBuilder jsonBuilder) {
		this.jsonBuilder = jsonBuilder;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLat() {
		return lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
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
}
