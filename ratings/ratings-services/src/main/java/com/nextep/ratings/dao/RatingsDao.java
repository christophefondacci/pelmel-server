package com.nextep.ratings.dao;

import java.util.List;

import com.nextep.ratings.model.Rating;
import com.videopolis.calm.model.ItemKey;

public interface RatingsDao {

	List<Rating> getTotalRatingsFor(List<ItemKey> keys);

	List<Rating> getRatingsFor(ItemKey ratedItemKey, List<ItemKey> ratedByList);

	void save(Rating rating);
}
