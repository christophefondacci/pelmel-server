package com.nextep.proto.blocks.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nextep.geo.model.Admin;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.Place;
import com.nextep.json.model.impl.JsonGMarker;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.UrlService;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;

public class MapSupportImpl implements MapSupport {

	private UrlService urlService;

	private Localized mainPoint;
	private Collection<? extends Localized> allPoints;
	private Double customZoomLevel = null;

	@Override
	public void initialize(Localized mainPoint,
			Collection<? extends CalmObject> allPoints) {
		this.mainPoint = mainPoint;
		this.allPoints = (Collection) allPoints;
	}

	@Override
	public boolean isMainPoint(Localized point) {
		return point != null && (point == mainPoint || point.equals(mainPoint));
	}

	@Override
	public String getIconVar(String category) {
		return category + "Icon";
	}

	@Override
	public String getJavascriptCentralPoint() {
		if (mainPoint != null) {
			return "new google.maps.LatLng(" + mainPoint.getLatitude() + ","
					+ mainPoint.getLongitude() + ")";
		} else {
			return "computeCentralPoint(Pelmel.markersarray)";
		}
	}

	@Override
	public String getJavascriptMarkers() {
		final StringBuilder buf = new StringBuilder();
		final StringBuilder iconsBuf = new StringBuilder();
		final Set<String> categories = new HashSet<String>();
		// iconsBuf.append("var shadowIcon = new google.maps.MarkerImage('/images/markers/shadow-marker.png',new google.maps.Size(40,36),new google.maps.Point(0,0),new google.maps.Point(12,36));\n");
		for (Localized l : allPoints) {
			String type = null;
			if (l instanceof Place) {
				final Place p = (Place) l;
				type = p.getPlaceType();
			} else {
				type = "bar";
			}

			// Adding icon place type declaration
			if (!categories.contains(type)) {
				iconsBuf.append(buildIconDeclaration(type));
				categories.add(type);
			}
			final String icon = getIconVar(type);
			buf.append("Pelmel.addPoint(");
			double latitude = l.getLatitude();
			double longitude = l.getLongitude();
			if (latitude == 0 && longitude == 0) {
				if (l instanceof Place) {
					final City c = ((Place) l).getCity();
					latitude = c.getLatitude();
					longitude = c.getLongitude();
				}
			}
			buf.append(latitude + ",");
			buf.append(longitude + ",");
			buf.append("\""
					+ DisplayHelper.getName((CalmObject) l).replace("\"", "")
					+ "\",");
			buf.append(icon);
			buf.append(",null,");
			buf.append("'"
					+ urlService.getMapInfoWindowUrl(((CalmObject) l).getKey())
					+ "'");
			buf.append(");\n");
		}
		if (mainPoint == null) {
			buf.append("Pelmel.map.fitBounds(computeBounds(Pelmel.markersarray));\n");
		}
		return iconsBuf.toString() + buf.toString();
	}

	@Override
	public String getJsonMarkers() {
		final List<JsonGMarker> markers = new ArrayList<JsonGMarker>();
		for (Localized l : allPoints) {

		}
		// TODO Auto-generated method stub
		return null;
	}

	private String buildIconDeclaration(String category) {
		final String imageUrl = urlService.getStaticUrl("/images/markers/"
				+ category + "Marker.png");
		return "var "
				+ getIconVar(category)
				+ " = {url:'"
				+ imageUrl
				+ "', size:new google.maps.Size(28,34),origin:new google.maps.Point(0,0),anchor:new google.maps.Point(14,34)};\n";
	}

	@Override
	public double getZoomLevel() {
		if (customZoomLevel != null) {
			return customZoomLevel;
		} else if (mainPoint instanceof Place) {
			return 16;
		} else if (mainPoint instanceof City) {
			return 12;
		} else if (mainPoint instanceof Admin) {
			return 8;
		} else if (mainPoint instanceof Country) {
			return 5;
		}
		return 10;
	}

	public void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}

	@Override
	public String getMapLink() {
		return null;
	}

	public void setCustomZoomLevel(Double customZoomLevel) {
		this.customZoomLevel = customZoomLevel;
	}
}
