package com.nextep.proto.action.model;

import com.nextep.proto.blocks.NearbySearchSupport;

public interface NearbySearchAware {

	void setNearbySearchSupport(NearbySearchSupport nearbySearchSupport);

	NearbySearchSupport getNearbySearchSupport();
}
