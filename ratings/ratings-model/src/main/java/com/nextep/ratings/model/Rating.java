package com.nextep.ratings.model;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public interface Rating extends CalmObject {

	String CAL_TYPE = "RATE";

	/**
	 * Provides the {@link ItemKey} of the element being rated
	 * 
	 * @return the rated element's item key
	 */
	ItemKey getRatedItemKey();

	/**
	 * Provides the {@link ItemKey} of the entity which made this rating
	 * 
	 * @return the {@link ItemKey} of the element (generally a user) who gave
	 *         this rate
	 * 
	 */
	ItemKey getRatedByItemKey();

	/**
	 * The rate as an integer. Scale is application dependent.
	 * 
	 * @return the rate
	 */
	int getRate();
}
