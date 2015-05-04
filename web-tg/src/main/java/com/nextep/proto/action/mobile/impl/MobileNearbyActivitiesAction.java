package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.events.model.Event;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonActivityStatistic;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisActivityTargetKeyAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.smaug.solr.model.impl.ActivitySearchItemImpl;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;

public class MobileNearbyActivitiesAction extends AbstractAction implements
		JsonProvider {

	private static final long serialVersionUID = -3839026287727290015L;
	private final static Log LOGGER = LogFactory
			.getLog(MobileNearbyActivitiesAction.class);

	private static final ApisItemKeyAdapter activityTargetKeyAdapter = new ApisActivityTargetKeyAdapter();

	private static final String APIS_ALIAS_PLACES_ACTIVITIES = "na";
	private static final String APIS_ALIAS_EVENTS_ACTIVITIES = "ea";
	private static final String APIS_ALIAS_MEDIA_ACTIVITIES = "ma";
	private static final int NEARBY_ACTIVITIES_COUNT = 2;
	private CurrentUserSupport currentUserSupport;

	@Resource(mappedName = "mobile/nearbyPlacesRadius")
	private Double radius;
	@Resource(mappedName = "mobile/maxActivityTimeMs")
	private Long maxActivityTime;

	@Autowired
	private JsonBuilder jsonBuilder;

	private Double lat, lng;
	private long lastActivityTime;
	private boolean highRes;

	private ApiCompositeResponse response;

	@SuppressWarnings({ "unchecked" })
	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = ApisFactory.createCompositeRequest();

		// Preparing time facet
		Collection<? extends Facet> facets = null;
		final long minActivityTime = System.currentTimeMillis()
				- maxActivityTime;

		// Converting timestamp to date
		Date lastActivityDate = new Date(minActivityTime);
		final FacetCategory fc = SearchHelper.getFacetCategory("activityDate");
		FacetRange facet = FacetFactory.createFacetRange(fc,
				ActivitySearchItemImpl.DATE_FORMAT.format(lastActivityDate),
				"*");
		facets = Arrays.asList(facet);

		// Preparing facetting
		final Collection<FacetCategory> facetCategories = Arrays
				.asList(SearchHelper.getFacetCategory("activityType"));

		// Adding activities
		final List<Sorter> activitiesDateSorter = SearchHelper
				.getActivitiesDateSorter(false);
		// PLACE activity target type
		request.addCriterion(buildNearbyFacettedCriterion(SearchScope.PLACES,
				(Collection<Facet>) facets, facetCategories,
				activitiesDateSorter, APIS_ALIAS_PLACES_ACTIVITIES));
		// EVNT activity target type
		request.addCriterion(buildNearbyFacettedCriterion(SearchScope.EVENTS,
				(Collection<Facet>) facets, facetCategories,
				activitiesDateSorter, APIS_ALIAS_EVENTS_ACTIVITIES));
		// Querying media
		request.addCriterion(buildNearbyFacettedCriterion(SearchScope.PHOTOS,
				(Collection<Facet>) facets, facetCategories,
				activitiesDateSorter, APIS_ALIAS_MEDIA_ACTIVITIES));
		request.addCriterion(currentUserSupport.createApisCriterionFor(
				getNxtpUserToken(), true));

		// Adding required elements for activity generation
		// ApisActivitiesHelper.addActivityConnectedItemsQuery(activitiesCrit);
		// request.addCriterion(activitiesCrit);

		// Executing query
		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		return SUCCESS;
	}

	private ApisCriterion buildNearbyFacettedCriterion(SearchScope scope,
			Collection<Facet> filters,
			Collection<FacetCategory> facetCategories, List<Sorter> sorter,
			String alias) throws ApisException {

		return (ApisCriterion) SearchRestriction
				.searchNear(Activity.class, scope, lat, lng, radius,
						NEARBY_ACTIVITIES_COUNT, 0)
				.filteredBy(filters)
				.facettedBy(facetCategories)
				.sortBy(sorter)
				.aliasedBy(alias)
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.adaptKey(activityTargetKeyAdapter)
								.aliasedBy(Constants.ALIAS_ACTIVITY_TARGET)
								.with(Media.class));
	}

	@Override
	public String getJson() {
		List<JsonActivityStatistic> jsonActivityStats = new ArrayList<JsonActivityStatistic>();

		final FacetInformation placesFacetInfo = response
				.getFacetInformation(SearchScope.PLACES);
		final FacetInformation eventsFacetInfo = response
				.getFacetInformation(SearchScope.EVENTS);
		final FacetInformation mediaFacetInfo = response
				.getFacetInformation(SearchScope.PHOTOS);

		try {
			final List<? extends Activity> placesActivities = response
					.getElements(Activity.class, APIS_ALIAS_PLACES_ACTIVITIES);
			final List<? extends Activity> eventsActivities = response
					.getElements(Activity.class, APIS_ALIAS_EVENTS_ACTIVITIES);
			final List<? extends Activity> mediaActivities = response
					.getElements(Activity.class, APIS_ALIAS_MEDIA_ACTIVITIES);

			final FacetCategory category = SearchHelper
					.getFacetCategory("activityType");

			final Map<String, Integer> placesFacetsMap = SearchHelper
					.unwrapFacets(placesFacetInfo, category);
			placesFacetsMap.remove(ActivityType.CREATION.getCode());
			fillJsonStats(jsonActivityStats, Place.CAL_TYPE, placesFacetsMap,
					placesActivities);
			final Map<String, Integer> eventsFacetsMap = SearchHelper
					.unwrapFacets(eventsFacetInfo, category);
			fillJsonStats(jsonActivityStats, Event.CAL_ID, eventsFacetsMap,
					eventsActivities);
			final Map<String, Integer> mediaFacetsMap = SearchHelper
					.unwrapFacets(mediaFacetInfo, category);
			fillJsonStats(jsonActivityStats, Media.CAL_TYPE, mediaFacetsMap,
					mediaActivities);
		} catch (ApisException e) {
			LOGGER.error("Cannot get activities: " + e.getMessage(), e);
		}
		return JSONArray.fromObject(jsonActivityStats).toString();
	}

	private void fillJsonStats(List<JsonActivityStatistic> stats,
			String calType, Map<String, Integer> placesFacetsMap,
			List<? extends Activity> activities) {

		// Hashing activities by their type (we might not have all types, there
		// might be a better way, but it saves us from querying each activity
		// type independently)
		final Map<String, Activity> activitiesTypesMap = new HashMap<String, Activity>();
		for (Activity a : activities) {
			if (activitiesTypesMap.get(a.getActivityType().getCode()) == null) {
				activitiesTypesMap.put(a.getActivityType().getCode(), a);
			}
		}
		// Converting to JSON
		for (String activityType : placesFacetsMap.keySet()) {
			final Integer count = placesFacetsMap.get(activityType);
			final JsonActivityStatistic stat = new JsonActivityStatistic();
			stat.setActivityType(activityType + "_" + calType);
			stat.setTotalCount(count);

			final Activity a = activitiesTypesMap.get(activityType);
			if (a != null) {
				stat.setLastId(a.getKey().getNumericId());
				try {
					final CalmObject target = a.getUnique(CalmObject.class,
							Constants.ALIAS_ACTIVITY_TARGET);
					final Media media = MediaHelper.getSingleMedia(target);
					if (media != null) {
						final JsonMedia m = jsonBuilder.buildJsonMedia(media,
								highRes);
						stat.setMedia(m);
					}
				} catch (CalException e) {
					LOGGER.error("Unable to get target object from activity: "
							+ e.getMessage(), e);
				}
			}
			stats.add(stat);
		}
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}

	public void setLastActivityTime(long lastActivityTime) {
		this.lastActivityTime = lastActivityTime;
	}

	public long getLastActivityTime() {
		return lastActivityTime;
	}

	public void setCurrentUserSupport(CurrentUserSupport currentUserSupport) {
		this.currentUserSupport = currentUserSupport;
	}
}
