package com.nextep.proto.services;

import java.util.List;

import com.nextep.activities.model.ActivityType;
import com.nextep.geo.model.Place;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.model.ItemKey;

/**
 * This service handles the registration and data updates required to enable
 * localization features. Methods of this service will mainly be called from
 * mobile actions and are meant to update data for everyone.
 * 
 * @author cfondacci
 * 
 */
public interface LocalizationService {

	/**
	 * Localizes the user using the provided latitude and longitude. A list of
	 * nearby places might be provided in which case the user will be placed in
	 * the nearest place that does not exceed the maximum localization radius.
	 * Search indexes and user data will be updated accordingly, time outs will
	 * be registered.<br>
	 * This function should be called every time an identified user connects
	 * through a mobile device.
	 * 
	 * @param user
	 *            the {@link User}
	 * @param nearbyPlaces
	 *            a list of nearby places, or <code>null</code> / empty if no
	 *            proximity or unknown
	 * @param response
	 *            the {@link ApiResponse} which provides distance information
	 *            between user and places
	 * @param lat
	 *            user's latitude
	 * @param lng
	 *            user's longitude
	 */
	void localize(User user, List<? extends Place> nearbyPlaces,
			ApiResponse response, double lat, double lng);

	/**
	 * Checkin the given user to the given place
	 * 
	 * @param user
	 *            user to check in
	 * @param placeKey
	 *            {@link ItemKey} of the place to checkin in
	 * @param activityType
	 *            the type of activity to add
	 * @param lat
	 *            current user latitude
	 * @param lng
	 *            current user longitude
	 */
	void checkin(MutableUser user, ItemKey placeKey, ActivityType activityType,
			double lat, double lng);
}
