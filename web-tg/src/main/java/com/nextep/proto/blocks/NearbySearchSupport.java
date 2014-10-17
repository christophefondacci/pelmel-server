package com.nextep.proto.blocks;

import java.util.Locale;

import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;

public interface NearbySearchSupport {

	void initialize(Locale locale, ApiResponse response);

	String getDistanceLabel(CalmObject o);

	boolean isDistanceAvailable(CalmObject o);
}
