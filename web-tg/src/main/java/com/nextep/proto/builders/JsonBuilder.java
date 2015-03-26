package com.nextep.proto.builders;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.activities.model.Activity;
import com.nextep.comments.model.Comment;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.City;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.json.model.IJsonLightEvent;
import com.nextep.json.model.impl.JsonActivity;
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
import com.nextep.json.model.impl.JsonUser;
import com.nextep.media.model.Media;
import com.nextep.messages.model.Message;
import com.nextep.users.model.User;
import com.videopolis.apis.model.FacetInformation;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.CalmObject;

/**
 * This builder centralized and facilitates building of JSON object from CAL
 * model objects.
 * 
 * @author cfondacci
 * 
 */
public interface JsonBuilder {

	/**
	 * Builds a JSON overview bean from a CAL bean.
	 * 
	 * @param o
	 *            the {@link CalmObject} of the object of the overview
	 * @return the JSON bean as a {@link JsonOverviewElement}
	 */
	JsonOverviewElement buildJsonOverview(Locale l, CalmObject o,
			boolean highRes);

	/**
	 * Builds a JSON media bean from a CAL media bean
	 * 
	 * @param m
	 *            the {@link Media} to convert to JSON
	 * @param highRes
	 *            whether or not the media will be displayed on a high
	 *            resolution screen
	 * @return the {@link JsonMedia} bean
	 */
	JsonMedia buildJsonMedia(Media m, boolean highRes);

	/**
	 * Fills the JSON facets for likes from facet information
	 * 
	 * @param locale
	 *            the {@link Locale} to use for translation
	 * @param f
	 *            the {@link FacetInformation} from which we can extract facets
	 */
	void fillJsonLikeFacets(Locale locale, JsonOverviewElement elt,
			FacetInformation f);

	/**
	 * Fills the JSON facets for users from facet information
	 * 
	 * @param locale
	 *            the {@link Locale} to use for translation
	 * @param f
	 *            the {@link FacetInformation} from which we can extract facets
	 */
	void fillJsonUserFacets(Locale locale, JsonOverviewElement elt,
			FacetInformation f);

	/**
	 * Builds a JSON user bean from a {@link User} bean
	 * 
	 * @param user
	 *            the {@link User} bean to convert to JSON bean
	 * @param highRes
	 *            whether or not media should be in high resolution
	 * @param l
	 *            locale to use to filter localized content, or
	 *            <code>null</code> for no filtering
	 * @return the {@link JsonUser} bean
	 */
	JsonUser buildJsonUser(User user, boolean highRes, Locale l);

	JsonUser buildJsonUser(User user, boolean highRes, Locale l,
			ApiResponse response);

	JsonActivity buildJsonActivity(Activity activity, boolean highRes, Locale l);

	/**
	 * Builds a JSON place bean from a {@link Place} bean
	 * 
	 * @param place
	 *            the {@link Place} to convert to JSON
	 * @param highRes
	 *            whether or not we need high resolution images
	 * @param l
	 *            locale to filter localized content
	 * @return the {@link JsonPlace} bean
	 */
	JsonPlace buildJsonPlace(Place place, boolean highRes, Locale l);

	JsonPlace buildJsonPlace(Place place, boolean highRes, Locale l,
			Map<String, Integer> likedPlaceMap,
			Map<String, Integer> currentPlacesMap,
			Map<String, Integer> eventUsersMap);

	void fillJsonEvent(IJsonLightEvent e, Event event, boolean highRes,
			Locale l, ApiResponse response);

	/**
	 * Builds a JSON message bean from a message bean
	 * 
	 * @param message
	 *            the {@link Message} CAL object
	 * @param highRes
	 *            the high resolution flag for any media in this message
	 * @param l
	 *            the {@link Locale} information
	 * @return the {@link JsonOneToOneMessageList} bean
	 */
	JsonOneToOneMessageList buildJsonOneToOneMessages(
			List<? extends Message> messages, boolean highRes, Locale l,
			User currentUser, User otherUser);

	/**
	 * Builds a JSON many to one message list wrapper containing the definition
	 * of every referenced users along with the list of JSON message beans.
	 * 
	 * @param messages
	 *            the messages to build in JSON format
	 * @param highRes
	 *            the high resolution flag for any media of this message / user
	 *            thumbs
	 * @param l
	 *            the {@link Locale} info
	 * @param currentUser
	 *            current user
	 * @return the {@link JsonManyToOneMessageList} bean
	 */
	JsonManyToOneMessageList buildJsonManyToOneMessages(
			List<? extends Message> messages, boolean highRes, Locale l,
			User currentUser);

	JsonManyToOneMessageList buildJsonMessagesFromComments(
			List<? extends Comment> comments, boolean highRes, Locale l);

	/**
	 * Converts a {@link Message} CAL object to its JSON representation
	 * 
	 * @param message
	 *            the {@link Message} instance to convert
	 * @return the corresponding {@link JsonMessage}
	 */
	JsonMessage buildJsonMessage(Message message);

	JsonLightUser buildJsonLightUser(User user, boolean highRes, Locale l);

	/**
	 * Builds a JSON light place bean from a given place
	 * 
	 * @param place
	 *            the {@link Place} bean to use to create JSON bean
	 * @param highRes
	 *            whether or not media should be provided in high resolution
	 * @param l
	 *            the Locale to use for textual contents
	 * @return the Json light bean
	 */
	JsonLightPlace buildJsonLightPlace(GeographicItem place, boolean highRes,
			Locale l);

	/**
	 * Builds a JSON light city from a city bean
	 * 
	 * @param city
	 *            the {@link City} to convert to JSON
	 * @param highRes
	 *            high resolution images flag
	 * @param l
	 *            current {@link Locale}
	 * @return the JSON light representation of the city
	 */
	JsonLightCity buildJsonLightCity(City city, boolean highRes, Locale l);

	/**
	 * Translates a collection of {@link EventSeries} into a collection of
	 * serializable {@link JsonHour} information.
	 * 
	 * @param eventSeries
	 *            the collection of events to translate, as {@link EventSeries}
	 *            beans
	 * @param l
	 *            {@link Locale} to use for descriptions
	 * @return a corresponding collection of {@link JsonHour} beans
	 */
	Collection<JsonHour> buildJsonHours(
			Collection<? extends EventSeries> eventSeries, City eventCity,
			Locale l);

}
