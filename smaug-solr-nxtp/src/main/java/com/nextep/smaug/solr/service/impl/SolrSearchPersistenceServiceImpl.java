package com.nextep.smaug.solr.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.BannerStatus;
import com.nextep.advertising.model.Subscription;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.Admin;
import com.nextep.geo.model.AlternateName;
import com.nextep.geo.model.City;
import com.nextep.geo.model.Country;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.smaug.service.SearchPersistenceService;
import com.nextep.smaug.solr.model.LikeActionResult;
import com.nextep.smaug.solr.model.impl.ActivitySearchItemImpl;
import com.nextep.smaug.solr.model.impl.BannerSearchItemImpl;
import com.nextep.smaug.solr.model.impl.CitiesSearchItemImpl;
import com.nextep.smaug.solr.model.impl.EventSearchItemImpl;
import com.nextep.smaug.solr.model.impl.LikeActionResultImpl;
import com.nextep.smaug.solr.model.impl.PlaceSearchItemImpl;
import com.nextep.smaug.solr.model.impl.SearchItemImpl;
import com.nextep.smaug.solr.model.impl.SearchTextItemImpl;
import com.nextep.smaug.solr.model.impl.UserSearchItemImpl;
import com.nextep.statistic.model.Statistic;
import com.nextep.tags.model.Tag;
import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;
import com.videopolis.calm.model.impl.ExpirableItemKeyImpl;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.exception.SearchException;

/**
 * Default implementation of the {@link SearchPersistenceService}
 * 
 * @author cfondacci
 * 
 */
public class SolrSearchPersistenceServiceImpl implements SearchPersistenceService {

	private final static Log log = LogFactory.getLog(SolrSearchPersistenceServiceImpl.class);
	private final static String TAG_DISPLAY_CHECKBOX = "CHECKBOX";
	private String userSolrUrl;
	private String placesSolrUrl;
	private String eventsSolrUrl;
	private String activitiesSolrUrl;
	@Resource(mappedName = "smaug/masterBannersSolrServer")
	private String bannersSolrUrl;
	private String suggestSolrUrl;
	private String citiesSolrUrl;
	private SolrClient userSolrServer;
	private SolrClient placesSolrServer;
	private SolrClient eventsSolrServer;
	private SolrClient activitiesSolrServer;
	private SolrClient bannersSolrServer;
	private SolrClient suggestSolrServer;
	private SolrClient citiesSolrServer;
	private long lastSeenMaxTime;

	public void init() throws MalformedURLException {
		userSolrServer = new HttpSolrClient(userSolrUrl);
		placesSolrServer = new HttpSolrClient(placesSolrUrl);
		eventsSolrServer = new HttpSolrClient(eventsSolrUrl);
		activitiesSolrServer = new HttpSolrClient(activitiesSolrUrl);
		bannersSolrServer = new HttpSolrClient(bannersSolrUrl);
		suggestSolrServer = new HttpSolrClient(suggestSolrUrl);
		citiesSolrServer = new HttpSolrClient(citiesSolrUrl);
	}

	private <T extends SearchItemImpl> T getSearchItem(SolrClient solrServer, ItemKey itemKey, Class<T> searchClass) {
		// First we fetch user from SOLR
		final SolrQuery query = new SolrQuery("id:" + itemKey.toString());
		QueryResponse response = null;
		try {
			response = solrServer.query(query);
		} catch (SolrServerException | IOException e) {
			throw new SearchException("Cannot fetch user from SOLR: " + e.getMessage(), e);
		}
		final List<T> users = response.getBeans(searchClass);
		// We need 1 and only 1 user
		if (users.size() != 1) {
			log.error(users.size() + " items where fetched from SOLR, expected 1 only.");
			return null;
		}
		return users.iterator().next();
	}

	@Override
	public void storeCalmObject(CalmObject object, SearchScope scope) throws SearchException {
		if (object instanceof User) {
			storeUser((User) object);
		} else if (object instanceof Event) {
			final Event event = (Event) object;
			if (event.isOnline()) {
				storeEvent((Event) object);
			} else {
				remove(object);
			}
		} else if (object instanceof Place) {
			storePlace((Place) object);
		} else if (object instanceof Activity) {
			storeActivity((Activity) object);
		} else if (object instanceof City) {
			storeCity((City) object);
		} else if (object instanceof AdvertisingBanner) {
			storeBanner((AdvertisingBanner) object);
		} else {
			throw new SearchException("Unsupported CAL object to store in index: " + object.getClass());
		}
	}

