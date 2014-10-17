package com.nextep.proto.action.model;

import com.nextep.proto.blocks.EventEditionSupport;

public interface EventEditionAware {

	EventEditionSupport getEventEditionSupport();

	void setEventEditionSupport(EventEditionSupport eventEditionSupport);
}
