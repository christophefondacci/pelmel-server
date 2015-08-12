package com.nextep.advertising.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.advertising.dao.AdvertisingDao;
import com.nextep.advertising.model.AdvertisingBanner;
import com.nextep.advertising.model.Payment;
import com.nextep.advertising.model.Subscription;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.CalDaoExt;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;

public class AdvertisingDaoImpl extends AbstractCalDao<CalmObject>implements AdvertisingDao, CalDaoExt<CalmObject> {

	private static final Log LOGGER = LogFactory.getLog(AdvertisingDaoImpl.class);

	@PersistenceContext(unitName = "nextep-advertising")
	private EntityManager entityManager;

	@Override
	public CalmObject getById(long id) {
		try {
			return (CalmObject) entityManager.createQuery("from SubscriptionImpl where id=:id").setParameter("id", id)
					.getSingleResult();
		} catch (NonUniqueResultException e) {
			LOGGER.error("Non unique result: " + e.getMessage(), e);
			return null;
		} catch (NoResultException e) {
			LOGGER.warn("Booster with ID " + id + " not found");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CalmObject> getItemsFor(ItemKey key) {
		return entityManager
				.createQuery(
						"from SubscriptionImpl where relatedItemKey=:itemKey and fromDate>=:currentDate and toDate<:currentDate")
				.setParameter("itemKey", key.toString()).setParameter("currentDate", new Date()).getResultList();
	}

	@Override
	public Map<ItemKey, List<Subscription>> getBoostersFor(List<ItemKey> itemKeys) {
		final List<String> itemKeysStr = new ArrayList<String>();
		for (ItemKey itemKey : itemKeys) {
			itemKeysStr.add(itemKey.toString());
		}

		final List<Subscription> adBoosters = entityManager
				.createQuery(
						"from SubscriptionImpl where relatedItemKey in (:itemKeys) and fromDate<=:currentDate and toDate>:currentDate")
				.setParameter("itemKeys", itemKeysStr).setParameter("currentDate", new Date()).getResultList();

		final Map<ItemKey, List<Subscription>> adBoostersKeyMap = new HashMap<ItemKey, List<Subscription>>();
		for (Subscription adBooster : adBoosters) {
			final ItemKey itemKey = adBooster.getRelatedItemKey();
			List<Subscription> adBoostersForKey = adBoostersKeyMap.get(itemKey);
			// Initializing list
			if (adBoostersForKey == null) {
				adBoostersForKey = new ArrayList<Subscription>();
				adBoostersKeyMap.put(itemKey, adBoostersForKey);
			}
			adBoostersForKey.add(adBooster);
		}
		return adBoostersKeyMap;
	}

	@Override
	public Map<ItemKey, List<Subscription>> getBoostersForUsers(List<ItemKey> itemKeys) {
		final List<String> itemKeysStr = new ArrayList<String>();
		for (ItemKey itemKey : itemKeys) {
			itemKeysStr.add(itemKey.toString());
		}
		final List<Subscription> adBoosters = entityManager
				.createQuery("from SubscriptionImpl where purchaserItemKey in (:itemKeys)")
				.setParameter("itemKeys", itemKeysStr).getResultList();
		final Map<ItemKey, List<Subscription>> adBoostersKeyMap = new HashMap<ItemKey, List<Subscription>>();
		for (Subscription adBooster : adBoosters) {
			final ItemKey itemKey = adBooster.getPurchaserItemKey();
			List<Subscription> adBoostersForUserKey = adBoostersKeyMap.get(itemKey);
			// Initializing list
			if (adBoostersForUserKey == null) {
				adBoostersForUserKey = new ArrayList<Subscription>();
				adBoostersKeyMap.put(itemKey, adBoostersForUserKey);
			}
			adBoostersForUserKey.add(adBooster);
		}
		return adBoostersKeyMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Payment> getPaymentsFor(ItemKey itemKey) {
		return entityManager.createQuery("from PaymentImpl where userKey=:userKey")
				.setParameter("userKey", itemKey.toString()).getResultList();
	}

	@Override
	public void save(CalmObject object) {

		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}

	@Override
	public Map<ItemKey, List<AdvertisingBanner>> getBannersFor(Collection<ItemKey> geoItemKeys, String searchType,
			Locale locale) {
		final Collection<String> keys = CalHelper.unwrapItemKeys(geoItemKeys);

		@SuppressWarnings("unchecked")
		final List<AdvertisingBanner> banners = entityManager
				.createQuery(
						"from AdvertisingBannerImpl where topGeographicItemKey in (:geoItemKey) and (searchType is null or searchType=:searchType) and (locale is null or locale=:locale) and status!='DELETED' order by display_count asc")
				.setParameter("geoItemKey", keys).setParameter("searchType", searchType)
				.setParameter("locale", locale.getLanguage()).getResultList();

		final Map<ItemKey, List<AdvertisingBanner>> bannersMap = new HashMap<ItemKey, List<AdvertisingBanner>>();
		// Building a map of banners hashed by their geographic belonging
		for (AdvertisingBanner banner : banners) {
			final ItemKey geoItemKey = banner.getTopGeographicItemKey();
			List<AdvertisingBanner> geoBanners = bannersMap.get(geoItemKey);
			if (geoBanners == null) {
				// Registering new list of banners under this geographic item
				// key
				geoBanners = new ArrayList<AdvertisingBanner>();
				bannersMap.put(geoItemKey, geoBanners);
			}
			// Adding the current banner
			geoBanners.add(banner);
		}
		return bannersMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<AdvertisingBanner> getBannersForUser(ItemKey userKey) {
		final List<AdvertisingBanner> banners = entityManager
				.createQuery(
						"from AdvertisingBannerImpl where ownerItemKey=:itemKey and status!='DELETED' order by startValidity desc")
				.setParameter("itemKey", userKey.toString()).getResultList();
		return banners;
	}

	@Override
	public List<AdvertisingBanner> getBanners(Collection<ItemKey> itemKeys) {
		if (!itemKeys.isEmpty()) {
			final Collection<Long> keys = CalHelper.unwrapItemKeyIds(itemKeys);

			@SuppressWarnings("unchecked")
			final List<AdvertisingBanner> banners = entityManager
					.createQuery("from AdvertisingBannerImpl where id in (:itemKeys)").setParameter("itemKeys", keys)
					.getResultList();

			return banners;
		} else {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CalmObject> listItems(RequestType requestType, Integer pageSize, Integer pageOffset) {

		List<CalmObject> banners = entityManager.createQuery("from AdvertisingBannerImpl").setMaxResults(pageSize)
				.setFirstResult(pageOffset * pageSize).getResultList();

		return banners;
	}

	@Override
	public int getCount() {
		final BigInteger count = (BigInteger) entityManager.createNativeQuery("select count(1) from ADS_BANNERS")
				.getSingleResult();
		return count.intValue();
	}

	@Override
	public int getListItemsCount(RequestType requestType) {
		return getCount();
	}
}
