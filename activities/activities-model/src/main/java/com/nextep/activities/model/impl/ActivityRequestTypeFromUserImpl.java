package com.nextep.activities.model.impl;

import com.nextep.activities.model.ActivityRequestTypeFromUser;
import com.nextep.activities.model.ActivityType;

/**
 * Default implementation of a {@link ActivityRequestTypeFromUser} request type
 * 
 * @author cfondacci
 * 
 */
public class ActivityRequestTypeFromUserImpl implements
		ActivityRequestTypeFromUser {

	private static final long serialVersionUID = 1426806745873338794L;
	private ActivityType[] activityTypes;

	public ActivityRequestTypeFromUserImpl(ActivityType... activityTypes) {
		this.activityTypes = activityTypes;
	}

	@Override
	public ActivityType[] getActivityTypes() {
		return activityTypes;
	}

}
