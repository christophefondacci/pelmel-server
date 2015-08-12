package com.nextep.advertising.dao;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.Subscription;
import com.nextep.advertising.model.Payment;
import com.nextep.cal.util.model.CalDao;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Data access object for adverstising features.
 * 
 * @author cfondacci
 * 
 */
public interface AdvertisingDao extends CalDao<CalmObject> {

	Map<ItemKey, List<Subscription>> getBoostersFor(List<ItemKey> keys);

	Map<ItemKey, List<Subscription>> getBoostersForUsers(
			List<ItemKey> keys);

	/**
	 * Retrieves all payments made by the user with the given key
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the user who made the payments to
	 *            retrieve
	 * @return a list of {@link Payment} or an empty list
	 */
	List<Payment> getPaymentsFor(ItemKey itemKey);

	/**
	 * Provides the banners to display for the given geographic area.
	 * 
	 * @param geoItemKeys
	 *            the {@link ItemKey} of the geographic zone to get banners for
	 * @param searchType
	 *            the current search type
	 * @return
	 */
	Map<ItemKey, List<AdvertisingBanner>> getBannersFor(
			Collection<ItemKey> geoItemKeys, String searchType, Locale locale);

	/**
	 * Provides the banner attached to any of the given item keys
	 * 
	 * @param itemKeys
	 *            the list of {@link ItemKey} of the object that might have
	 *            associated banners (will typically be geographical
	 *            administrative areas
	 * @return the list of {@link AdvertisingBanner} associated with any of the
	 *         provided item keys
	 */
	List<AdvertisingBanner> getBanners(Collection<ItemKey> itemKeys);

	/**
	 * Lists all banners that the user identified by the given {@link ItemKey}
	 * has created
	 * 
	 * @param userKey
	 *            the {@link ItemKey} of the user who created the banners
	 * @return the list of all {@link AdvertisingBanner} that this user created
	 */
	List<AdvertisingBanner> getBannersForUser(ItemKey userKey);
}