	private void storeCity(City city) throws SearchException {
		CitiesSearchItemImpl searchItem = new CitiesSearchItemImpl(city.getKey().toString());
		searchItem.setLat(city.getLatitude());
		searchItem.setLng(city.getLongitude());
		saveBean(searchItem, citiesSolrServer);
	}

	private void storeBanner(AdvertisingBanner banner) throws SearchException {
		if (banner.getStatus() != BannerStatus.READY || (banner.getEndValidity() != null
				&& banner.getEndValidity().getTime() < System.currentTimeMillis())) {
			try {
				bannersSolrServer.deleteById(banner.getKey().toString());
				bannersSolrServer.commit();
			} catch (IOException | SolrServerException e) {
				throw new SearchException("Cannot unindex banner with key '" + banner.getKey() + "':" + e.getMessage(),
						e);
			}
		} else {
			BannerSearchItemImpl searchItem = new BannerSearchItemImpl();
			searchItem.setKey(banner.getKey());
			searchItem.setLat(banner.getLatitude());
			searchItem.setLng(banner.getLongitude());
			searchItem.setOwnerItemKey(banner.getOwnerItemKey());
			searchItem.setTargetItemKey(banner.getTargetItemKey());
			saveBean(searchItem, bannersSolrServer);
		}
	}

	@Override
	public void updateWithRemoval(CalmObject object, List<ItemKey> events) {
		if (object instanceof User) {
			final UserSearchItemImpl solrUser = getSearchItem(userSolrServer, object.getKey(),
					UserSearchItemImpl.class);
			if (solrUser != null) {
				solrUser.setEvents(new ArrayList<String>());
				for (ItemKey key : events) {
					solrUser.addEvent(key.toString());
				}
				// Saving bean back to solr
				saveBean(solrUser, userSolrServer);
			}
		}
	}

	@Override
	public LikeActionResult toggleLike(ItemKey liker, ItemKey likedKey) {
		// Liker could only be a user, but we enforce this constraint
		if (User.CAL_TYPE.equals(liker.getType())) {
			final UserSearchItemImpl solrUser = getSearchItem(userSolrServer, liker, UserSearchItemImpl.class);
			if (solrUser != null) {
				List<String> likedKeys = null;

				final String keyType = CalHelper.getKeyType(likedKey);
				// We get the appropriate like collection that we will work on
				if (Place.CAL_TYPE.equals(keyType)) {
					likedKeys = solrUser.getPlaces();
				} else if (User.CAL_TYPE.equals(keyType)) {
					likedKeys = solrUser.getUsers();
				} else if (Event.CAL_ID.equals(keyType) || EventSeries.SERIES_CAL_ID.equals(keyType)) {
					likedKeys = solrUser.getEvents();
				}
				// If we got something
				if (likedKeys != null) {
					final String likedKeyStr = likedKey.toString();
					// Cleaning past expired occurrences if needed
					if (isExpirable(likedKey)) {
						cleanExpiredKeys(likedKeys);
					}
					boolean liked = likedKeys.contains(likedKeyStr);
					if (liked) {
						likedKeys.remove(likedKeyStr);
					} else {
						likedKeys.add(likedKeyStr);
					}

					saveBean(solrUser, userSolrServer);
					// We have toggle the like status, so we return the opposite
					return new LikeActionResultImpl(!liked, likedKeys.size());
				}
			}
		}
		return new LikeActionResultImpl(false, 0);
	}

	private boolean isExpirable(ItemKey itemKey) {
		return itemKey.getType().equals(ExpirableItemKeyImpl.CAL_TYPE);
	}

	private void cleanExpiredKeys(List<String> likedKeys) {
		// Processing each key
		for (String likedKey : new ArrayList<String>(likedKeys)) {

			// Getting expiration time
			if (likedKey.contains("-")) {
				final long expirationTime = Long.valueOf(likedKey.split("-")[1]);

				// Checking if expired
				if (expirationTime < System.currentTimeMillis()) {

					// If so we remove the key
					likedKeys.remove(likedKey);
				}
			}
		}
	}

