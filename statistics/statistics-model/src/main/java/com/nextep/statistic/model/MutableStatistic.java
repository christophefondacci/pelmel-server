package com.nextep.statistic.model;

import com.videopolis.calm.model.ItemKey;

/**
 * Exposes accessors to modify statistics for a given element.
 * 
 * @author cfondacci
 * 
 */
public interface MutableStatistic extends Statistic {

	/**
	 * Defines the item key to which this statistics refers
	 * 
	 * @param itemKey
	 *            related {@link ItemKey}
	 */
	void setItemKey(ItemKey itemKey);

	/**
	 * Defines the rating of the element
	 * 
	 * @param rating
	 */
	void setRating(int rating);

	/**
	 * Defines the view type of this statistic
	 * 
	 * @param viewType
	 */
	void setViewType(String viewType);

}
