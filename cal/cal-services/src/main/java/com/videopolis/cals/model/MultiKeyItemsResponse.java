package com.videopolis.cals.model;

import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This response allows to store a combination of {@link CalmObject} collection
 * corresponding to different <u>remote</u> foreign keys.<br>
 * 
 * @author Christophe Fondacci
 * 
 */
public interface MultiKeyItemsResponse extends ItemsResponse {

	/**
	 * Returns the list of {@link CalmObject} items contributing to the
	 * specified external item referenced through its {@link ItemKey}.
	 * 
	 * @param foreignKey
	 *            unique key of the external item
	 * @return a collection of {@link CalmObject} provided by the current CAL
	 *         component contributing to the external item.
	 */
	List<? extends CalmObject> getItemsFor(ItemKey foreignKey);
}
