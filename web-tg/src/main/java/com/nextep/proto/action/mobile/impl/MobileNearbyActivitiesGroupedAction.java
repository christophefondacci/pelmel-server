package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.Activity;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonActivity;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisFacetToItemKeyAdapter;
import com.nextep.proto.apis.model.impl.ApisActivitiesHelper;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisCustomAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileNearbyActivitiesGroupedAction extends AbstractAction
		implements JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_ACTIVITIES = "activities";
	private static final String APIS_ALIAS_FACET_ITEMS = "facetItems";

	// Services
	@Autowired
	private CurrentUserSupport currentUserSupport;
	@Autowired
	private JsonBuilder jsonBuilder;

	// Injected constants
	@Resource(mappedName = "mobile/nearbyPlacesRadius")
	private Double radius;
	@Resource(mappedName = "mobile/maxActivityTimeMs")
	private Long maxActivityTime;
	@Resource(mappedName = "mobile/maxCreationActivityTimeMs")
	private Long maxCreationActivityTimeMs;

	// Dynamic arguments
	private String statActivityType;
	private double lat;
	private double lng;
	private int page = 0;
	private int pageSize = 30;
	private boolean highRes;

	// Internal variables
	private ApiCompositeResponse response;
	private List<? extends CalmObject> objects;
	private List<? extends Activity> activities;

	@Override
	protected String doExecute() throws Exception {
		// Preparing facet filters to query the specific activities
		// corresponding to statActivityType and max age
		Collection<Facet> facets = ApisActivitiesHelper
				.buildFacetsFromStatActivityType(statActivityType,
						maxActivityTime, maxCreationActivityTimeMs);

		// Activities date sorter
		final List<Sorter> activitiesDateSorter = SearchHelper
				.getActivitiesDateSorter(false);

		final FacetCategory placeKeyCategory = SearchHelper
				.getFacetCategory("placeKey");
		final List<FacetCategory> facetCategories = Arrays
				.asList(placeKeyCategory);
		final ApisCustomAdapter facetToItemKeyAdapter = new ApisFacetToItemKeyAdapter(
				SearchScope.NEARBY_ACTIVITIES, placeKeyCategory, pageSize);

		// Building query
		final ApisRequest request = (ApisRequest) ApisFactory
				.createCompositeRequest()
				.addCriterion(
						currentUserSupport.createApisCriterionFor(
								getNxtpUserToken(), true))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.searchNear(Activity.class,
										SearchScope.NEARBY_ACTIVITIES, lat,
										lng, radius, pageSize, page)
								.filteredBy(facets)
								.facettedBy(facetCategories)
								.sortBy(activitiesDateSorter)
								.aliasedBy(APIS_ALIAS_ACTIVITIES)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.customAdapt(
														facetToItemKeyAdapter,
														APIS_ALIAS_FACET_ITEMS)
												.with(Media.class)));

		// Executing
		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Extracting information
		objects = response
				.getElements(CalmObject.class, APIS_ALIAS_FACET_ITEMS);
		activities = response
				.getElements(Activity.class, APIS_ALIAS_ACTIVITIES);
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final List<JsonActivity> jsonActivities = new ArrayList<JsonActivity>();

		// Building activities map for date retrieval
		final Map<String, Activity> activitiesMap = new HashMap<String, Activity>();
		for (Activity a : activities) {
			if (!activitiesMap.containsKey(a.getLoggedItemKey().toString())) {
				activitiesMap.put(a.getLoggedItemKey().toString(), a);
			}
		}
		// Unwrapping facets
		final FacetInformation facetInfo = response
				.getFacetInformation(SearchScope.NEARBY_ACTIVITIES);
		final Map<String, Integer> facetsMap = SearchHelper.unwrapFacets(
				facetInfo, SearchHelper.getFacetCategory("placeKey"));

		final Locale l = getLocale();
		// Converting objects to JSON
		for (CalmObject object : objects) {
			final JsonActivity jsonActivity = new JsonActivity();

			// Converting to JsonUser if User
			if (object instanceof User) {
				final JsonLightUser jsonUser = jsonBuilder.buildJsonLightUser(
						(User) object, highRes, l);
				jsonActivity.setUser(jsonUser);
			} else if (object instanceof Place) {
				// Converting to JsonPlace if Place
				final JsonLightPlace jsonPlace = jsonBuilder
						.buildJsonLightPlace((Place) object, highRes, l);
				jsonActivity.setActivityPlace(jsonPlace);
			}

			// Filling activity type and facet counts
			jsonActivity.setActivityType(statActivityType);
			final Integer count = facetsMap.get(object.getKey().toString());
			jsonActivity.setCount(count);

			// Filling date
			final Activity activity = activitiesMap.get(object.getKey()
					.toString());
			if (activity != null) {
				jsonActivity.setActivityDateValue(activity.getDate());
				jsonActivity.setKey(activity.getKey().toString());
				jsonActivities.add(jsonActivity);
			}
		}
		return JSONArray.fromObject(jsonActivities).toString();
	}

	public String getStatActivityType() {
		return statActivityType;
	}

	public void setStatActivityType(String statActivityType) {
		this.statActivityType = statActivityType;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

}
