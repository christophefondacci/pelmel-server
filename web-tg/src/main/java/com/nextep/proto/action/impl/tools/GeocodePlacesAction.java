package com.nextep.proto.action.impl.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.MutablePlace;
import com.nextep.geo.model.Place;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.services.PuffService;
import com.nextep.proto.spring.ContextHolder;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.cals.factory.ContextFactory;

public class GeocodePlacesAction extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3266655573337018284L;
	private CalPersistenceService placesService;
	private PuffService puffService;
	private final List<String> messages = new ArrayList<String>();

	@Override
	protected String doExecute() throws Exception {
		final ApisRequest request = ApisFactory.createRequest(Place.class)
				.list(Place.class, null);
		final ApiResponse response = getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final Collection<Place> places = (Collection<Place>) response
				.getElements();
		ContextHolder.toggleWrite();
		for (Place p : places) {
			if (p.getLatitude() == 0 && p.getLongitude() == 0) {
				String address = p.getAddress1() + " "
						+ DisplayHelper.getName(p.getCity());
				// Removing metro before localization
				final int metroIndex = address.indexOf("M°");
				if (metroIndex >= 0) {
					address = address.substring(0, metroIndex);
				}
				final String cityName = DisplayHelper.getName(p.getCity());
				if (!address.contains(cityName)) {
					address += " " + cityName;
				}
				final URL url = new URL(
						"http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address="
								+ URLEncoder.encode(address, "UTF-8"));
				final HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				final InputStream is = connection.getInputStream();
				if (connection.getResponseCode() == 200) {
					final StringWriter writer = new StringWriter();
					try {
						final Reader reader = new BufferedReader(
								new InputStreamReader(is, "UTF-8"));
						char[] buffer = new char[10240];
						int i = 0;
						while ((i = reader.read(buffer)) != -1) {
							writer.write(buffer, 0, i);
						}
					} finally {
						is.close();
					}
					final String jsonStr = writer.toString();
					// Parsing Google geocoding JSON
					// results[0].geometry.location
					final JSONObject json = (JSONObject) JSONSerializer
							.toJSON(jsonStr);
					JSONArray results = json.getJSONArray("results");
					if (!results.isEmpty()) {
						final JSONObject result = (JSONObject) results.get(0);
						final JSONObject location = result.getJSONObject(
								"geometry").getJSONObject("location");
						final double latitude = location.getDouble("lat");
						final double longitude = location.getDouble("lng");
						messages.add("Place " + p.getName() + "["
								+ p.getKey().toString()
								+ "] has been localized at " + latitude + " - "
								+ longitude);
						puffService.log(p.getKey(), "latitude",
								String.valueOf(p.getLatitude()),
								String.valueOf(latitude), getLocale(), null);
						puffService.log(p.getKey(), "longitude",
								String.valueOf(p.getLongitude()),
								String.valueOf(longitude), getLocale(), null);
						((MutablePlace) p).setLatitude(latitude);
						((MutablePlace) p).setLongitude(longitude);
						placesService.saveItem(p);
					}

				}
				Thread.sleep(1000);
			}
		}
		return SUCCESS;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setPlacesService(CalPersistenceService placeService) {
		this.placesService = placeService;
	}

	public void setPuffService(PuffService puffService) {
		this.puffService = puffService;
	}
}
