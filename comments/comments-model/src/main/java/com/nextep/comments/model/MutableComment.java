package com.nextep.comments.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableComment extends Comment {

	/**
	 * Defines the {@link ItemKey} of the commented element.
	 * 
	 * @param commentedItemKey
	 *            the {@link ItemKey} of commented element
	 */
	void setCommentedItemKey(ItemKey commentedItemKey);

	/**
	 * Sets the comment's date
	 * 
	 * @param date
	 *            comment date
	 */
	void setDate(Date date);

	/**
	 * Sets the author of this comment
	 * 
	 * @param fromKey
	 *            the author's {@link ItemKey}
	 */
	void setAuthorItemKey(ItemKey fromKey);

	/**
	 * Sets the message of this comment
	 * 
	 * @param message
	 *            the comment's text
	 */
	void setMessage(String message);

	/**
	 * Sets the rate given to the element.
	 * 
	 * @param rate
	 *            rate
	 */
	void setRate(Integer rate);

}