	@Override
	public void toggleIndex(CalmObject object, int seoIndexed) {
		if (object instanceof Place) {
			// Getting place entry
			final PlaceSearchItemImpl solrPlace = getSearchItem(placesSolrServer, object.getKey(),
					PlaceSearchItemImpl.class);
			if (solrPlace != null) {
				// Setting SEO flag
				solrPlace.setSeoIndexed(seoIndexed);
				// Saving bean
				saveBean(solrPlace, placesSolrServer);
			}
		}
	}

	@Override
	public void updateRating(ItemKey itemKey, int rating) {
		SolrClient server = null;
		final String itemType = itemKey.getType();
		if (Place.CAL_TYPE.equals(itemType)) {
			server = placesSolrServer;
		}

		if (server != null) {
			final PlaceSearchItemImpl solrItem = getSearchItem(server, itemKey, PlaceSearchItemImpl.class);
			solrItem.setRating(rating);
			saveBean(solrItem, server);
		}
	}

	private void storeActivity(Activity activity) throws SearchException {
		if (activity.getActivityType() == ActivityType.CITY_CHANGE) {
			log.info("Skipping SOLR indexation of CITY_CHANGE activitiy");
			return;
		} else if (!activity.isVisible()) {
			// Getting place entry
			if (activity != null) {
				try {
					activitiesSolrServer.deleteById(activity.getKey().toString());
					activitiesSolrServer.commit();
				} catch (SolrServerException | IOException e) {
					// Not deleting an activity is OK
					log.error("Unable to delete activity: " + e.getMessage(), e);
				}
			}
		} else {
			final ActivitySearchItemImpl searchItem = new ActivitySearchItemImpl();
			try {
				final GeographicItem location = activity.getUnique(GeographicItem.class);
				searchItem.setKey(activity.getKey());
				searchItem.setActivityDate(activity.getDate());
				searchItem.setTargetType(activity.getLoggedItemKey().getType());
				if (activity.getUserKey() != null) {
					searchItem.setUserKey(activity.getUserKey().toString());
				}
				if (activity.getLoggedItemKey() != null) {
					searchItem.setPlaceKey(activity.getLoggedItemKey().toString());
				}
				searchItem.setActivityType(activity.getActivityType().getCode());

				// Extracting extra info itemKey (if any) and setting as extra
				// type
				if (activity.getExtraInformation() != null) {
					// If this is a list we don't want it
					if (!activity.getExtraInformation().contains(",")) {
						// Trying to parse an ItemKey
						try {
							final ItemKey itemKey = CalmFactory.parseKey(activity.getExtraInformation());
							// If we succeed we set as extra type
							searchItem.setExtraType(itemKey.getType());
						} catch (CalException e) {
							log.error("Unable to convert extra info '" + activity.getExtraInformation()
									+ "' to itemKey: " + e.getMessage());
						}
					}
				}

				if (location instanceof Place) {
					fillLocalization(searchItem, ((Place) location).getCity());
				} else if (location instanceof City) {
					fillLocalization(searchItem, (City) location);
					searchItem.setLat(((City) location).getLatitude());
					searchItem.setLng(((City) location).getLongitude());
				} else {
					log.error(
							"Activity '" + activity.getKey() + "' does not have PLACE or CITY localization, skipping");
					return;
				}
				if (location instanceof Localized) {
					searchItem.setLat(((Localized) location).getLatitude());
					searchItem.setLng(((Localized) location).getLongitude());
				} else {
					log.warn("Null activity localization for " + activity.getKey().toString());
				}
				// Updating SOLR
				if (location != null) {
					try {
						activitiesSolrServer.addBean(searchItem);
						activitiesSolrServer.commit();
					} catch (SolrServerException e) {
						throw new SearchException("Unable to store calm object: " + e, e);
					} catch (IOException e) {
						throw new SearchException("Unable to store calm object: " + e, e);
					}
				}
			} catch (CalException e) {
				throw new SearchException("Unable to extract activity localization: " + e.getMessage(), e);
			}
		}
	}

