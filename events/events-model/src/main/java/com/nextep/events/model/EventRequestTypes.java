package com.nextep.events.model;

import com.videopolis.calm.model.RequestType;

public interface EventRequestTypes {

	RequestType FUTURE_EVENTS = new RequestType() {
		private static final long serialVersionUID = -6443319168841147911L;
	};

	RequestType ALL_EVENTS = new RequestType() {
		private static final long serialVersionUID = -4039488149282119268L;
	};
	RequestType NEWEST_FIRST = new RequestType() {
		private static final long serialVersionUID = -6226565658236874263L;
	};
}
