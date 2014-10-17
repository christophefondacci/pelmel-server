package com.nextep.smaug.service;

import java.util.Date;
import java.util.List;

import com.nextep.smaug.solr.model.LikeActionResult;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.smaug.common.model.SearchScope;
import com.videopolis.smaug.exception.SearchException;

/**
 * Provides abstract methods to store information in the search index.
 * 
 * @author cfondacci
 * 
 */
public interface SearchPersistenceService {

	/**
	 * Stores the provided CAL object into the search engine
	 * 
	 * @param object
	 *            the {@link CalmObject} to store, containing all needed
	 *            dependencies
	 * @param scope
	 *            the optional {@link SearchScope} to locate the storage
	 *            location in which the object should be stored
	 */
	void storeCalmObject(CalmObject object, SearchScope scope)
			throws SearchException;

	/**
	 * Refreshes the specified user's online information in the index.
	 * 
	 * @param user
	 *            the {@link User} to refresh
	 * @throws SearchException
	 *             whenever the update failed (a failure will prevent other
	 *             users to properly see the user when setting 'online users
	 *             first' flag
	 */
	void updateUserOnlineStatus(User user) throws SearchException;

	/**
	 * Removes every object of the specified type from indexes. This operation
	 * is very dangerous and should only be used with good care.
	 * 
	 * @param type
	 */
	void removeAll(String type);

	/**
	 * Removes the specified object from the index
	 * 
	 * @param object
	 *            the object to remove
	 */
	void remove(CalmObject object);

	void storeSuggest(ItemKey itemKey, List<String> knownNames);

	/**
	 * Updates the document corresponding to the specified user by removing the
	 * specified collection of elements
	 * 
	 * @param object
	 *            object to update
	 * @param removed
	 *            elements to remove
	 */
	void updateWithRemoval(CalmObject object, List<ItemKey> elements);

	/**
	 * Toggles the like status of the specified liked element for the given
	 * liker
	 * 
	 * @param liker
	 *            the {@link ItemKey} of the liker
	 * @param likedKey
	 *            the {@link ItemKey} of the element to like / unlike
	 * @return information resulting from the like action as a
	 *         {@link LikeActionResult} bean
	 */
	LikeActionResult toggleLike(ItemKey liker, ItemKey likedKey);

	/**
	 * Toggles the SEO index flag of the given element
	 * 
	 * @param object
	 *            the object to alter
	 * @param seoIndexed
	 *            the seo index flag status
	 */
	void toggleIndex(CalmObject object, int seoIndexed);

	/**
	 * Updates the rating of the specified CAL object
	 * 
	 * @param placeKey
	 *            the {@link ItemKey} of the item to update
	 * @param rating
	 *            the rating of the item
	 */
	void updateRating(ItemKey placeKey, int rating);

	/**
	 * Adds a booster to the given CAL object until the given date with the
	 * specified factor. The boost is immediate and replaces any other boost.
	 * 
	 * @param object
	 *            object to boost
	 * @param endDate
	 *            end date of boost
	 * @param boostFactor
	 *            factor of boost
	 */
	void addBooster(CalmObject object, Date endDate, int boostFactor)
			throws SearchException;
}