	private void storePlace(Place place) throws SearchException {
		if (place.isOnline()) {
			final PlaceSearchItemImpl searchItem = new PlaceSearchItemImpl();
			searchItem.setKey(place.getKey());
			searchItem.setName(place.getName());
			searchItem.setPlaceType(place.getPlaceType());
			// A hack to make hotels always boosted with the lowest boost
			// (unless others paid booster)
			if (place.getPlaceType().equals("hotel")) {
				searchItem.setAdBoostValue(1);
				searchItem.setAdBoostEndDate(new Date(Long.MAX_VALUE));
			}
			searchItem.setSeoIndexed(place.isIndexed() ? 1 : 0);
			final City city = place.getCity();
			fillLocalization(searchItem, city);
			if (place.getLatitude() != 0) {
				searchItem.setLat(place.getLatitude());
			} else {
				// Fallbacking to city latitude if no place latitude found
				if (city.getLatitude() != 0) {
					searchItem.setLat(city.getLatitude());
				} else {
					searchItem.setLat(null);
				}
			}
			if (place.getLongitude() != 0) {
				searchItem.setLng(place.getLongitude());
			} else {
				if (city.getLongitude() != 0) {
					searchItem.setLng(city.getLongitude());
				} else {
					searchItem.setLng(null);
				}
			}
			// Adding tags
			for (Tag tag : place.get(Tag.class)) {
				if (TAG_DISPLAY_CHECKBOX.equals(tag.getDisplayMode())) {
					searchItem.addAmenity(tag.getKey().toString());
				} else {
					searchItem.addTag(tag.getKey().toString());
				}
			}
			// Adding ad boosts
			final List<? extends Subscription> adBoosters = place.get(Subscription.class);
			// TODO: ADBOOST adjust this computation
			// Computing upper date bound and higher price
			Date maxDate = null;
			double maxPrice = 0;
			for (Subscription adBooster : adBoosters) {
				// Adjusting max date
				if (maxDate == null || adBooster.getEndDate().after(maxDate)) {
					maxDate = adBooster.getEndDate();
				}
				// Adjusting max price
				if (adBooster.getPrice().getPrice() > maxPrice) {
					maxPrice = adBooster.getPrice().getPrice();
				}
			}
			if (maxDate != null) {
				searchItem.setAdBoostEndDate(maxDate);
				searchItem.setAdBoostValue((int) maxPrice);
			}
			// Updating place ranking
			try {
				final Statistic stat = place.getUnique(Statistic.class);
				if (stat != null) {
					searchItem.setRating(stat.getRating());
				}
			} catch (CalException e) {
				log.error("Unable to store rating in SOLR for place : " + e.getMessage(), e);
			}

			// Updating SOLR
			try {
				placesSolrServer.addBean(searchItem);
				placesSolrServer.commit();
			} catch (SolrServerException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			} catch (IOException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			}
			storeSuggest(place.getKey(), Arrays.asList(place.getName()), place.getCity());
		} else {
			remove(place);
		}

	}

	@Override
	public void remove(CalmObject object) {
		if (object instanceof Place) {
			log.info("Removing place " + object.getKey() + " from SOLR index");
			// For offline places we remove everything
			try {
				placesSolrServer.deleteById(object.getKey().toString());
				placesSolrServer.commit();
				suggestSolrServer.deleteById(object.getKey().toString());
				suggestSolrServer.commit();
			} catch (SolrServerException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			} catch (IOException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			}
		} else if (object instanceof Activity) {
			log.info("Removing activity " + object.getKey() + " from SOLR index");
			try {
				activitiesSolrServer.deleteById(object.getKey().toString());
			} catch (SolrServerException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			} catch (IOException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			}
		} else if (object instanceof Event) {
			log.info("Removing event " + object.getKey() + " from SOLR index");
			try {
				eventsSolrServer.deleteById(object.getKey().toString());
				eventsSolrServer.commit();
				suggestSolrServer.deleteById(object.getKey().toString());
				suggestSolrServer.commit();
			} catch (SolrServerException | IOException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			}
		}
	}

