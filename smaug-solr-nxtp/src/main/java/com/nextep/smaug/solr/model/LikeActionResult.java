package com.nextep.smaug.solr.model;

/**
 * A simple interface providing the result of a like action.
 * 
 * @author cfondacci
 * 
 */
public interface LikeActionResult {

	/**
	 * Indicates whether the action was a LIKE or a DISLIKE
	 * 
	 * @return <code>true</code> if we added the user as liker or
	 *         <code>false<code> if we removed him from likers
	 */
	boolean wasLiked();

	/**
	 * Provides the new number of likes after the action was performed
	 * 
	 * @return the new like count
	 */
	int getNewLikeCount();

}
