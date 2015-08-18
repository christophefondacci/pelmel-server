package com.nextep.statistic.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This items represents the view of an item by a viewer.
 * 
 * @author cfondacci
 * 
 */
public interface ItemView extends CalmObject {

	String CAL_TYPE = "VIEW";

	/**
	 * Provides the unique key of the viewed element.
	 * 
	 * @return the {@link ItemKey} of the viewed element
	 */
	ItemKey getViewedItemKey();

	/**
	 * Provides the unique key of the viewer
	 * 
	 * @return the {@link ItemKey} of the viewer
	 */
	ItemKey getViewerItemKey();

	/**
	 * Provides the time when the viewed element has been viewed by the viewer.
	 * 
	 * @return the {@link Date} when the view occurred
	 */
	Date getViewDate();

	/**
	 * Provides the type of view (depends on the context)
	 * 
	 * @return the view type
	 */
	String getViewType();

	/**
	 * An optional transient count used for "fake" statistics (reports)
	 * 
	 * @return
	 */
	int getCount();
}
