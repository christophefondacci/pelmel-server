package com.nextep.proto.blocks.impl;

import java.util.Locale;

import com.nextep.proto.blocks.NearbySearchSupport;
import com.nextep.proto.services.DistanceDisplayService;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;

public class NearbySearchSupportImpl implements NearbySearchSupport {

	private ApiResponse response;
	private Locale locale;
	private DistanceDisplayService distanceDisplayService;

	@Override
	public void initialize(Locale locale, ApiResponse response) {
		this.response = response;
		this.locale = locale;
	}

	@Override
	public String getDistanceLabel(CalmObject o) {
		return distanceDisplayService.getDistanceFromItem(o.getKey(), response,
				locale);
	}

	@Override
	public boolean isDistanceAvailable(CalmObject o) {
		final SearchStatistic distanceStat = response.getStatistic(o.getKey(),
				SearchStatistic.DISTANCE);
		return distanceStat != null;
	}

	public void setDistanceDisplayService(
			DistanceDisplayService distanceDisplayService) {
		this.distanceDisplayService = distanceDisplayService;
	}
}
