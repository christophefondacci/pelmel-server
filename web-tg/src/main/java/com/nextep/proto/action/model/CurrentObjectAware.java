package com.nextep.proto.action.model;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Implemented by actions able to provide the current object. This is used by
 * many tools that can be applied on the current action (adding a media, adding
 * a review, etc.)
 * 
 * @author cfondacci
 * 
 */
public interface CurrentObjectAware {

	/**
	 * Provides access to the current object unique key.
	 * 
	 * @return the current {@link CalmObject}'s {@link ItemKey}
	 */
	ItemKey getCurrentObjectKey();

}
