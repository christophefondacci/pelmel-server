package com.nextep.deals.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.fgp.deals.model.Deal;
import com.fgp.deals.model.DealUse;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.deals.dao.DealDao;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class DealDaoImpl extends AbstractCalDao<Deal>implements DealDao {

	@PersistenceContext(unitName = "nextep-deals")
	private EntityManager entityManager;

	@Override
	public Deal getById(long id) {
		try {
			return (Deal) entityManager.createQuery("from DealImpl where id=:id").setParameter("id", id)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Deal> getItemsFor(ItemKey key) {

		@SuppressWarnings("unchecked")
		final List<Deal> objects = entityManager.createQuery("from DealImpl where relatedItemKey=:key")
				.setParameter("key", key.toString()).getResultList();
		return objects;
	}

	@Override
	public Map<ItemKey, List<Deal>> getDealsFor(List<ItemKey> itemKeys) {

		// Transforming ItemKey list into string-based list
		final Collection<String> loggedItemKeys = CalHelper.unwrapItemKeys(itemKeys);

		// Querying all activities
		@SuppressWarnings("unchecked")
		final List<Deal> deals = entityManager.createQuery("from DealImpl where relatedItemKey in (:itemKeys)")
				.setParameter("itemKeys", loggedItemKeys).getResultList();

		// Preparing our resulting structure
		final Map<ItemKey, List<Deal>> dealsKeyMap = new HashMap<ItemKey, List<Deal>>();

		// Processing activities
		for (Deal deal : deals) {
			final ItemKey loggedItemKey = deal.getRelatedItemKey();
			// Getting our list
			List<Deal> itemDeals = dealsKeyMap.get(loggedItemKey);
			if (itemDeals == null) {
				// Or creating it
				itemDeals = new ArrayList<Deal>();
				dealsKeyMap.put(loggedItemKey, itemDeals);
			}
			itemDeals.add(deal);
		}

		return dealsKeyMap;
	}

	@Override
	public Map<ItemKey, List<DealUse>> getDealUsesFor(List<ItemKey> itemKeys, boolean lastDay) {

		if (!itemKeys.isEmpty()) {

			String queryStr = null;
			final boolean isForUserQuery = itemKeys.iterator().next().getType().equals("USER");
			if (isForUserQuery) {
				queryStr = "from DealUseImpl where consumerItemKey in (:itemKeys)";
			} else {
				queryStr = "from DealUseImpl where deal.id in (:dealIds)";
			}
			if (lastDay) {
				queryStr += " and useTime>:time";
			}
			// Querying all deals used
			final Query query = entityManager.createQuery(queryStr);
			if (isForUserQuery) {
				// Transforming ItemKey list into string-based list
				final Collection<String> consumerItemKeys = CalHelper.unwrapItemKeys(itemKeys);
				query.setParameter("itemKeys", consumerItemKeys);
			} else {
				// Transforming ItemKey list into string-based list
				final Collection<Long> dealIds = CalHelper.unwrapItemKeyIds(itemKeys);
				query.setParameter("dealIds", dealIds);
			}
			if (lastDay) {
				query.setParameter("time", new Date(System.currentTimeMillis() - 86400000));
			}

			@SuppressWarnings("unchecked")
			final List<DealUse> dealUses = query.getResultList();

			// Preparing our resulting structure
			final Map<ItemKey, List<DealUse>> dealsKeyMap = new HashMap<ItemKey, List<DealUse>>();

			// Processing activities
			for (DealUse deal : dealUses) {
				final ItemKey loggedItemKey = isForUserQuery ? deal.getConsumerItemKey() : deal.getDeal().getKey();
				// Getting our list
				List<DealUse> itemDealUses = dealsKeyMap.get(loggedItemKey);
				if (itemDealUses == null) {
					// Or creating it
					itemDealUses = new ArrayList<DealUse>();
					dealsKeyMap.put(loggedItemKey, itemDealUses);
				}
				itemDealUses.add(deal);
			}

			return dealsKeyMap;
		} else {
			return Collections.emptyMap();
		}
	}

	@Override
	public List<DealUse> getDealUses(List<ItemKey> itemKeys) {
		final Collection<Long> itemKeysStr = CalHelper.unwrapItemKeyIds(itemKeys);
		try {
			return entityManager.createQuery("from DealUseImpl where dealUseId in (:id)")
					.setParameter("dealUseId", itemKeys).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public void save(CalmObject object) {
		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}
}
