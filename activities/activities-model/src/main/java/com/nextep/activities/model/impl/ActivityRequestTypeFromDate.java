package com.nextep.activities.model.impl;

import java.util.Date;

import com.nextep.activities.model.ActivityType;
import com.videopolis.calm.model.RequestType;

public class ActivityRequestTypeFromDate implements RequestType {

	private static final long serialVersionUID = 1L;
	private ActivityType activityType;
	private Date fromDate;

	public ActivityRequestTypeFromDate(ActivityType type, Date fromDate) {
		this.activityType = type;
		this.fromDate = fromDate;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public Date getFromDate() {
		return fromDate;
	}
}
