package com.nextep.proto.blocks;

import java.util.Collection;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;

public interface MapSupport {

	void initialize(Localized mainPoint,
			Collection<? extends CalmObject> allPoints);

	boolean isMainPoint(Localized point);

	String getIconVar(String category);

	String getJavascriptCentralPoint();

	String getJavascriptMarkers();

	String getJsonMarkers();

	double getZoomLevel();

	String getMapLink();

}
