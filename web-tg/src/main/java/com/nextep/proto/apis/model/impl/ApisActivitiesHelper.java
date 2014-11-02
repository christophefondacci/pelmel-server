package com.nextep.proto.apis.model.impl;

import java.util.Collections;
import java.util.List;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityRequestTypes;
import com.nextep.activities.model.ActivityType;
import com.nextep.media.model.Media;
import com.nextep.proto.apis.adapters.ApisActivityExtraKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityTargetKeyAdapter;
import com.nextep.proto.apis.adapters.ApisActivityUserAdapter;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.Sorter;
import com.videopolis.smaug.common.model.Facet;
import com.videopolis.smaug.common.model.SearchScope;

public final class ApisActivitiesHelper {
	private static final ApisItemKeyAdapter activityTargetKeyAdapter = new ApisActivityTargetKeyAdapter();
	private static final ApisItemKeyAdapter activityUserKeyAdapter = new ApisActivityUserAdapter();
	private static final ApisItemKeyAdapter activityExtraKeyAdapter = new ApisActivityExtraKeyAdapter();

	private ApisActivitiesHelper() {
	}

	public static WithCriterion withActivities(int pageSize, int pageOffset)
			throws ApisException {
		return withActivities(pageSize, pageOffset, null);
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

}
