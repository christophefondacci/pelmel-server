package com.nextep.proto.action.mobile.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;

import com.nextep.activities.model.Activity;
import com.nextep.json.model.impl.JsonActivity;
import com.nextep.media.model.Media;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.action.model.JsonProvider;
import com.nextep.proto.apis.adapters.ApisActivityExtraKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityTargetKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.blocks.CurrentUserSupport;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.smaug.solr.model.impl.ActivitySearchItemImpl;
import com.nextep.users.model.User;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.model.Sorter;
import com.videopolis.cals.factory.ContextFactory;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;

/**
 * This action provides the detail of activities of a given type and is designed
 * to be called after the {@link MobileNearbyActivitiesStatsAction} with the
 * activityType information of one item of the array.
 * 
 * @author cfondacci
 *
 */
public class MobileNearbyActivitiesAction extends AbstractAction implements
		JsonProvider {

	private static final long serialVersionUID = 1L;
	private static final String APIS_ALIAS_ACTIVITIES = "activities";
	private static final ApisItemKeyAdapter activityTargetKeyAdapter = new ApisActivityTargetKeyAdapter();
	private static final ApisItemKeyAdapter activityExtraKeyAdapter = new ApisActivityExtraKeyAdapter();
	private static final ApisItemKeyAdapter activityUserKeyAdapter = new ApisActivityUserAdapter();

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

	// Dynamic arguments
	private String statActivityType;
	private Double lat;
	private Double lng;
	private int page = 0;
	private int pageSize = 30;
	private boolean highRes;

	// Internal variables
	private ApiCompositeResponse response;
	private List<? extends Activity> activities;

	@Override
	protected String doExecute() throws Exception {

		// Preparing time facet
		Collection<Facet> facets = new ArrayList<Facet>();
		final long minActivityTime = System.currentTimeMillis()
				- maxActivityTime;

		// Converting timestamp to date
		Date lastActivityDate = new Date(minActivityTime);
		final FacetCategory fc = SearchHelper.getFacetCategory("activityDate");
		FacetRange facet = FacetFactory.createFacetRange(fc,
				ActivitySearchItemImpl.DATE_FORMAT.format(lastActivityDate),
				"*");
		facets.add(facet);

		// Building stat activity type filter
		String[] statComponents = statActivityType.split("_");
		if (statComponents[0].length() == 1) {
			// Activity type facet
			final FacetCategory activityTypeCategory = SearchHelper
					.getFacetCategory("activityType");
			facets.add(FacetFactory.createFacet(activityTypeCategory,
					statComponents[0]));

			// Target type facet
			final FacetCategory targetTypeCategory = SearchHelper
					.getFacetCategory("targetType");
			facets.add(FacetFactory.createFacet(targetTypeCategory,
					statComponents[1]));
		} else if (statComponents[1].equals("CREATION")) {
			// Activity type facet = CREATION
			final FacetCategory activityTypeCategory = SearchHelper
					.getFacetCategory("activityType");
			facets.add(FacetFactory.createFacet(activityTypeCategory, "C"));

			// Extra type facet
			final FacetCategory extraTypeCategory = SearchHelper
					.getFacetCategory("extraType");
			facets.add(FacetFactory.createFacet(extraTypeCategory,
					statComponents[0]));
		}

		// Activities date sorter
		final List<Sorter> activitiesDateSorter = SearchHelper
				.getActivitiesDateSorter(false);

		// Building request
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
								.sortBy(activitiesDateSorter)
								.aliasedBy(APIS_ALIAS_ACTIVITIES)
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														activityTargetKeyAdapter)
												.aliasedBy(
														Constants.ALIAS_ACTIVITY_TARGET)
												.with(Media.class))
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														activityExtraKeyAdapter)
												.aliasedBy(
														Constants.ALIAS_ACTIVITY_OBJECT)
												.with(Media.class))
								.addCriterion(
										(ApisCriterion) SearchRestriction
												.adaptKey(
														activityUserKeyAdapter)
												.aliasedBy(
														Constants.ALIAS_ACTIVITY_USER)
												.with(Media.class)));

		// Executing
		response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));

		// Checking user
		User user = response.getUniqueElement(User.class,
				CurrentUserSupport.APIS_ALIAS_CURRENT_USER);
		checkCurrentUser(user);

		// Extracting activities
		activities = response
				.getElements(Activity.class, APIS_ALIAS_ACTIVITIES);
		return SUCCESS;
	}

	@Override
	public String getJson() {
		final List<JsonActivity> jsonActivities = new ArrayList<JsonActivity>();
		for (Activity a : activities) {
			final JsonActivity jsonActivity = jsonBuilder.buildJsonActivity(a,
					highRes, getLocale());
			jsonActivities.add(jsonActivity);
		}

		return JSONArray.fromObject(jsonActivities).toString();
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

	public String getStatActivityType() {
		return statActivityType;
	}

	public void setStatActivityType(String statActivityType) {
		this.statActivityType = statActivityType;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage() {
		return page;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setHighRes(boolean highRes) {
		this.highRes = highRes;
	}

	public boolean isHighRes() {
		return highRes;
	}
}
