package com.nextep.activities.model;

import java.util.Date;

import com.nextep.activities.model.impl.ActivityRequestTypeFromDate;
import com.nextep.activities.model.impl.ActivityRequestTypeFromUserImpl;
import com.videopolis.calm.model.RequestType;

public class ActivityRequestTypes {

	// RequestType FROM_USER = new RequestType() {
	// private static final long serialVersionUID = 7329916012471279745L;
	// };

	/**
	 * Restricts activity search to the given types for the user. Defaults to 30
	 * max activities
	 * 
	 * @param activityTypes
	 *            list of activities
	 * @return the {@link RequestType}
	 */
	public static RequestType fromUser(ActivityType... activityTypes) {
		return new ActivityRequestTypeFromUserImpl(activityTypes);
	}

	/**
	 * Restricts activity search to the given types for the user, returning at
	 * most "maxActivities" number of activities.
	 * 
	 * @param maxActivities
	 *            the maximum number of activities to return or -1 for all
	 * @param activityTypes
	 *            the {@link ActivityType} to restrict
	 * @return the {@link RequestType}
	 */
	public static RequestType fromUser(int maxActivities, ActivityType... activityTypes) {
		return new ActivityRequestTypeFromUserImpl(activityTypes);
	}

	public static RequestType forTypeFromDate(ActivityType activityType, Date fromDate) {
		return new ActivityRequestTypeFromDate(activityType, fromDate);
	}
}
