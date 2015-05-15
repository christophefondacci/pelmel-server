package com.nextep.advertising.dao;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.AdvertisingBooster;
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

	Map<ItemKey, List<AdvertisingBooster>> getBoostersFor(List<ItemKey> keys);

	Map<ItemKey, List<AdvertisingBooster>> getBoostersForUsers(
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

	List<AdvertisingBanner> getBanners(Collection<ItemKey> itemKeys);
}
