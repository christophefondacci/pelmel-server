package com.nextep.proto.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.proto.apis.model.impl.ApisLocalizationHelper;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.SearchStatistic;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiResponse;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;

public class LocalizationServiceImpl implements LocalizationService {

	private final static Log LOGGER = LogFactory
			.getLog(LocalizationServiceImpl.class);
	private static final Log CHECKIN_LOGGER = LogFactory.getLog("CHECKIN");

	private CalPersistenceService usersService;
	private CalPersistenceService activitiesService;
	private CalPersistenceService geoService;
	private SearchPersistenceService searchService;
	private double localizationDistance;
	private long lastSeenMaxTime;

	@Override
	public void localize(User user, List<? extends Place> nearbyPlaces,
			ApiResponse response, double lat, double lng) {
		final MutableUser currentUser = (MutableUser) user;
		if (currentUser != null) {
			ItemKey currentPlaceKey = null;
			ContextHolder.toggleWrite();
			if (!nearbyPlaces.isEmpty()) {
				final Place p = nearbyPlaces.iterator().next();
				final SearchStatistic stat = response.getStatistic(p.getKey(),
						SearchStatistic.DISTANCE);
				if (stat != null) {
					if (stat.getNumericValue().doubleValue() < localizationDistance) {
						currentPlaceKey = p.getKey();
					}
				}

				// Always saving user lat/lng for nearby user-to-user search
				currentUser.setLatitude(lat);
				currentUser.setLongitude(lng);
				// Storing user localization info (lat, long and place)
				usersService.saveItem(user);
				searchService.updateUserOnlineStatus(currentUser);

				// If localized, we store the place
				// if (currentPlaceKey != null) {
				// checkin(currentUser, currentPlaceKey,
				// ActivityType.LOCALIZATION, lat, lng);
				// }
			}
			if (response instanceof ApiCompositeResponse) {
				try {
					final City city = ((ApiCompositeResponse) response)
							.getUniqueElement(
									City.class,
									ApisLocalizationHelper.APIS_ALIAS_CITY_NEARBY);
					final City currentCity = user.getUnique(City.class);
					if (city != null) {
						geoService.setItemFor(user.getKey(), city.getKey());
					}
					// Generating an activity when the city changes
					if (city != null && currentCity != null
							&& !city.getKey().equals(currentCity.getKey())) {
						final MutableActivity activity = (MutableActivity) activitiesService
								.createTransientObject();
						activity.setActivityType(ActivityType.CITY_CHANGE);
						activity.setUserKey(currentUser.getKey());
						activity.setDate(new Date());
						activity.setLoggedItemKey(city.getKey());
						activitiesService.saveItem(activity);
					}
				} catch (ApisException e) {
					LOGGER.error(
							"Unable to bind user's localized city for user "
									+ user.getKey(), e);
				} catch (CalException e) {
					LOGGER.error(
							"Unable to bind user's localized city for user "
									+ user.getKey(), e);
				}
			}

		}
	}

	@Override
	public void checkin(MutableUser user, ItemKey placeKey,
			ActivityType activityType, double lat, double lng) {
		checkinOrOut(user, placeKey, activityType, lat, lng, false);
	}

	@Override
	public void checkout(MutableUser user, ItemKey placeKey,
			ActivityType activityType, double lat, double lng) {
		checkinOrOut(user, placeKey, activityType, lat, lng, true);
	}

	private void checkinOrOut(MutableUser user, ItemKey placeKey,
			ActivityType activityType, double lat, double lng, boolean checkout) {
		if (!checkout) {
			user.setLastLocationKey(placeKey);
			user.setLastLocationTime(new Date());
		} else {
			// Only checking out if user is already checked in at this place
			if (user.getLastLocationKey().equals(placeKey)) {
				user.setLastLocationKey(null);
			} else {
				// Logging
				LOGGER.warn("Attempt to checkout from a place where the user is not: userKey='"
						+ user.getKey()
						+ "' / placeKeyToCheckout='"
						+ placeKey
						+ "' / currentPlaceKey='"
						+ user.getLastLocationKey()
						+ "'");
			}
		}
		user.setLatitude(lat);
		user.setLongitude(lng);

		// Adding activity
		final MutableActivity activity = (MutableActivity) activitiesService
				.createTransientObject();
		activity.setActivityType(activityType);
		activity.setUserKey(user.getKey());
		activity.setDate(user.getLastLocationTime());
		activity.setLoggedItemKey(placeKey);
		activitiesService.saveItem(activity);
		searchService.updateUserOnlineStatus(user);

		// Saving user
		usersService.saveItem(user);

		// Logging lat/lng with checkin
		CHECKIN_LOGGER.info(placeKey + ";"
				+ (checkout ? "CHECKOUT" : "CHECKIN") + ";"
				+ System.currentTimeMillis() + ";" + user.getKey() + ";" + lat
				+ ";" + lng);
	}

	public void setActivitiesService(CalPersistenceService activitiesService) {
		this.activitiesService = activitiesService;
	}

	public void setUsersService(CalPersistenceService userService) {
		this.usersService = userService;
	}

	public void setSearchService(SearchPersistenceService searchService) {
		this.searchService = searchService;
	}

	public void setLocalizationDistance(double localizationDistance) {
		this.localizationDistance = localizationDistance;
	}

	public void setLastSeenMaxTime(long lastSeenMaxTime) {
		this.lastSeenMaxTime = lastSeenMaxTime;
	}

	public void setGeoService(CalPersistenceService geoService) {
		this.geoService = geoService;
	}
}
