package com.nextep.proto.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.descriptions.model.Description;
import com.nextep.descriptions.model.DescriptionRequestType;
import com.nextep.events.model.Event;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.proto.action.base.AbstractSearchUserAction;
import com.nextep.proto.action.model.CurrentUserAware;
import com.nextep.proto.action.model.GeoPlaceTypesAware;
import com.nextep.proto.action.model.MessagingAware;
import com.nextep.proto.apis.adapters.ApisEventLocationAdapter;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.blocks.GeoPlaceTypesSupport;
import com.nextep.proto.blocks.MessagingSupport;
import com.nextep.proto.exceptions.UserLoginTimeoutException;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.model.SearchType;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.helper.ApisRegistry;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

public class SearchUserAction extends AbstractSearchUserAction implements
		CurrentUserAware, MessagingAware, GeoPlaceTypesAware {

	// Constants declaration
	private static final long serialVersionUID = -4510271313233554759L;
	private static final Log LOGGER = LogFactory.getLog(SearchUserAction.class);
	private static final String APIS_ALIAS_GEO = "geo";
	private static final String APIS_ALIAS_CITY = "city";
	private static final ApisItemKeyAdapter EVENT_CITY_ADAPTER = new ApisEventLocationAdapter();
	private static final ApisFacetToItemKeyAdapter CITY_FACET_ADAPTER = new ApisFacetToItemKeyAdapter(
			SearchScope.CHILDREN, SearchHelper.getCityFacetCategory(),
			Constants.MAX_USER_POPULAR_CITIES);
	private static final RequestType DESC_MAIN_TYPE = DescriptionRequestType.SINGLE_DESC;

	// Injected services & supports
	private CurrentUserSupport currentUserSupport;
	private MessagingSupport messagingSupport;
	private GeoPlaceTypesSupport geoPlaceTypesSupport;

	@Override
	protected ApisRequest buildApisRequest(ItemKey geoItemKey)
			throws ApisException, CalException, UserLoginTimeoutException {
		// Preparing facet categories
		final Collection<FacetCategory> userCategories = SearchHelper
				.buildUserFacetCategories();
		final List<Sorter> userSorters = SearchHelper.getUserDefaultSorters();
		final Collection<FacetCategory> placeCategories = SearchHelper
				.buildPlaceFacetCategoriesForUserSearch();
		// Building facet filters
		final List<Facet> facetFilters = buildFacetFilters(userCategories
				.iterator().next());
		final Class<? extends CalmObject> calClass = ApisRegistry
				.getModelFromType(geoItemKey.getType());
		final ApisRequest request = ApisFactory.createCompositeRequest();
		ApisCriterion rootCriterion = null;
		if (calClass == null) {
			rootCriterion = SearchRestriction.alternateKey(
					GeographicItem.class, geoItemKey).aliasedBy(APIS_ALIAS_GEO);
		} else {
			rootCriterion = (ApisCriterion) SearchRestriction
					.alternateKey(calClass, geoItemKey)
					.aliasedBy(APIS_ALIAS_GEO).with(GeographicItem.class);
		}
		final ApisCriterion searchCriterion = (ApisCriterion) rootCriterion
				.with((WithCriterion) SearchRestriction
						.withContained(User.class, SearchScope.CHILDREN,
								USERS_PER_PAGE, getPageOffset())
						.facettedBy(userCategories).filteredBy(facetFilters)
						.sortBy(userSorters)
						.customAdapt(CITY_FACET_ADAPTER, APIS_ALIAS_CITY)
						.with(Media.class).with(Tag.class)
						.with(GeographicItem.class)
						.with(Description.class, DESC_MAIN_TYPE));

		// Adding place and events pagination
		final Collection<FacetCategory> placeTypeFacet = Arrays
				.asList(SearchHelper.getPlaceTypeCategory());
		rootCriterion.addCriterion(SearchRestriction.withContained(Place.class,
				SearchScope.NEARBY_BLOCK, 5, 0).facettedBy(placeTypeFacet));
		rootCriterion.addCriterion(SearchRestriction.withContained(Event.class,
				SearchScope.EVENTS, 0, 0).aliasedBy(
				Constants.APIS_ALIAS_EVENT_COUNT));

		// Adding places information and top cities listing if geographic entity
		if (calClass == null
				|| (calClass == GeographicItem.class && calClass != Place.class)) {
			searchCriterion.with(SearchRestriction.withContained(Place.class,
					SearchScope.POI, 0, 0).facettedBy(placeCategories));
		}
		request.addCriterion(searchCriterion);
		request.addCriterion(currentUserSupport.createApisCriterionFor(
				getNxtpUserToken(), false));
		return request;

	}

	@Override
	protected void initialize(ApiResponse response) throws ApisException,
			UserLoginTimeoutException {
		final ApiCompositeResponse r = (ApiCompositeResponse) response;

		final CalmObject geopoint = r.getUniqueElement(CalmObject.class,
				APIS_ALIAS_GEO);
		// Extracting localization (TODO: Check this, I think it is useless)
		GeographicItem geoItem = null;
		if (geopoint instanceof GeographicItem) {
			geoItem = (GeographicItem) geopoint;
		} else {
			try {
				geoItem = geopoint.getUnique(GeographicItem.class);
			} catch (CalException e) {
				LOGGER.error("Cannot extract GeographicItem from "
						+ geopoint.getKey() + " : " + e.getMessage());
			}
		}
		// TODO: End check
		final List<? extends User> users = geopoint == null ? null : geopoint
				.get(User.class);
		getSearchSupport().initialize(getSearchType(), getUrlService(),
				getLocale(), geopoint, DisplayHelper.getName(geopoint),
				response.getFacetInformation(SearchScope.CHILDREN),
				response.getPaginationInfo(User.class), users);
		getLocalizationSupport().initialize(getSearchType(), getUrlService(),
				getLocale(), geoItem,
				response.getFacetInformation(SearchScope.CHILDREN));
		// getPlacesSupport().initialize(geopoint.get(Place.class));
		getPlaceSearchSupport()
				.initialize(getSearchType(), getUrlService(), getLocale(),
						geoItem, DisplayHelper.getName(geopoint),
						response.getFacetInformation(SearchScope.POI),
						response.getPaginationInfo(Place.class),
						Collections.EMPTY_LIST);

		getHeaderSupport().initialize(getLocale(), geopoint,
				getSearchSupport(), SearchType.MEN);
		final User currentUser = r.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(currentUser);
		currentUserSupport.initialize(getUrlService(), currentUser);
		messagingSupport.initialize(getUrlService(), getLocale(),
				currentUser.get(Message.class),
				response.getPaginationInfo(Message.class),
				currentUser.getKey(), getHeaderSupport().getPageStyle());
		final List<? extends City> popularCities = r.getElements(City.class,
				APIS_ALIAS_CITY);
		final List<FacetCount> facetCounts = r.getFacetInformation(
				SearchScope.CHILDREN).getFacetCounts(
				SearchHelper.getCityFacetCategory());
		getPopularSupport().initialize(getSearchType(), getLocale(),
				getUrlService(), geopoint, popularCities, facetCounts);

		// Initializing media for gallery
		final List<Media> medias = new ArrayList<Media>();
		for (User user : users) {
			final Media media = MediaHelper.getSingleMedia(user);
			if (media != null) {
				medias.add(media);
			}
		}
		getMediaProvider().initialize(geopoint.getKey(), medias);

		getTagSupport().initialize(getLocale(), Collections.EMPTY_LIST);

		// Initializing geo place types
		final FacetInformation fi = response
				.getFacetInformation(SearchScope.NEARBY_BLOCK);
		getGeoPlaceTypesSupport().initialize(getLocale(), getUrlService(),
				geoItem, fi, SearchType.MEN.getSubtype(), response);

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
		this.messagingSupport = messagingSupport;
	}

	@Override
	public MessagingSupport getMessagingSupport() {
		return messagingSupport;
	}

	@Override
	public void setGeoPlaceTypesSupport(
			GeoPlaceTypesSupport geoPlaceTypesSupport) {
		this.geoPlaceTypesSupport = geoPlaceTypesSupport;
	}

	@Override
	public GeoPlaceTypesSupport getGeoPlaceTypesSupport() {
		return geoPlaceTypesSupport;
	}
}
