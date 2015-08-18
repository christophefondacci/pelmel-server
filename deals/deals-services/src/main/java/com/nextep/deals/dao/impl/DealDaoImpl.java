package com.nextep.deals.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.fgp.deals.model.Deal;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.nextep.deals.dao.DealDao;
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

}
