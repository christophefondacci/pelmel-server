package com.nextep.activities.model;

import com.videopolis.calm.model.RequestType;

public interface ActivityRequestTypeFromUser extends RequestType {

	ActivityType[] getActivityTypes();

	int getMaxActivities();
}