	private void storeEvent(Event event) throws SearchException {
		final EventSearchItemImpl searchItem = new EventSearchItemImpl();
		final ItemKey locationKey = event.getLocationKey();
		if (locationKey != null) {
			searchItem.setKey(event.getKey());
			searchItem.setName(event.getName());
			// Adding tags
			for (Tag tag : event.get(Tag.class)) {
				searchItem.addTag(tag.getKey().toString());
			}
			City city = null;
			try {
				final Place place = event.getUnique(Place.class);
				if (place != null) {
					city = place.getCity();
					searchItem.setLat(place.getLatitude());
					searchItem.setLng(place.getLongitude());
				} else {
					city = event.getUnique(City.class);
					// When we don't have an exact place, we locate the event
					// at the lat/lng of the city
					if (city != null) {
						searchItem.setLat(city.getLatitude());
						searchItem.setLng(city.getLongitude());
					}
				}
				if (city == null) {
					return;
				}
				fillLocalization(searchItem, city);
			} catch (CalException e) {
				log.error("Unable to get city information for user addition, skipping: " + e.getMessage(), e);
				if (City.CAL_ID.equals(locationKey.getType())) {
					searchItem.setCityId(locationKey.toString());
				}
			}
			if (Place.CAL_TYPE.equals(locationKey.getType())) {
				searchItem.setPlaceId(locationKey.toString());
			}
			searchItem.setStart(event.getStartDate());
			searchItem.setEnd(event.getEndDate());
			try {
				eventsSolrServer.addBean(searchItem);
				eventsSolrServer.commit();
			} catch (SolrServerException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			} catch (IOException e) {
				throw new SearchException("Unable to store calm object: " + e, e);
			}
			storeSuggest(event.getKey(), Arrays.asList(event.getName()), city, event.getEndDate());
		}
	}

	private void storeUser(User user) throws SearchException {
		UserSearchItemImpl searchItem = null;
		// We are not storing or indexing anonymous users
		if (user.isAnonymous()) {
			return;
		}
		// if (user.getKey() != null) {
		// try {
		// searchItem = getUserSearchItem(user.getKey());
		// } catch (SearchException e) {
		// log.error("Cannot fetch an existing user from solr : "
		// + user.getKey());
		// }
		// }
		// if (searchItem == null) {
		searchItem = new UserSearchItemImpl();
		// }
		searchItem.setKey(user.getKey());
		// Adding info
		if (user.getHeightInCm() != null) {
			searchItem.setHeightCM(user.getHeightInCm());
		}
		if (user.getWeightInKg() != null) {
			searchItem.setWeightKg(user.getWeightInKg());
		}
		final Calendar birthDate = Calendar.getInstance();
		if (user.getBirthday() != null) {
			birthDate.setTime(user.getBirthday());
		}
		searchItem.setBirthyear(birthDate.get(Calendar.YEAR));
		searchItem.setOnlineTimeout(user.getOnlineTimeout());
		if (user.getLatitude() != 0) {
			searchItem.setLat(user.getLatitude());
		} else {
			searchItem.setLat(null);
		}
		if (user.getLongitude() != 0) {
			searchItem.setLng(user.getLongitude());
		} else {
			searchItem.setLng(null);
		}
		// Adding tags
		for (Tag tag : user.get(Tag.class)) {
			searchItem.addTag(tag.getKey().toString());
		}
		// Adding places
		final List<? extends Place> places = user.get(Place.class, "favorites");
		if (places != null) {
			for (Place p : places) {
				searchItem.addPlace(p.getKey().toString());
			}
		}
		// Adding events
		final List<? extends Event> events = user.get(Event.class, "favorites");
		if (events != null) {
			for (Event e : events) {
				if (e.getEndDate() == null || e.getEndDate().getTime() > System.currentTimeMillis()) {
					searchItem.addEvent(e.getKey().toString());
				}
			}
		}
		// Adding users
		final List<? extends User> users = user.get(User.class, "favorites");
		if (users != null) {
			for (User u : users) {
				searchItem.addUser(u.getKey().toString());
			}
		}
		// Adding localization information
		City city = null;
		try {
			city = user.getUnique(City.class);
			fillLocalization(searchItem, city);
		} catch (CalException e) {
			log.error("Unable to get city information for user addition, skipping: " + e.getMessage(), e);
		}
		if (user.getLastLocationKey() != null) {
			searchItem.setCurrentPlace(user.getLastLocationKey().toString());
			searchItem.setCurrentPlaceTimeout(user.getLastLocationTime().getTime() + lastSeenMaxTime);
		}
		// If current last location is expired or not existing and we have an
		// automatic localization
		if ((user.getLastLocationTime() == null
				|| (user.getLastLocationTime().getTime() + lastSeenMaxTime) < System.currentTimeMillis())
				&& user.getStatLocationKey() != null) {
			// Then we set the user at the auto localization place for him being
			// counted
			searchItem.setCurrentPlace(user.getStatLocationKey().toString());
			searchItem.setCurrentPlaceTimeout(System.currentTimeMillis() + lastSeenMaxTime);
		}

		// Availability
		searchItem.setAvailable(user.getPushDeviceId() != null && !user.getPushDeviceId().isEmpty());

		// Storing
		try {
			userSolrServer.addBean(searchItem);
			userSolrServer.commit();
		} catch (SolrServerException e) {
			throw new SearchException("Unable to store calm object: " + e, e);
		} catch (IOException e) {
			throw new SearchException("Unable to store calm object: " + e, e);
		} catch (RuntimeException e) {
			throw new SearchException("Unable to store calm object: " + e, e);
		}
		storeSuggest(user.getKey(), Arrays.asList(user.getPseudo()), city);
	}

