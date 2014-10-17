package com.nextep.ratings.model;

import com.videopolis.calm.model.ItemKey;

public interface MutableRating extends Rating {

	void setRatedItemKey(ItemKey ratedItemKey);

	void setRatedByItemKey(ItemKey ratedByItemKey);

	void setRate(int rate);
}
