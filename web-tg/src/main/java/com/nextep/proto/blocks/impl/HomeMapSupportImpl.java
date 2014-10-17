package com.nextep.proto.blocks.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.blocks.MapSupport;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.Localized;

public class HomeMapSupportImpl implements MapSupport {

	private static final Log LOGGER = LogFactory
			.getLog(HomeMapSupportImpl.class);
	private UrlService urlService;
	private Collection<? extends CalmObject> allActivities;

	@Override
	public void initialize(Localized mainPoint,
			Collection<? extends CalmObject> allPoints) {
		this.allActivities = allPoints;
	}

	@Override
	public boolean isMainPoint(Localized point) {
		return false;
	}

	@Override
	public String getIconVar(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJavascriptCentralPoint() {
		return "computeCentralPoint(Pelmel.markersarray)";
	}

	@Override
	public String getJavascriptMarkers() {
		final StringBuilder buf = new StringBuilder();
		final StringBuilder iconsBuf = new StringBuilder();
		final Set<String> categories = new HashSet<String>();

		// Preparing pointer
		final String pointerName = "pointerMarker";
		iconsBuf.append("var " + pointerName + "=Pelmel.buildMapPointer();");
		for (CalmObject o : allActivities) {
			final Activity activity = (Activity) o;

			// Extracting target
			try {
				final CalmObject target = activity.getUnique(CalmObject.class,
						Constants.ALIAS_ACTIVITY_TARGET);
				if ((target instanceof Localized) && !(target instanceof User)) {
					final Localized l = (Localized) target;
					// Extracting user
					// final User user = activity.getUnique(User.class,
					// Constants.ALIAS_ACTIVITY_USER);
					// final Media userPhoto = MediaHelper.getSingleMedia(user);
					final Media placePhoto = MediaHelper.getSingleMedia(target);
					final String photoUrl = placePhoto != null ? placePhoto
							.getMiniThumbUrl() : MediaHelper
							.getNoMiniThumbUrl();
					// Adding icon place type declaration
					final String iconName = "icon" + target.getKey().getId();
					iconsBuf.append("var " + iconName
							+ "=Pelmel.buildMapThumbMarker('" + photoUrl
							+ "');");

					// Computing info window URL
					final String popupUrl = urlService
							.getMapInfoWindowUrl(((CalmObject) l).getKey());
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
							+ DisplayHelper.getName((CalmObject) l).replace(
									"\"", "") + "\",");
					buf.append(iconName);
					buf.append(",shadowIcon,");
					buf.append("'" + popupUrl + "'");
					buf.append(",'" + activity.getKey() + "'");
					buf.append(");\n");
					// Adding pointer
					buf.append("Pelmel.addPoint(");
					buf.append(latitude + ",");
					buf.append(longitude + ",");
					buf.append("\""
							+ DisplayHelper.getName((CalmObject) l).replace(
									"\"", "") + "\",");
					buf.append(pointerName);
					buf.append(",shadowIcon,");
					buf.append("'" + popupUrl + "',");
					buf.append("null,true);\n");
				}
			} catch (CalException e) {
				LOGGER.error(
						"Error while generating home page markers: "
								+ e.getMessage(), e);
			}
		}
		buf.append("Pelmel.map.fitBounds(computeBounds(Pelmel.markersarray));\n");
		buf.append("Pelmel.mapTimerVar = setTimeout(function() {Pelmel.mapTimer();},5000);");
		return iconsBuf.toString() + buf.toString();
	}

	@Override
	public String getJsonMarkers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getZoomLevel() {
		return 10;
	}

	@Override
	public String getMapLink() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}
}