	private void fillLocalization(SearchItemImpl searchItem, City city) {
		if (city != null) {
			searchItem.setCityId(city.getKey().toString());

			final Admin adm2 = city.getAdm2();
			if (adm2 != null) {
				searchItem.setAdm2(adm2.getKey().toString());
			}
			final Admin adm1 = city.getAdm1();
			if (adm1 != null) {
				searchItem.setAdm1(adm1.getKey().toString());
			}
			final Country country = city.getCountry();
			if (country != null) {
				searchItem.setCountryId(country.getKey().toString());
			}
			if (country.getContinent() != null) {
				searchItem.setContinentId(country.getContinent().getKey().toString());
			}
		}

	}

	@Override
	public void updateUserOnlineStatus(User user) throws SearchException {
		final UserSearchItemImpl solrUser = getSearchItem(userSolrServer, user.getKey(), UserSearchItemImpl.class);
		if (solrUser != null) {
			// Updating timeout
			solrUser.setOnlineTimeout(user.getOnlineTimeout());
			// Updating localization (for mobile devices which will send
			// localization
			// at connect time)
			if (user.getLatitude() != 0) {
				solrUser.setLat(user.getLatitude());
			} else {
				solrUser.setLat(null);
			}
			if (user.getLongitude() != 0) {
				solrUser.setLng(user.getLongitude());
			} else {
				solrUser.setLng(null);
			}
			// Is it a new localization information ?
			Date lastLocationTime = user.getLastLocationTime();
			if (lastLocationTime == null) {
				lastLocationTime = new Date();
			}
			final long lastSeenDate = System.currentTimeMillis() - lastLocationTime.getTime();

			solrUser.setCurrentAutoPlace(null);
			solrUser.setCurrentPlaceTimeout(0);
			if (user.getLastLocationKey() != null && lastSeenDate < lastSeenMaxTime) {
				solrUser.setCurrentPlace(user.getLastLocationKey().toString());
				solrUser.setCurrentPlaceTimeout(user.getLastLocationTime().getTime() + lastSeenMaxTime);
			} else {
				// If current last location is expired or not existing and we
				// have an automatic localization
				solrUser.setCurrentPlace(null);
				// if (user.getStatLocationKey() != null) {
				// // Then we set the user at the auto localization place for
				// // him to be counted
				// solrUser.setCurrentAutoPlace(user.getStatLocationKey()
				// .toString());
				// solrUser.setCurrentPlaceTimeout(System.currentTimeMillis()
				// + lastSeenMaxTime);
				// }
			}

			// Adding localization information
			try {
				final City city = user.getUnique(City.class);
				if (city != null) {
					fillLocalization(solrUser, city);
				}
			} catch (CalException e) {
				log.error("Unable to get city information for user addition, skipping: " + e.getMessage(), e);
			}

			// Saving bean back to solr
			saveBean(solrUser, userSolrServer);
		}
	}

