package com.nextep.proto.builders.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.advertising.model.AdvertisingBooster;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.CalendarType;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.impl.JsonActivity;
import com.nextep.json.model.impl.JsonDescription;
import com.nextep.json.model.impl.JsonEvent;
import com.nextep.json.model.impl.JsonFacet;
import com.nextep.json.model.impl.JsonHour;
import com.nextep.json.model.impl.JsonLightCity;
import com.nextep.json.model.impl.JsonLightPlace;
import com.nextep.json.model.impl.JsonLightUser;
import com.nextep.json.model.impl.JsonManyToOneMessageList;
import com.nextep.json.model.impl.JsonMedia;
import com.nextep.json.model.impl.JsonMessage;
import com.nextep.json.model.impl.JsonOneToOneMessageList;
import com.nextep.json.model.impl.JsonOverviewElement;
import com.nextep.json.model.impl.JsonPlace;
import com.nextep.json.model.impl.JsonSpecialEvent;
import com.nextep.json.model.impl.JsonUser;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.proto.builders.JsonBuilder;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.helpers.SearchHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.DistanceDisplayService;
import com.nextep.proto.services.EventManagementService;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.FacetCategory;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.model.FacetCount;

/**
 * A class that helps with JSON creation
 * 
 * @author cfondacci
 * 
 */
public class JsonBuilderImpl implements JsonBuilder {

	private static final Log LOGGER = LogFactory.getLog(JsonBuilderImpl.class);
	private DistanceDisplayService distanceDisplayService;
	private EventManagementService eventManagementService;
	private String baseUrl;

	private JsonBuilderImpl() {
	}

	@Override
	public JsonOverviewElement buildJsonOverview(Locale l, CalmObject o) {
		final JsonOverviewElement elt = new JsonOverviewElement(o.getKey()
				.toString());
		elt.setName(DisplayHelper.getName(o));

		// Place specific information
		if (o instanceof Place) {
			final Place p = (Place) o;
			elt.setAddress(p.getAddress1());
			elt.setType(p.getPlaceType());
			elt.setCity(p.getCity().getName());

			// Filling hours
			final List<? extends EventSeries> events = p.get(EventSeries.class);
			if (events != null) {
				final Collection<JsonHour> hours = buildJsonHours(events);
				elt.setHours(hours);
			}
		}

		// Iterating over descriptions to generate JSON description bean
		final List<? extends Description> descriptions = o
				.get(Description.class);
		Description desc = getDescriptionForLanguage(descriptions, l);
		if (desc == null || desc.getDescription() == null
				|| desc.getDescription().trim().isEmpty()) {
			desc = getDescriptionForLanguage(descriptions, null);
		}
		if (desc != null) {
			elt.setDescription(desc.getDescription());
			elt.setDescriptionKey(desc.getKey().toString());
			elt.setDescriptionLanguage(desc.getLocale().getLanguage());
		} else {
			elt.setDescriptionLanguage(l.getLanguage());
		}
		// Iterating over tags to generate JSON tag beans
		final List<? extends Tag> tags = o.get(Tag.class);
		for (Tag t : tags) {
			elt.addTag(t.getCode());
		}

		return elt;
	}

