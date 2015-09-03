package com.nextep.activities.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.cal.util.model.CalDaoExt;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;

public interface ActivitiesDao extends CalDaoExt<Activity> {

	/**
	 * Provides the list of activities that the user identified by the provided
	 * key has created.
	 * 
	 * @param userKey
	 *            the {@link ItemKey} of the user
	 * @param activityTypes
	 *            the restriction of activity types to filter, empty list or
	 *            null for all
	 * @return the list of his recent activities
	 */
	List<Activity> getActivitiesCreatedByUser(ItemKey userKey, int resultsPerPage, int pageOffset,
			ActivityType... activityTypes);

	/**
	 * Retrieves the total number of activities for this element
	 * 
	 * @param itemKey
	 *            the element to retrieve activities count for
	 * @return the total number of activities
	 */
	int getActivitiesForCount(ItemKey itemKey);

	/**
	 * Retrieves the number of activities of the given type associated with the
	 * given item key.
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the element of which to count
	 *            activities
	 * @param activityTypes
	 *            the set of {@link ActivityType} of activities to count
	 * @return the number of corresponding {@link Activity}
	 */
	int getTypedActivitiesForCount(ItemKey itemKey, ActivityType... activityTypes) throws CalException;

	/**
	 * Retrieves the total number of activities created by this user
	 * 
	 * @param itemKey
	 *            the user's {@link ItemKey}
	 * @param activityTypes
	 *            the restriction of activity types to filter, empty list or
	 *            null for all
	 * @return the total number of activities created by this user
	 */
	int getActivitiesCreatedByUserCount(ItemKey itemKey, ActivityType... activityTypes);

	/**
	 * Retrieves a list of all activities for all the specifed item keys.
	 * 
	 * @param itemKeys
	 *            the list of {@link ItemKey} to get activities for
	 * @return a list of corresponding {@link Activity}, most recent first
	 */
	Map<ItemKey, List<Activity>> getActivitiesFor(List<ItemKey> itemKeys);

	/**
	 * Retrieves the activities of the given element filtered for the activity
	 * type specified and ordered by date desc (most recent first)
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the element to get activities for
	 * @param maxActivitiesCount
	 *            maximum number of activities to return
	 * @param activityTypes
	 *            the set of {@link ActivityType} of activities to retrieve
	 * @return the list of corresponding {@link Activity} sorted by date desc
	 */
	List<Activity> getTypedActivitiesFor(ItemKey itemKey, int maxActivitiesCount, ActivityType... activityTypes)
			throws CalException;

	/**
	 * Same as
	 * {@link ActivitiesDao#getTypedActivitiesFor(ItemKey, ActivityType)} with
	 * pagination
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the element to get activities for
	 * @param resultsPerPage
	 *            number of activities to retrieve
	 * @param pageOffset
	 *            page to start from
	 * @param activityTypes
	 *            the set {@link ActivityType} of activities to retrieve
	 * @return the corresponding activities, up to <code>resultsPerPage</code>
	 * @throws CalException
	 */
	List<Activity> getTypedActivitiesFor(ItemKey itemKey, Integer resultsPerPage, Integer pageOffset,
			ActivityType... activityTypes) throws CalException;

	/**
	 * Lists all activities of the given type for the specified item, starting
	 * at the provided date.
	 * 
	 * @param itemKey
	 *            the {@link ItemKey} of the item to get activities for
	 * @param activityType
	 *            the {@link ActivityType} of the activities to fetch
	 * @param fromDate
	 *            the date to start looking activities from
	 * @return the list of all {@link Activity}
	 */
	List<Activity> getActivitiesFor(ItemKey itemKey, ActivityType activityType, Date fromDate) throws CalException;
}
