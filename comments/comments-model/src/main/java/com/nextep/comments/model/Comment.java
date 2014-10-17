package com.nextep.comments.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface represents a comment from someone on an element.
 * 
 * @author cfondacci
 * 
 */
public interface Comment extends CalmObject {

	String CAL_ID = "CMNT";

	/**
	 * Author of this comment
	 * 
	 * @return the {@link ItemKey} of the comment's author
	 */
	ItemKey getAuthorItemKey();

	/**
	 * The unique key of the item being commented
	 * 
	 * @return the commented item's unique key
	 */
	ItemKey getCommentedItemKey();

	/**
	 * The date when this comment was written
	 * 
	 * @return the comment's date
	 */
	Date getDate();

	/**
	 * The message of this comment
	 * 
	 * @return this comment's message
	 */
	String getMessage();

	/**
	 * The rate that the user gave to the commented element.
	 * 
	 * @return the rate, or <code>null</code> if the user has not defined any
	 *         rate
	 */
	Integer getRate();
}