	private void saveBean(Object item, SolrClient server) {
		try {
			server.addBean(item);
			server.commit();
		} catch (SolrServerException e) {
			throw new SearchException("Cannot update user in SOLR: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new SearchException("Cannot update user in SOLR: " + e.getMessage(), e);
		}
	}

	@Override
	public void removeAll(String type) throws SearchException {
		SolrClient server = null;
		if (Place.CAL_TYPE.equals(type)) {
			server = placesSolrServer;
		} else if (User.CAL_TYPE.equals(type)) {
			server = userSolrServer;
		} else if (Activity.CAL_TYPE.equals(type)) {
			server = activitiesSolrServer;
		} else if (AdvertisingBanner.CAL_ID.equals(type)) {
			server = bannersSolrServer;
		} else if (Event.CAL_ID.equals(type)) {
			server = eventsSolrServer;
		}
		if (server != null) {
			try {
				server.deleteByQuery("*:*");
			} catch (IOException e) {
				throw new SearchException("Unable to delete: " + e.getMessage(), e);
			} catch (SolrServerException e) {
				throw new SearchException("Unable to delete: " + e.getMessage(), e);
			}

		}
	}

	@Override
	public void storeSuggest(ItemKey itemKey, List<String> knownNames) {
		storeSuggest(itemKey, knownNames, null);
	}

	private Collection<String> getNameWithAlternates(String name, CalmObject o) {
		final List<? extends AlternateName> alternateNames = o.get(AlternateName.class);
		final Set<String> names = new HashSet<String>();
		names.add(name);
		for (AlternateName alternateName : alternateNames) {
			names.add(alternateName.getAlternameName());
		}
		return names;
	}

	@Override
	public void storeSuggest(ItemKey itemKey, List<String> knownNames, City city) {
		storeSuggest(itemKey, knownNames, city, null);
	}

	public void storeSuggest(ItemKey itemKey, List<String> knownNames, City city, Date expirationDate) {

		final SearchTextItemImpl item = new SearchTextItemImpl();
		item.setId(itemKey.toString());
		item.setNames(knownNames);
		item.setType(itemKey.getType());
		item.setExpirationTime(expirationDate);
		if (city != null) {
			final Collection<String> cityNames = getNameWithAlternates(city.getName(), city);
			item.setCityName(cityNames);
			if (city.getAdm1() != null) {
				final Collection<String> stateNames = getNameWithAlternates(city.getAdm1().getName(), city.getAdm1());
				item.setStateName(stateNames);
			}
			if (city.getCountry() != null) {
				final Collection<String> countryNames = getNameWithAlternates(city.getCountry().getName(),
						city.getCountry());
				item.setCountryName(countryNames);
			}
		}
		try {
			suggestSolrServer.addBean(item);
			suggestSolrServer.commit();
		} catch (IOException e) {
			throw new SearchException("Unable to store suggest for " + itemKey + " : " + e.getMessage());
		} catch (SolrServerException e) {
			throw new SearchException("Unable to store suggest for " + itemKey + " : " + e.getMessage());
		}
	}

	@Override
	public void addBooster(CalmObject object, Date endDate, int boostFactor) throws SearchException {
		if (object instanceof Place) {
			PlaceSearchItemImpl placeItem = getSearchItem(placesSolrServer, object.getKey(), PlaceSearchItemImpl.class);
			if (placeItem != null) {
				placeItem.setAdBoostEndDate(endDate);
				placeItem.setAdBoostValue(boostFactor);
				saveBean(placeItem, placesSolrServer);
				return;
			}
		}
		throw new SearchException("Cannot boost non-place element or place not found for item "
				+ (object == null ? "null" : object.getKey()));
	}

	public void setLastSeenMaxTime(long lastSeenMaxTime) {
		this.lastSeenMaxTime = lastSeenMaxTime;
	}

	public void setUserSolrUrl(String userSolrUrl) {
		this.userSolrUrl = userSolrUrl;
	}

	public void setPlacesSolrUrl(String placesSolrUrl) {
		this.placesSolrUrl = placesSolrUrl;
	}

	public void setEventsSolrUrl(String eventsSolrUrl) {
		this.eventsSolrUrl = eventsSolrUrl;
	}

	public void setActivitiesSolrUrl(String activitiesSolrUrl) {
		this.activitiesSolrUrl = activitiesSolrUrl;
	}

	public void setSuggestSolrUrl(String suggestSolrUrl) {
		this.suggestSolrUrl = suggestSolrUrl;
	}

	public void setCitiesSolrUrl(String citiesSolrUrl) {
		this.citiesSolrUrl = citiesSolrUrl;
	}
}
