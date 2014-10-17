package com.nextep.ratings.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.ratings.dao.RatingsDao;
import com.nextep.ratings.model.Rating;
import com.videopolis.calm.model.ItemKey;

/**
 * Default Implementation of the {@link RatingsDao}
 * 
 * @author cfondacci
 * 
 */
public class RatingsDaoImpl implements RatingsDao {

	@PersistenceContext(unitName = "nextep-ratings")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<Rating> getRatingsFor(ItemKey ratedItemKey,
			List<ItemKey> ratedByList) {
		final Collection<String> keysStr = CalHelper
				.unwrapItemKeys(ratedByList);
		final List<Rating> ratings = entityManager
				.createQuery(
						"from RatingImpl where ratedItemKey=:ratedItemKey and ratedByItemKey in (:ratedByItemKeys)")
				.setParameter("ratedItemKey", ratedItemKey.toString())
				.setParameter("ratedByItemKeys", keysStr).getResultList();
		return ratings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rating> getTotalRatingsFor(List<ItemKey> keys) {
		final Collection<String> keysStr = CalHelper.unwrapItemKeys(keys);
		final List<Rating> ratings = entityManager
				.createQuery("from TotalRatingImpl where itemKey in (:itemKey)")
				.setParameter("itemKey", keysStr).getResultList();
		return ratings;
	}

	@Override
	public void save(Rating rating) {
		if (rating.getKey() == null) {
			entityManager.persist(rating);
		} else {
			entityManager.merge(rating);
		}
	}
}
