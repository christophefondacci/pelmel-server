package com.nextep.proto.action.model;

import com.nextep.proto.blocks.ActivitySupport;

public interface ActivityAware {

	void setActivitySupport(ActivitySupport activitySupport);

	ActivitySupport getActivitySupport();
}
