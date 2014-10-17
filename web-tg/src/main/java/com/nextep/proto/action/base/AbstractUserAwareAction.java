//package com.nextep.proto.action.base;
//
//import java.util.Collection;
//import java.util.Collections;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.nextep.geo.model.City;
//import com.nextep.geo.model.GeographicItem;
//import com.nextep.geo.model.Place;
//import com.nextep.media.model.Media;
//import com.nextep.messages.model.Message;
//import com.nextep.messages.model.impl.RequestTypeUnreadMessages;
//import com.nextep.proto.action.model.MessagingAware;
//import com.nextep.proto.action.model.SearchAware;
//import com.nextep.proto.blocks.CurrentUserSupport;
//import com.nextep.proto.blocks.MessagingSupport;
//import com.nextep.proto.blocks.SearchSupport;
//import com.nextep.proto.helpers.SearchHelper;
//import com.nextep.proto.services.UrlService;
//import com.nextep.users.model.User;
//import com.opensymphony.xwork2.ActionSupport;
//import com.videopolis.apis.factory.ApisFactory;
//import com.videopolis.apis.factory.SearchRestriction;
//import com.videopolis.apis.model.ApisCriterion;
//import com.videopolis.apis.model.ApisRequest;
//import com.videopolis.apis.service.ApiResponse;
//import com.videopolis.apis.service.ApiService;
//import com.videopolis.calm.factory.CalmFactory;
//import com.videopolis.calm.model.ItemKey;
//import com.videopolis.cals.factory.ContextFactory;
//import com.videopolis.smaug.common.model.FacetCategory;
//import com.videopolis.smaug.common.model.SearchScope;
//
//public abstract class AbstractUserAwareAction extends ActionSupport implements
//		SearchAware, MessagingAware, CurrentUserSupport {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 4698469674120411898L;
//	private static final Log LOGGER = LogFactory.getLog(AbstractAction.class);
//
//	private String nxtpUserToken;
//	private User currentUser;
//	private ApiService apiService;
//	private SearchSupport searchSupport;
//	private SearchSupport placeSearchSupport;
//	private MessagingSupport messagingSupport;
//	private UrlService urlService;
//
//	@Override
//	public final String execute() throws Exception {
//		try {
//			if (nxtpUserToken != null) {
//				// FIXME : use a token instead of id here
//				final ItemKey userKey = CalmFactory.parseKey(nxtpUserToken);
//
//				// Preparing facet categories
//				final Collection<FacetCategory> userCategories = SearchHelper
//						.buildUserFacetCategories();
//				final Collection<FacetCategory> placeCategories = SearchHelper
//						.buildPlaceFacetCategories();
//
//				final ApisRequest request = (ApisRequest) ApisFactory
//						.createRequest(User.class)
//						.uniqueKey(userKey.getId())
//						.with(Media.class)
//						.with(SearchRestriction
//								.with(GeographicItem.class)
//								.with(SearchRestriction.withContained(
//										User.class, SearchScope.CHILDREN, 0, 0)
//										.facettedBy(userCategories))
//								.with(SearchRestriction.withContained(
//										Place.class, SearchScope.POI, 0, 0)
//										.facettedBy(placeCategories)))
//						.addCriterion(
//								(ApisCriterion) SearchRestriction
//										.list(Message.class,
//												new RequestTypeUnreadMessages(
//														userKey))
//										.addCriterion(
//												SearchRestriction
//														.adaptKey(messageFromUserAdapter)));
//				final ApiResponse response = apiService.execute(request,
//						ContextFactory.createContext(getLocale()));
//				currentUser = (User) response.getUniqueElement();
//				final City city = currentUser.getUnique(City.class);
//				searchSupport.initialize(city,
//						response.getFacetInformation(SearchScope.CHILDREN),
//						response.getPaginationInfo(User.class),
//						Collections.EMPTY_LIST);
//				messagingSupport.initialize(currentUser.get(Message.class),
//						response.getPaginationInfo(Message.class),
//						currentUser.getKey());
//				placeSearchSupport.initialize(city,
//						response.getFacetInformation(SearchScope.POI),
//						response.getPaginationInfo(Place.class),
//						Collections.EMPTY_LIST);
//				return doExecute();
//			} else {
//				return INPUT;
//			}
//		} catch (Exception e) {
//			LOGGER.error(
//					"Error while executing the action : " + e.getMessage(), e);
//		}
//		return ERROR;
//	}
//
//	protected abstract String doExecute() throws Exception;
//
//	public void setNxtpUserToken(String token) {
//		this.nxtpUserToken = token;
//	}
//
//	public String getNxtpUserToken() {
//		return nxtpUserToken;
//	}
//
//	public void setApiService(ApiService apiService) {
//		this.apiService = apiService;
//	}
//
//	public ApiService getApiService() {
//		return apiService;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.nextep.proto.action.base.CurrentUserSupport#getCurrentUser()
//	 */
//	@Override
//	public User getCurrentUser() {
//		return currentUser;
//	}
//
//	public String getAjaxUserUrl(User user) {
//		return urlService.getUserOverviewUrl("mainContent", user.getKey());
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * com.nextep.proto.action.base.CurrentUserSupport#getCurrentUserMedia()
//	 */
//	@Override
//	public Media getCurrentUserMedia() {
//
//	}
//
//	@Override
//	public void setSearchSupport(SearchSupport facettingSupport) {
//		this.searchSupport = facettingSupport;
//	}
//
//	@Override
//	public SearchSupport getSearchSupport() {
//		return searchSupport;
//	}
//
//	@Override
//	public void setMessagingSupport(MessagingSupport messagingSupport) {
//		this.messagingSupport = messagingSupport;
//	}
//
//	@Override
//	public MessagingSupport getMessagingSupport() {
//		return messagingSupport;
//	}
//
//	public void setPlaceSearchSupport(SearchSupport placesSearchSupport) {
//		this.placeSearchSupport = placesSearchSupport;
//	}
//
//	public SearchSupport getPlaceSearchSupport() {
//		return placeSearchSupport;
//	}
//
//	public void setUrlService(UrlService urlService) {
//		this.urlService = urlService;
//	}
// }
