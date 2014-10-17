package com.nextep.proto.blocks.impl;

import java.util.Collection;

import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.SearchType;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;

public class MapOverviewSupportImpl implements MapSupport {
	private MapSupport baseMapSupport;
	private UrlService urlService;

	private GeographicItem mainPoint;

	@Override
	public void initialize(Localized mainPoint,
			Collection<? extends CalmObject> allPoints) {
		this.mainPoint = (GeographicItem) mainPoint;
		baseMapSupport.initialize(mainPoint, allPoints);
	}

	@Override
	public boolean isMainPoint(Localized point) {
		return baseMapSupport.isMainPoint(point);
	}

	@Override
	public String getIconVar(String category) {
		return baseMapSupport.getIconVar(category);
	}

	@Override
	public String getJavascriptCentralPoint() {
		return baseMapSupport.getJavascriptCentralPoint();
	}

	@Override
	public String getMapLink() {
		GeographicItem geoItem = mainPoint;
		if (mainPoint instanceof Place) {
			geoItem = ((Place) mainPoint).getCity();
		}
		final String url = urlService.buildSearchUrl(
				DisplayHelper.getDefaultAjaxContainer(), geoItem,
				SearchType.MAP);
		return url;
	}

	@Override
	public String getJavascriptMarkers() {
		final String markers = baseMapSupport.getJavascriptMarkers();
		final StringBuilder buf = new StringBuilder();
		// buf.append("var mapDiv = document.getElementById('map');");
		// buf.append("google.maps.event.addDomListener(mapDiv,'click',function() {"
		// + "window.location.href='");
		//
		// buf.append(getMapLink());
		// buf.append("'; });");
		return markers + buf.toString();
	}

	@Override
	public String getJsonMarkers() {
		return baseMapSupport.getJsonMarkers();
	}

	@Override
	public double getZoomLevel() {
		return baseMapSupport.getZoomLevel();
	}

	public void setBaseMapSupport(MapSupport baseMapSupport) {
		this.baseMapSupport = baseMapSupport;
	}

	public void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}
}
