package com.nextep.proto.services.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.MutableActivity;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Place;
import com.nextep.proto.apis.model.impl.ApisLocalizationHelper;
import com.nextep.proto.services.LocalizationService;
import com.nextep.proto.services.ViewManagementService;
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
	private static final String LOCALIZATION_AUTO_STAT = "LOCALIZATION_AUTO_STAT";
	private static final String STAT_CHECKIN = "CHECKIN";
	private static final String STAT_CHECKOUT = "CHECKOUT";

	private CalPersistenceService usersService;
	private CalPersistenceService activitiesService;
	private CalPersistenceService geoService;
	private SearchPersistenceService searchService;
	@Autowired
	private ViewManagementService viewManagementService;
	private double localizationDistance;
	private long lastSeenMaxTime;

	@Async
	@Override
	public Future<Boolean> localize(User user,
			List<? extends Place> nearbyPlaces, ApiResponse response,
			double lat, double lng) {
		final MutableUser currentUser = (MutableUser) user;
		if (currentUser != null) {
			ItemKey currentPlaceKey = null;
			Place currentPlace = null;
			ContextHolder.toggleWrite();

			// Always saving user lat/lng for nearby user-to-user search
			currentUser.setLatitude(lat);
			currentUser.setLongitude(lng);

			// If we have places around
			if (!nearbyPlaces.isEmpty()) {
				final Place p = nearbyPlaces.iterator().next();
				final SearchStatistic stat = response.getStatistic(p.getKey(),
						SearchStatistic.DISTANCE);
				// We compute if the closest one is under localization distance
				if (stat != null) {
					if (stat.getNumericValue().doubleValue() < localizationDistance) {
						currentPlaceKey = p.getKey();
						currentPlace = p;
					}
				}

				// If localized, we store the place
				if (currentPlaceKey != null) {
					// Assigning statistic place key
					((MutableUser) user).setStatLocationKey(currentPlaceKey);
					viewManagementService.logViewCount(currentPlace,
							currentUser, LOCALIZATION_AUTO_STAT);

					// Logging lat/lng with checkin
					CHECKIN_LOGGER.info(currentPlaceKey + ";"
							+ LOCALIZATION_AUTO_STAT + ";"
							+ System.currentTimeMillis() + ";" + user.getKey()
							+ ";" + lat + ";" + lng);
				}
			}
			// Storing user localization info (lat, long and place)
			usersService.saveItem(user);
			searchService.updateUserOnlineStatus(currentUser);

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
		return new AsyncResult<Boolean>(true);
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

		ContextHolder.toggleWrite();
		activitiesService.saveItem(activity);
		searchService.updateUserOnlineStatus(user);

		// Saving user
		usersService.saveItem(user);

		// Logging lat/lng with checkin
		CHECKIN_LOGGER.info(placeKey + ";"
				+ (checkout ? STAT_CHECKOUT : STAT_CHECKIN) + ";"
				+ System.currentTimeMillis() + ";" + user.getKey() + ";" + lat
				+ ";" + lng);
		viewManagementService.logViewCountByKey(placeKey, user,
				checkout ? STAT_CHECKOUT : STAT_CHECKIN);
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