	private Description getDescriptionForLanguage(
			List<? extends Description> descriptions, Locale l) {
		Description selectedDesc = null;
		for (Description d : descriptions) {
			if (l == null
					|| d.getLocale().getLanguage().equals(l.getLanguage())) {
				selectedDesc = d;
			}
		}

		return selectedDesc;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public JsonMedia buildJsonMedia(Media m, boolean highRes) {
		final JsonMedia jsonMedia = new JsonMedia();
		final String url = highRes ? m.getMobileUrlHighDef() : m.getMobileUrl();
		if (url != null) {
			jsonMedia.setUrl(baseUrl + url);
			final String thumbUrl = highRes ? m.getThumbUrl() : m
					.getMiniThumbUrl();
			jsonMedia.setThumbUrl(baseUrl + thumbUrl);
			// jsonMedia.setTitle(m.getTitle());
			jsonMedia.setKey(m.getKey().toString());
			return jsonMedia;
		} else {
			return null;
		}
	}

	@Override
	public void fillJsonLikeFacets(Locale locale, JsonOverviewElement elt,
			FacetInformation f) {
		final List<JsonFacet> jsonFacets = buildJsonFacets(locale, f);
		// Filling our overview bean
		elt.setLikesFacets(jsonFacets);
	}

	@Override
	public void fillJsonUserFacets(Locale locale, JsonOverviewElement elt,
			FacetInformation f) {
		final List<JsonFacet> jsonFacets = buildJsonFacets(locale, f);
		// Filling our overview bean
		elt.setUsersFacets(jsonFacets);
	}

	private List<JsonFacet> buildJsonFacets(Locale locale, FacetInformation f) {
		// So far, we only extract tags facets, we'll see if more is needed in
		// the future
		final FacetCategory category = SearchHelper.getTagFacetCategory();
		final List<FacetCount> tagsFacets = f.getFacetCounts(category);
		// Preparing our JSON bean list
		final List<JsonFacet> jsonFacets = new ArrayList<JsonFacet>();
		// Building...
		for (FacetCount tagFacet : tagsFacets) {
			final String facetCode = tagFacet.getFacet().getFacetCode();
			// Building our JSON bean
			final JsonFacet jsonFacet = new JsonFacet(facetCode,
					tagFacet.getCount());
			jsonFacets.add(jsonFacet);
		}
		return jsonFacets;
	}

	@Override
	public JsonUser buildJsonUser(User user, boolean highRes, Locale l) {
		final JsonUser json = new JsonUser();
		json.setKey(user.getKey().toString());
		json.setPseudo(user.getPseudo());
		json.setBirthDateValue(user.getBirthday());
		json.setHeightInCm(user.getHeightInCm());
		json.setNxtpUserToken(user.getToken());
		json.setWeightInKg(user.getWeightInKg());
		final List<? extends Message> unreadMessages = user.get(Message.class);
		json.setUnreadMsgCount(unreadMessages.size());
		json.setOnline(user.getOnlineTimeout().getTime() > System
				.currentTimeMillis());

		// Filling city
		try {
			final City city = user.getUnique(City.class);
			if (city != null) {
				json.setCity(city.getName());
			}
		} catch (CalException e) {
			LOGGER.error("Unable to extract city from user '" + user.getKey()
					+ "'");
		}
		// Filling descriptions
		final List<? extends Description> descriptions = user
				.get(Description.class);
		for (Description description : descriptions) {
			// If no locale provided we fill all descriptions, otherwise
			// we filter the ones with the requested language
			if (l == null
					|| l.getLanguage().equals(
							description.getLocale().getLanguage())) {
				// Initializing JSON bean
				final JsonDescription jsonDesc = new JsonDescription();
				jsonDesc.setKey(description.getKey().toString());
				jsonDesc.setLanguage(description.getLocale().getLanguage());
				jsonDesc.setDescription(description.getDescription());
				json.addDescriptions(jsonDesc);
			}
		}
		// Filling media
		for (Media m : user.get(Media.class)) {
			final JsonMedia jsonMedia = buildJsonMedia(m, highRes);
			if (jsonMedia != null) {
				json.addMedia(jsonMedia);
			}
		}
		// Filling user tags
		for (Tag tag : user.get(Tag.class)) {
			json.addTag(tag.getCode());
		}

		// Setting last location
		if (user.getLastLocationKey() != null) {
			try {
				// Extracting last location
				final Place lastLocation = user.getUnique(Place.class,
						Constants.APIS_ALIAS_USER_LOCATION);
				if (lastLocation != null) {
					final Date lastLocationTime = user.getLastLocationTime();

					// Converting to JSON place
					final JsonLightPlace jsonUserPlace = buildJsonLightPlace(
							lastLocation, highRes, l);

					// Injecting into JSON user bean
					json.setLastLocation(jsonUserPlace);
					json.setLastLocationTimeValue(lastLocationTime);
				}
			} catch (CalException e) {
				LOGGER.error(
						"Unable to extract user's last location : "
								+ e.getMessage(), e);
			}
		}
		return json;
	}

	/**
	 * Builds a JSON light user bean from a CAL {@link User}
	 * 
	 * @param user
	 *            the CAL {@link User} bean containing all user info
	 * @return the JSON bean
	 */
	@Override
	public JsonLightUser buildJsonLightUser(User user, boolean highRes,
			Locale locale) {
		JsonLightUser jsonUser = new JsonLightUser();
		jsonUser.setKey(user.getKey().toString());
		jsonUser.setPseudo(user.getPseudo());
		final Media userMedia = MediaHelper.getSingleMedia(user);
		if (userMedia != null) {
			final JsonMedia jsonMedia = buildJsonMedia(userMedia, highRes);
			if (jsonMedia != null) {
				jsonUser.setThumb(jsonMedia);
			}
		}
		jsonUser.setLastLocationTimeValue(user.getLastLocationTime());
		jsonUser.setOnline(user.getOnlineTimeout().getTime() > System
				.currentTimeMillis());
		return jsonUser;
	}

	@Override
	public JsonOneToOneMessageList buildJsonOneToOneMessages(
			List<? extends Message> messages, boolean highRes, Locale l,
			User fromUser, User toUser) {

		// Building from and to users
		final JsonLightUser jsonFrom = buildJsonLightUser(fromUser, highRes, l);
		final JsonLightUser jsonTo = buildJsonLightUser(toUser, highRes, l);

		// Initializing resulting structure
		final JsonOneToOneMessageList messagesList = new JsonOneToOneMessageList();
		messagesList.setFromUser(jsonFrom);
		messagesList.setToUser(jsonTo);

		Collections.reverse(messages);
		// Building the message list
		for (Message message : messages) {
			final JsonMessage json = buildJsonMessage(message);
			messagesList.addMessage(json);
		}
		return messagesList;
	}

	private JsonMessage buildJsonMessage(Message message) {
		final JsonMessage json = new JsonMessage();
		json.setKey(message.getKey().toString());
		json.setFromKey(message.getFromKey().toString());
		json.setToKey(message.getToKey().toString());
		json.setTime(message.getMessageDate());
		json.setMessage(message.getMessage());
		return json;
	}

	@Override
	public JsonManyToOneMessageList buildJsonManyToOneMessages(
			List<? extends Message> messages, boolean highRes, Locale l,
			User currentUser) {

		final Map<ItemKey, JsonLightUser> msgUsersMap = new HashMap<ItemKey, JsonLightUser>();
		final JsonManyToOneMessageList messagesList = new JsonManyToOneMessageList();

		// Processing messages
		for (Message message : messages) {
			// Building JSON message bean
			final JsonMessage json = buildJsonMessage(message);
			// Adding message to list
			messagesList.addMessage(json);

			// Building the map of referenced users
			try {
				final User user = message.getUnique(User.class);
				if (user != null) {
					// Have we already built this user ?
					JsonLightUser jsonUser = msgUsersMap.get(user.getKey());
					if (jsonUser == null) {
						// If no, we build it and add it to our map
						jsonUser = buildJsonLightUser(user, highRes, l);
						msgUsersMap.put(user.getKey(), jsonUser);
					}
				}
			} catch (CalException e) {
				LOGGER.error("Unable to retrieve from/to user of message "
						+ message.getKey() + ": " + e.getMessage(), e);
			}
		}

		// Filling users definition
		for (JsonLightUser jsonUser : msgUsersMap.values()) {
			messagesList.addUser(jsonUser);
		}
		return messagesList;
	}

	@Override
	public JsonManyToOneMessageList buildJsonMessagesFromComments(
			List<? extends Comment> comments, boolean highRes, Locale l) {

		final Map<ItemKey, JsonLightUser> msgUsersMap = new HashMap<ItemKey, JsonLightUser>();
		final JsonManyToOneMessageList messagesList = new JsonManyToOneMessageList();

		for (Comment comment : comments) {
			// Building a JSON message from our comment
			final JsonMessage msg = new JsonMessage();
			msg.setKey(comment.getKey().toString());
			msg.setFromKey(comment.getAuthorItemKey().toString());
			msg.setToKey(comment.getCommentedItemKey().toString());
			msg.setTime(comment.getDate());
			msg.setMessage(comment.getMessage());

			// Adding to our message list
			messagesList.addMessage(msg);

			// Building the map of referenced users
			try {
				final User user = comment.getUnique(User.class);
				if (user != null) {
					// Have we already built this user ?
					JsonLightUser jsonUser = msgUsersMap.get(user.getKey());
					if (jsonUser == null) {
						// If no, we build it and add it to our map
						jsonUser = buildJsonLightUser(user, highRes, l);
						msgUsersMap.put(user.getKey(), jsonUser);
					}
				}
			} catch (CalException e) {
				LOGGER.error("Unable to retrieve from/to user of comment "
						+ comment.getKey() + ": " + e.getMessage(), e);
			}

		}

		// Filling users definition
		for (JsonLightUser jsonUser : msgUsersMap.values()) {
			messagesList.addUser(jsonUser);
		}
		return messagesList;
	}

	@Override
	public JsonLightPlace buildJsonLightPlace(GeographicItem place,
			boolean highRes, Locale l) {
		final JsonLightPlace jsonPlace = new JsonLightPlace();
		jsonPlace.setKey(place.getKey().toString());
		jsonPlace.setName(place.getName());
		final Media placeMedia = MediaHelper.getSingleMedia(place);
		if (placeMedia != null) {
			final JsonMedia jsonMedia = buildJsonMedia(placeMedia, highRes);
			if (jsonMedia != null) {
				jsonPlace.setThumb(jsonMedia);
			}
		}
		return jsonPlace;
	}

	@Override
	public void fillJsonEvent(IJsonLightEvent e, Event event, boolean highRes,
			Locale l, ApiResponse response) {

		e.setName(event.getName());
		e.setKey(event.getKey().toString());
		if (!(event instanceof EventSeries)) {
			e.setStartTime(event.getStartDate());
			e.setEndTime(event.getEndDate());
		}

		// Extract the place
		try {
			final GeographicItem place = event.getUnique(GeographicItem.class,
					Constants.APIS_ALIAS_EVENT_PLACE);
			if (place != null) {
				JsonLightPlace jsonPlace = buildJsonLightPlace(place, highRes,
						l);
				e.setPlace(jsonPlace);

				// We compute series date here because we need timezone from
				// extracted place
				if (event instanceof EventSeries) {
					final EventSeries series = (EventSeries) event;
					String timezoneId = TimeZone.getDefault().getID();
					if (place instanceof City) {
						timezoneId = ((City) place).getTimezoneId();
					} else if (place instanceof Place) {
						timezoneId = ((Place) place).getCity().getTimezoneId();
					}
					final Date nextStart = eventManagementService.computeNext(
							series, timezoneId, true);
					final Date nextEnd = eventManagementService.computeNext(
							series, timezoneId, false);
					e.setStartTime(nextStart);
					e.setEndTime(nextEnd);
				}
			}
		} catch (CalException ex) {
			LOGGER.error("Unable to get Place for event " + event.getKey()
					+ ": " + ex.getMessage(), ex);
		}

		// Distance
		if (response != null) {
			final ItemKey eventKey = event.getKey();
			final String distanceString = distanceDisplayService
					.getDistanceFromItem(eventKey, response, l);
			e.setDistance(distanceString);
			final SearchStatistic distanceStat = response.getStatistic(
					eventKey, SearchStatistic.DISTANCE);
			if (distanceStat != null) {
				e.setRawDistance(distanceStat.getNumericValue().doubleValue());
			}
		}
		// Image & thumb
		for (Media media : event.get(Media.class)) {
			final JsonMedia jsonMedia = buildJsonMedia(media, highRes);
			if (jsonMedia != null) {
				e.addMedia(jsonMedia);
			}
		}

		// Descriptions
		if (e instanceof JsonEvent) {
			// Iterating over descriptions to generate JSON description bean
			final List<? extends Description> descriptions = event
					.get(Description.class);
			Description desc = getDescriptionForLanguage(descriptions, l);
			if (desc == null || desc.getDescription() == null
					|| desc.getDescription().trim().isEmpty()) {
				desc = getDescriptionForLanguage(descriptions, null);
			}
			if (desc != null) {
				((JsonEvent) e).setDescription(desc.getDescription());
			}
		}

		// Participants
		if (response != null) {
			final FacetInformation facetInfo = response
					.getFacetInformation(SearchScope.EVENTS);

			Map<String, Integer> likedEventsMap = SearchHelper.unwrapFacets(
					facetInfo, SearchHelper.getUserEventsCategory());
			final Integer likes = likedEventsMap.get(event.getKey().toString());
			if (likes != null) {
				e.setParticipants(likes);
			}
		}
	}

	public void setDistanceDisplayService(
			DistanceDisplayService distanceDisplayService) {
		this.distanceDisplayService = distanceDisplayService;
	}

	public void setEventManagementService(
			EventManagementService eventManagementService) {
		this.eventManagementService = eventManagementService;
	}

	@Override
	public JsonPlace buildJsonPlace(Place place, boolean highRes, Locale l) {
		final Map<String, Integer> emptyMap = Collections.emptyMap();
		return buildJsonPlace(place, highRes, l, emptyMap, emptyMap);
	}

	@Override
	public JsonPlace buildJsonPlace(Place place, boolean highRes, Locale l,
			Map<String, Integer> likedPlacesMap,
			Map<String, Integer> currentPlacesMap) {
		final JsonPlace p = new JsonPlace(DisplayHelper.getName(place));

		p.setAddress(place.getAddress1());
		p.setCity(DisplayHelper.getName(place.getCity()));
		// p.setDescription(searchSupport.getResultDescription(o));
		p.setClosedReportsCount(place.getClosedCount());

		// Image & thumb
		final Media m = MediaHelper.getSingleMedia(place);
		if (m != null) {
			final JsonMedia jsonMedia = buildJsonMedia(m, highRes);
			if (jsonMedia != null) {
				p.setThumb(jsonMedia);
			}
		}
		for (Media media : place.get(Media.class)) {
			if (media != m) {
				final JsonMedia jsonMedia = buildJsonMedia(media, highRes);
				p.addOtherImage(jsonMedia);
				// We select as main image one with higher height for proper
				// mobile display
				// if (media.getWidth() < media.getHeight()
				// && m.getWidth() > m.getHeight()) {
				// final JsonMedia previousMainMedia = p.getThumb();
				// p.setThumb(jsonMedia);
				// p.addOtherImage(previousMainMedia);
				// } else if (jsonMedia != null) {
				// p.addOtherImage(jsonMedia);
				// }
			}
		}
		// Misc info
		p.setItemKey(place.getKey().toString());
		p.setLat(place.getLatitude());
		p.setLng(place.getLongitude());
		p.setType(place.getPlaceType());

		// Injecting tags
		for (Tag tag : place.get(Tag.class)) {
			p.addTag(tag.getCode());
		}

		// Injecting counts
		final Integer likes = likedPlacesMap.get(place.getKey().toString());
		if (likes != null) {
			p.setLikesCount(likes);
		}
		final Integer users = currentPlacesMap.get(place.getKey().toString());
		if (users != null) {
			p.setUsersCount(users);
		}

		// Injecting advertising sponsors
		final List<? extends AdvertisingBooster> adBoosters = place
				.get(AdvertisingBooster.class);
		if (adBoosters != null && !adBoosters.isEmpty()) {
			// If we have several boosters, we take the highest booster
			int maxBoostValue = 0;
			for (AdvertisingBooster adBooster : adBoosters) {
				if (adBooster.getBoostValue() > maxBoostValue) {
					maxBoostValue = adBooster.getBoostValue();
				}
			}
			// Assigning the highest boost value
			p.setBoostValue(maxBoostValue);
		}

		// Injecting specials
		final List<? extends EventSeries> events = place.get(EventSeries.class);
		// if (localizedDate == null) {
		// localizedDate = eventManagementService.getLocalizedNow(place
		// .getCity().getTimezoneId());
		// }
		// Preparing map of next dates by calendar types (because several
		// series could be defined for a same type
		Map<CalendarType, JsonSpecialEvent> eventsTypeMap = new HashMap<CalendarType, JsonSpecialEvent>();
		final String timezoneId = place.getCity().getTimezoneId();
		for (EventSeries series : events) {
			final Date nextStart = eventManagementService.computeNext(series,
					timezoneId, true);
			if (nextStart != null) {
				final Date nextEnd = eventManagementService.computeNext(series,
						timezoneId, false);

				// Getting any other event registered under same calendar
				// type
				JsonSpecialEvent event = eventsTypeMap.get(series
						.getCalendarType());
				// Creating if not already existing
				if (event == null) {
					event = new JsonSpecialEvent();
					event.setType(series.getCalendarType().name());
					eventsTypeMap.put(series.getCalendarType(), event);
				}

				// Filling with info on the soonest events to come
				final long nextStartTime = nextStart.getTime() / 1000;
				final long nextEndTime = nextEnd.getTime() / 1000;

				// Soonest event to end will define the name
				if (event.getNextEnd() == null
						|| event.getNextEnd() > nextEndTime) {
					event.setNextEnd(nextEndTime);
					event.setName(series.getName());
					event.setKey(series.getKey().toString());
				}
				if (event.getNextStart() == null
						|| event.getNextStart() > nextStartTime) {
					event.setNextStart(nextStartTime);
				}

				// Computing opening hours string
				final String hours = eventManagementService
						.buildReadableTimeframe(series, l);
				String description = event.getDescription() == null ? ""
						: event.getDescription() + " / ";
				event.setDescription(description + hours);

				// Processing series own media
				final List<? extends Media> media = series.get(Media.class);
				if (!media.isEmpty()) {
					final JsonMedia jsonMedia = buildJsonMedia(media.iterator()
							.next(), highRes);
					event.setThumb(jsonMedia);
				}
			}
		}
		// Now we inject all specials (one by type)
		for (CalendarType calType : eventsTypeMap.keySet()) {
			final JsonSpecialEvent e = eventsTypeMap.get(calType);
			p.addSpecial(e);
		}
		// We're done with JSON
		return p;
	}

	@Override
	public JsonActivity buildJsonActivity(Activity activity, boolean highRes,
			Locale l) {
		final JsonActivity json = new JsonActivity();
		User user = null;
		Place activityPlace = null;
		User activityUser = null;
		try {
			user = activity
					.getUnique(User.class, Constants.ALIAS_ACTIVITY_USER);
			activityPlace = activity.getUnique(Place.class,
					Constants.ALIAS_ACTIVITY_TARGET);
			activityUser = activity.getUnique(User.class,
					Constants.ALIAS_ACTIVITY_TARGET);
		} catch (CalException e) {
			LOGGER.error(
					"Unable to extract info from activity " + activity.getKey()
							+ " : " + e.getMessage(), e);
		}
		if (user != null) {
			final JsonLightUser jsonUser = buildJsonLightUser(user, highRes, l);
			json.setUser(jsonUser);
		}
		if (activityPlace != null) {
			final JsonLightPlace jsonPlace = buildJsonLightPlace(activityPlace,
					highRes, l);
			json.setActivityPlace(jsonPlace);
		}
		if (activityUser != null) {
			final JsonLightUser jsonUser = buildJsonLightUser(activityUser,
					highRes, l);
			json.setActivityUser(jsonUser);
		}
		json.setActivityDateValue(activity.getDate());
		return json;
	}

	@Override
	public JsonLightCity buildJsonLightCity(City city, boolean highRes, Locale l) {
		final JsonLightCity jsonCity = new JsonLightCity();
		jsonCity.setKey(city.getKey().toString());
		jsonCity.setName(city.getName());
		jsonCity.setLatitude(city.getLatitude());
		jsonCity.setLongitude(city.getLongitude());

		// Extracting city media when available
		Media cityMedia = MediaHelper.getSingleMedia(city);
		if (cityMedia != null) {
			final JsonMedia jsonMedia = buildJsonMedia(cityMedia, highRes);
			jsonCity.setMedia(jsonMedia);
		}
		return jsonCity;
	}

	@Override
	public Collection<JsonHour> buildJsonHours(
			Collection<? extends EventSeries> eventSeries) {
		final List<JsonHour> jsonHours = new ArrayList<JsonHour>();

		for (EventSeries event : eventSeries) {
			final JsonHour hour = new JsonHour();
			hour.setKey(event.getKey() != null ? event.getKey().toString()
					: null);
			hour.setName(event.getName());
			hour.setStartHour(event.getStartHour());
			hour.setStartMinute(event.getStartMinute());
			hour.setEndHour(event.getEndHour());
			hour.setEndMinute(event.getEndMinute());
			hour.setMonday(event.isMonday());
			hour.setTuesday(event.isTuesday());
			hour.setWednesday(event.isWednesday());
			hour.setThursday(event.isThursday());
			hour.setFriday(event.isFriday());
			hour.setSaturday(event.isSaturday());
			hour.setSunday(event.isSunday());
			hour.setType(event.getCalendarType().name());
			hour.setRecurrency(event.getWeekOfMonthOffset());
			jsonHours.add(hour);
		}
		return jsonHours;
	}
}
