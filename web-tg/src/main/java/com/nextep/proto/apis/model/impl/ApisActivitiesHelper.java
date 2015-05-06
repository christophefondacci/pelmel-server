package com.nextep.proto.apis.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityRequestTypes;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.media.model.Media;
import com.nextep.proto.action.mobile.impl.MobileNearbyActivitiesStatsAction;
import com.nextep.proto.apis.adapters.ApisActivityExtraKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityTargetKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.smaug.solr.model.impl.ActivitySearchItemImpl;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.factory.FacetFactory;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.FacetRange;
import com.videopolis.smaug.common.model.SearchScope;

public final class ApisActivitiesHelper {
	private static final ApisItemKeyAdapter activityTargetKeyAdapter = new ApisActivityTargetKeyAdapter();
	private static final ApisItemKeyAdapter activityUserKeyAdapter = new ApisActivityUserAdapter();
	private static final ApisItemKeyAdapter activityExtraKeyAdapter = new ApisActivityExtraKeyAdapter();

	private ApisActivitiesHelper() {
	}

	public static WithCriterion withActivities(int pageSize, int pageOffset)
			throws ApisException {
		return withActivities(pageSize, pageOffset,
				new RequestTypeLatestActivities(-1, ActivityType.COMMENT,
						ActivityType.CREATION, ActivityType.HOURS,
						ActivityType.LIKE, ActivityType.REGISTER,
						ActivityType.UNLIKE, ActivityType.UPDATE));
	}

	public static WithCriterion withActivities(int pageSize, int pageOffset,
			RequestType requestType) throws ApisException {
		final WithCriterion crit = SearchRestriction.with(Activity.class,
				requestType).paginatedBy(pageSize, pageOffset);
		crit.addCriterion(
				(ApisCriterion) SearchRestriction
						.adaptKey(activityUserKeyAdapter)
						.aliasedBy(Constants.ALIAS_ACTIVITY_USER)
						.with(Media.class)).addCriterion(
				SearchRestriction.adaptKey(activityExtraKeyAdapter).aliasedBy(
						Constants.ALIAS_ACTIVITY_OBJECT));
		return crit;
	}

	public static WithCriterion withUserActivities(int pageSize,
			int pageOffset, ActivityType... activityTypes) throws ApisException {
		if (activityTypes == null || activityTypes.length == 0) {
			activityTypes = new ActivityType[] { ActivityType.COMMENT,
					ActivityType.CREATION, ActivityType.HOURS,
					ActivityType.LIKE, ActivityType.REGISTER,
					ActivityType.UNLIKE, ActivityType.UPDATE };
		}
		final WithCriterion crit = SearchRestriction.with(Activity.class,
				ActivityRequestTypes.fromUser(activityTypes)).paginatedBy(
				pageSize, pageOffset);
		crit.addCriterion(
				(ApisCriterion) SearchRestriction
						.adaptKey(activityTargetKeyAdapter)
						.aliasedBy(Constants.ALIAS_ACTIVITY_TARGET)
						.with(Media.class)).addCriterion(
				SearchRestriction.adaptKey(activityExtraKeyAdapter).aliasedBy(
						Constants.ALIAS_ACTIVITY_OBJECT));
		return crit;
	}

	public static WithCriterion withGeoActivities(int pageSize, int pageOffset,
			String typeFilter) throws ApisException {
		List<Facet> filters = Collections.emptyList();
		if (typeFilter != null) {
			filters = SearchHelper.getTargetTypeFilters(typeFilter);
		}
		final List<Sorter> sorters = SearchHelper.getActivitiesDefaultSorter();
		final WithCriterion crit = SearchRestriction
				.withContained(Activity.class, SearchScope.CHILDREN, pageSize,
						pageOffset).filteredBy(filters).sortBy(sorters);
		// Adding required connected elements
		addActivityConnectedItemsQuery(crit);
		return crit;
	}

	public static void addActivityConnectedItemsQuery(ApisCriterion crit)
			throws ApisException {
		crit.addCriterion(
				(ApisCriterion) SearchRestriction
						.adaptKey(activityUserKeyAdapter)
						.aliasedBy(Constants.ALIAS_ACTIVITY_USER)
						.with(Media.class))
				.addCriterion(
						SearchRestriction.adaptKey(activityExtraKeyAdapter)
								.aliasedBy(Constants.ALIAS_ACTIVITY_OBJECT))
				.addCriterion(
						(ApisCriterion) SearchRestriction
								.adaptKey(activityTargetKeyAdapter)
								.aliasedBy(Constants.ALIAS_ACTIVITY_TARGET)
								.with(Media.class));
	}

	/**
	 * This method builds the facet filters from a statisticActivityType
	 * information combined with a time in the past to query activities. The
	 * created set of facet can then be used to target the specific set of
	 * activities corresponding to the statActivityTime, returning activities as
	 * old as maxActivityTime.
	 * 
	 * @param statActivityType
	 *            the statistic activity type string (provided by the
	 *            {@link MobileNearbyActivitiesStatsAction})
	 * @param maxActivityTime
	 *            the max age in ms of activities to return
	 * @return the set of {@link Facet} which will target these specific
	 *         activities
	 */
	public static Collection<Facet> buildFacetsFromStatActivityType(
			String statActivityType, long maxActivityTime) {
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
		return facets;
	}
}
