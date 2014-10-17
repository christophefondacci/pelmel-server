package com.nextep.proto.action.model;

import com.nextep.proto.blocks.OverviewSupport;

public interface OverviewAware {

	void setOverviewSupport(OverviewSupport userSupport);

	OverviewSupport getOverviewSupport();
}
