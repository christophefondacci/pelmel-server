package com.nextep.proto.action.impl.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.MutableEvent;
import com.nextep.geo.model.Place;
import com.nextep.geo.model.impl.RequestTypeWithAlternates;
import com.nextep.proto.action.base.AbstractAction;
import com.nextep.proto.blocks.MediaPersistenceSupport;
import com.nextep.proto.exceptions.GenericWebappException;
import com.nextep.proto.services.DescriptionsManagementService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class FacebookEventsBatchAction extends AbstractAction {

	private static final long serialVersionUID = 3359802800746152979L;
	private static final Log LOGGER = LogFactory.getLog("EVENT");
	private static final String APIS_ALIAS_EVENT = "event";
	private static final String APIS_ALIAS_PLACE = "place";
	private static final String APIS_ALIAS_USER = "user";

	private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	@Resource(mappedName = "facebook.accessToken")
	private String accessToken;
	@Resource(mappedName = "systemMessageUserKey")
	private String systemUserKey;

	@Autowired
	@Qualifier("eventsService")
	private CalPersistenceService eventsService;
	@Autowired
	private DescriptionsManagementService descriptionsManagementService;
	@Autowired
	private MediaPersistenceSupport mediaPersistenceSupport;

	@Override
	protected String doExecute() throws Exception {
		final ItemKey systemItemKey = CalmFactory.parseKey(systemUserKey);
		final ApisRequest request = (ApisRequest) ApisFactory.createCompositeRequest()
				.addCriterion(SearchRestriction.list(Place.class, RequestTypeWithAlternates.WITH_ALTERNATES)
						.aliasedBy(APIS_ALIAS_PLACE))
				.addCriterion(SearchRestriction.uniqueKey(systemItemKey).aliasedBy(APIS_ALIAS_USER));

		final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(request,
				ContextFactory.createContext(getLocale()));
		final Collection<? extends Place> places = response.getElements(Place.class, APIS_ALIAS_PLACE);
		final User systemUser = response.getUniqueElement(User.class, APIS_ALIAS_USER);

		ContextHolder.toggleWrite();

		for (Place place : places) {
			if (place.getFacebookId() != null) {
				try {
					processEvents(systemUser, place, place.getFacebookId());
				} catch (ParseException | IOException e) {
					LOGGER.error("Unable to fetch events for '" + place.getKey() + "': " + e.getMessage(), e);
				}
			}
		}

		// TODO Auto-generated method stub
		return SUCCESS;
	}

	private void processEvents(User systemUser, Place place, String facebookId) throws IOException, ParseException {
		final long timestampNow = System.currentTimeMillis() / 1000;
		String fbUrl = "https://graph.facebook.com/v2.4/" + facebookId
				+ "/events/attending/?fields=id,name,description,timezone,start_time,end_time,cover,place&access_token="
				+ accessToken + "&since=" + timestampNow;
		final URL url = new URL(fbUrl);
		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		final InputStream is = connection.getInputStream();
		if (connection.getResponseCode() == 200) {
			final StringWriter writer = new StringWriter();
			try {
				final Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				char[] buffer = new char[10240];
				int i = 0;
				while ((i = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, i);
				}
			} finally {
				is.close();
			}
			final JSONObject json = (JSONObject) JSONSerializer.toJSON(writer.toString());
			final JSONArray data = json.getJSONArray("data");
			for (int i = 0; i < data.size(); i++) {
				JSONObject jsonEvent = data.getJSONObject(i);
				final Date start = sdf.parse(jsonEvent.getString("start_time"));
				Date end = null;
				if (jsonEvent.containsKey("end_time")) {
					final String endTime = jsonEvent.getString("end_time");
					end = sdf.parse(endTime);
				} else {
					// Default 4 hours event
					end = new Date(start.getTime() + 1000 * 60 * 60 * 4);
				}
				final String name = jsonEvent.getString("name");
				final String desc = jsonEvent.getString("description");
				final String id = jsonEvent.getString("id");
				String placeId = null;
				if (jsonEvent.containsKey("place")) {
					final JSONObject jsonPlace = jsonEvent.getJSONObject("place");
					if (jsonPlace.containsKey("id")) {
						placeId = jsonPlace.getString("id");
					}
				}

				final JSONObject jsonCover = jsonEvent.getJSONObject("cover");
				String mediaUrl = null;
				if (jsonCover != null) {
					mediaUrl = jsonCover.getString("source");
					LOGGER.info("MEDIA: " + mediaUrl);
				}
				MutableEvent event = null;
				try {
					// Fetching event by its facebook id
					final ApisRequest r = (ApisRequest) ApisFactory.createCompositeRequest()
							.addCriterion((ApisCriterion) SearchRestriction
									.uniqueKey(CalmFactory.createKey(Event.CAL_ID_FB, id)).aliasedBy(APIS_ALIAS_EVENT)
									.with(Description.class));

					// Fetching event place by its facebook id
					if (placeId != null) {
						r.addCriterion(SearchRestriction.uniqueKey(CalmFactory.createKey(Place.CAL_FB_TYPE, placeId))
								.aliasedBy(APIS_ALIAS_PLACE));
					}
					final ApiCompositeResponse response = (ApiCompositeResponse) getApiService().execute(r,
							ContextFactory.createContext(getLocale()));
					event = response.getUniqueElement(MutableEvent.class, APIS_ALIAS_EVENT);
					if (event == null) {
						event = (MutableEvent) eventsService.createTransientObject();
					}
					if (place == null) {
						place = response.getUniqueElement(Place.class, APIS_ALIAS_PLACE);
					}
					event.setFacebookId(id);
					event.setName(name);
					event.setStartDate(start);
					event.setEndDate(end);
					if (place != null) {
						event.setLocationKey(place.getKey());
					}
					eventsService.saveItem(event);

				} catch (ApisException | CalException e) {
					LOGGER.error("APIS Error element " + (place != null ? "'" + place.getKey() + "'" : facebookId)
							+ " index " + i + ": " + e.getMessage(), e);
				}

				// Creating media
				if (mediaUrl != null) {
					final URL mUrl = new URL(mediaUrl);

					// Storing media in local temp file first as we need to read
					// it several times for thumb generation
					final File tmpFile = File.createTempFile("pelmel-event" + System.currentTimeMillis(), ".png");
					try (final InputStream mediaStream = new BufferedInputStream(mUrl.openStream());
							FileOutputStream w = new FileOutputStream(tmpFile);) {

						byte[] buf = new byte[2048];
						int len = 0;
						while ((len = mediaStream.read(buf, 0, 2048)) >= 0) {
							w.write(buf, 0, len);
						}
					}

					// Creating the media
					mediaPersistenceSupport.createMedia(systemUser, event.getKey(), tmpFile, tmpFile.getName(),
							"image/png", null, false, 0);
				}

				// Creating description
				try {
					descriptionsManagementService.updateDescription(systemUser, event, "en", desc);
				} catch (GenericWebappException e) {
					LOGGER.error("Error storing description of event '" + event.getKey() + "' id:" + id + " -> "
							+ e.getMessage(), e);
				}

				LOGGER.info("Event '" + jsonEvent.getString("name") + "' : " + jsonEvent.getString("description")
						+ " starts at " + jsonEvent.getString("start_time"));
			}
		} else {
			LOGGER.error("Unable to fetch FB events for item " + (place != null ? place.getKey() : facebookId));
		}
	}

}
