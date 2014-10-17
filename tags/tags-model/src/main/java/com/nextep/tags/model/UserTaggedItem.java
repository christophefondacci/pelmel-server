package com.nextep.tags.model;

import com.videopolis.calm.model.ItemKey;

/**
 * This class is the representation of a tag made on an element by a user
 * 
 * @author cfondacci
 * 
 */
public interface UserTaggedItem {

	/**
	 * User who made the tag
	 * 
	 * @return the user's {@link ItemKey}
	 */
	ItemKey getUserItemKey();

	/**
	 * Tagged element
	 * 
	 * @return the element's {@link ItemKey}
	 */
	ItemKey getTaggedItemKey();

	/**
	 * The Tag
	 * 
	 * @return the {@link Tag}
	 */
	Tag getTag();
}
