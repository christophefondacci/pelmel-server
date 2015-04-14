package com.nextep.proto.services;

import java.util.concurrent.Future;

import com.nextep.geo.model.GeographicItem;
import com.nextep.proto.model.SearchType;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This service factorizes some common management code to count views on a
 * specific page.
 * 
 * @author cfondacci
 * 
 */
public interface ViewManagementService {

	/**
	 * Counts one view on an overview page
	 * 
	 * @param overviewItem
	 *            the overview {@link CalmObject} object
	 * @param user
	 *            the optional {@link User} who viewed this page
	 */
	Future<CalmObject> logViewedOverview(CalmObject overviewItem, User user);

	/**
	 * Counts one view on a search page
	 * 
	 * @param geographicItem
	 *            the {@link GeographicItem} where the search was made
	 * @param searchType
	 *            the {@link SearchType} = the kind of searched elements
	 * @param user
	 *            the optional {@link User} who viewed this page
	 */
	Future<CalmObject> logViewedSearch(GeographicItem geographicItem,
			SearchType searchType, User user);

	/**
	 * Generic view count logging
	 * 
	 * @param viewedObject
	 *            element which has been viewed
	 * @param user
	 *            the user who viewed the eleent
	 * @param viewType
	 *            an optional type information about the context
	 */
	Future<Object> logViewCount(CalmObject viewedObject, User user,
			String viewType);

	/**
	 * Same as
	 * {@link ViewManagementService#logViewCount(CalmObject, User, String)}
	 * except the viewed object is passed via its unique key.
	 * 
	 * @param viewedObjectKey
	 *            the {@link ItemKey} of the viewed object
	 * @param user
	 *            the {@link User} who originated the action
	 * @param viewType
	 *            the type of information to log
	 * @return a future for asynchronous execution
	 */
	Future<Object> logViewCountByKey(ItemKey viewedObjectKey, User user,
			String viewType);
}
