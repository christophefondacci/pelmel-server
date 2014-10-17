package com.nextep.statistic.model;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This statistic bean contains information regarding a specific CAL object
 * referenced through its item key.
 * 
 * @author cfondacci
 * 
 */
public interface Statistic extends CalmObject {

	String CAL_TYPE = "STAT";

	/**
	 * Provides the {@link ItemKey} of the element holding the statistics
	 * 
	 * @return the {@link ItemKey}
	 */
	ItemKey getItemKey();

	/**
	 * Provides the number of views for this element
	 * 
	 * @return the number of views
	 */
	int getViewsCount();

	/**
	 * Provides the rating for this element
	 * 
	 * @return the element's rating
	 */
	int getRating();

	/**
	 * Provides the view type for this statistic (for example, in the context of
	 * city pages, the view type is the type of the listed items).
	 * 
	 * @return the view type
	 */
	String getViewType();

}
