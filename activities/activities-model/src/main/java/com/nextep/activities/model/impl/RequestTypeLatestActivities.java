package com.nextep.activities.model.impl;

import com.nextep.activities.model.ActivityType;
import com.videopolis.calm.model.RequestType;

/**
 * The request type to use to constraint the listing of activities using the
 * generic CAL list service.
 * 
 * @author cfondacci
 * 
 */
public class RequestTypeLatestActivities implements RequestType {

	private static final long serialVersionUID = 2286317742312557949L;

	private final int activitiesCount;
	private final ActivityType[] activityTypes;
	private final boolean includeUserDirectActivity;

	/**
	 * Builds a request type that specifies the number of activities to fetch
	 * and the optional Activity type restrictions
	 * 
	 * @param activitiesCount
	 *            number of activities to retrieve
	 * @param activityTypes
	 *            type of activities to retrieve, if not specified all
	 *            activities will be returned.
	 */
	public RequestTypeLatestActivities(int activitiesCount,
			ActivityType... activityTypes) {
		this(false, activitiesCount, activityTypes);
	}

	public RequestTypeLatestActivities(boolean includeUserActivity,
			int activitiesCount, ActivityType... activityTypes) {
		this.includeUserDirectActivity = includeUserActivity;
		this.activitiesCount = activitiesCount;
		this.activityTypes = activityTypes;
	}

	public int getActivitiesCount() {
		return activitiesCount;
	}

	public ActivityType[] getActivityTypes() {
		return activityTypes;
	}

	public boolean includeUserDirectActivity() {
		return includeUserDirectActivity;
	}
}
