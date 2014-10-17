package com.nextep.activities.model;

import com.nextep.activities.model.impl.ActivityRequestTypeFromUserImpl;
import com.videopolis.calm.model.RequestType;

public class ActivityRequestTypes {

	// RequestType FROM_USER = new RequestType() {
	// private static final long serialVersionUID = 7329916012471279745L;
	// };

	public static RequestType fromUser(ActivityType... activityTypes) {
		return new ActivityRequestTypeFromUserImpl(activityTypes);
	}
}
