package com.nextep.activities.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.dao.ActivitiesDao;
import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.model.base.AbstractCalDao;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.helper.Assert;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.model.RequestSettings;

public class ActivitiesDaoImpl extends AbstractCalDao<Activity> implements
		ActivitiesDao {

	private static final Log LOGGER = LogFactory
			.getLog(ActivitiesDaoImpl.class);
	private static final int DEFAULT_RESULTS_COUNT = 30;

	@PersistenceContext(unitName = "nextep-activities")
	private EntityManager entityManager;

	@Override
	public Activity getById(long id) {
		try {
			return (Activity) entityManager
					.createQuery("from ActivityImpl where id=:id")
					.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			LOGGER.error("Unable to get activity from unique id " + id);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> getByIds(List<Long> idList) {
		if (!idList.isEmpty()) {
			return entityManager
					.createQuery("from ActivityImpl where id in (:ids)")
					.setParameter("ids", idList).getResultList();
		} else {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> getItemsFor(ItemKey key, int resultsPerPage,
			int pageOffset) {
		return entityManager
				.createQuery(
						"from ActivityImpl where loggedItemKey=:key and visible=true order by date DESC")
				.setParameter("key", key.toString())
				.setFirstResult(pageOffset * resultsPerPage)
				.setMaxResults(resultsPerPage).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<ItemKey, List<Activity>> getActivitiesFor(List<ItemKey> itemKeys) {

		// Transforming ItemKey list into string-based list
		final Collection<String> loggedItemKeys = CalHelper
				.unwrapItemKeys(itemKeys);

		// Querying all activities
		final List<Activity> activities = entityManager
				.createQuery(
						"from ActivityImpl where loggedItemKey in (:itemKeys) and visible=true order by date DESC")
				.setParameter("itemKeys", loggedItemKeys).getResultList();

		// Preparing our resulting structure
		final Map<ItemKey, List<Activity>> activitiesKeyMap = new HashMap<ItemKey, List<Activity>>();

		// Processing activities
		for (Activity a : activities) {
			final ItemKey loggedItemKey = a.getLoggedItemKey();
			// Getting our list
			List<Activity> itemActivities = activitiesKeyMap.get(loggedItemKey);
			if (itemActivities == null) {
				// Or creating it
				itemActivities = new ArrayList<Activity>();
				activitiesKeyMap.put(loggedItemKey, itemActivities);
			}
			itemActivities.add(a);
		}

		return activitiesKeyMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> getActivitiesCreatedByUser(ItemKey userKey,
			int resultsPerPage, int pageOffset, ActivityType... activityTypes) {
		final List<String> types = unwrapActivityTypes(activityTypes);
		final Query query = entityManager
				.createQuery(
						"from ActivityImpl where userKey=:key and activityType in (:activityTypes)  and visible=true order by date DESC")
				.setParameter("key", userKey.toString())
				.setParameter("activityTypes", types);
		if (resultsPerPage > 0) {
			query.setFirstResult(pageOffset * resultsPerPage).setMaxResults(
					resultsPerPage);
		}
		return query.getResultList();
	}

	@Override
	public int getActivitiesForCount(ItemKey itemKey) {
		return ((BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from ACTIVITIES where ITEM_KEY=:itemKey and IS_VISIBLE='Y'")
				.setParameter("itemKey", itemKey.toString()).getSingleResult())
				.intValue();
	}

	@Override
	public int getTypedActivitiesForCount(ItemKey itemKey,
			ActivityType... activityTypes) throws CalException {
		Assert.notNull(itemKey,
				"No item key provided for getTypedActivitiesForCount");
		Assert.notNull(activityTypes,
				"No activity types provided for getTypedActivitiesForCount");

		// Building types list
		final List<String> types = unwrapActivityTypes(activityTypes);

		return ((BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from ACTIVITIES where ITEM_KEY=:itemKey and ACTIVITY_TYPE in (:activityTypes) and IS_VISIBLE='Y'")
				.setParameter("itemKey", itemKey.toString())
				.setParameter("activityTypes", types).getSingleResult())
				.intValue();
	}

	@Override
	public int getActivitiesCreatedByUserCount(ItemKey itemKey,
			ActivityType... activityTypes) {
		final List<String> types = unwrapActivityTypes(activityTypes);
		return ((BigInteger) entityManager
				.createNativeQuery(
						"select count(1) from ACTIVITIES where USER_KEY=:itemKey and ACTIVITY_TYPE in (:types) and IS_VISIBLE='Y'")
				.setParameter("itemKey", itemKey.toString())
				.setParameter("types", types).getSingleResult()).intValue();
	}

	@Override
	public List<Activity> getItemsFor(ItemKey key) {
		return getItemsFor(key, DEFAULT_RESULTS_COUNT, 0);
	}

	@Override
	public void save(CalmObject object) {
		if (object.getKey() == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}

	/**
	 * Lists <code>count</code> activities of the specified types.
	 * 
	 * @param count
	 *            number of activities to list
	 * @param activityTypes
	 *            activity type restriction, or <code>null</code> for all
	 * @return the corresponding list of activities
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> listItems(RequestType requestType,
			RequestSettings requestSettings) {
		if (requestType instanceof RequestTypeLatestActivities) {
			final RequestTypeLatestActivities rt = (RequestTypeLatestActivities) requestType;
			// Retrieving request type parameters
			final int activitiesCount = rt.getActivitiesCount();
			final ActivityType[] types = rt.getActivityTypes();
			final List<String> activityTypes = unwrapActivityTypes(types);

			return entityManager
					.createQuery(
							"from ActivityImpl where activityType in (:activityTypes) and visible=true "
									+ (rt.includeUserDirectActivity() ? ""
											: "and loggedItemKey not like 'USER%'")
									+ " order by date desc")
					.setMaxResults(activitiesCount)
					.setParameter("activityTypes", activityTypes)
					.getResultList();
		}
		return Collections.emptyList();
	}

	private List<String> unwrapActivityTypes(ActivityType... types) {
		// Building a string list for SQL param injection
		final List<String> activityTypes = new ArrayList<String>();
		// No type = no filter = all types
		if (types == null || types.length == 0) {
			types = ActivityType.values();
		}
		if (types != null) {
			// Iterating over enum types
			for (ActivityType type : types) {
				// Filling the string list
				activityTypes.add(type.getCode());
			}
		}
		return activityTypes;
	}

	@Override
	public List<Activity> getTypedActivitiesFor(ItemKey itemKey,
			int maxActivitiesCount, ActivityType... activityTypes)
			throws CalException {
		return getTypedActivitiesFor(itemKey, maxActivitiesCount, 0,
				activityTypes);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> getTypedActivitiesFor(ItemKey itemKey,
			Integer resultsPerPage, Integer pageOffset,
			ActivityType... activityTypes) throws CalException {
		Assert.notNull(itemKey);
		Assert.notNull(activityTypes);

		// Building list of types string array for use as SQL argument
		final List<String> typesList = unwrapActivityTypes(activityTypes);

		// Querying all activities
		final Query query = entityManager
				.createQuery(
						"from ActivityImpl where loggedItemKey=:itemKey and activityType in (:activityTypes) and visible=true order by date DESC")
				.setParameter("itemKey", itemKey.toString())
				.setParameter("activityTypes", typesList);

		// Paginating if requested
		if (resultsPerPage != null && pageOffset != null) {
			query.setFirstResult(pageOffset * resultsPerPage).setMaxResults(
					resultsPerPage);
		}

		// Fetching
		final List<Activity> activities = query.getResultList();

		return activities;
	}

	@Override
	public int getCount() {
		return ((BigInteger) entityManager.createNativeQuery(
				"select count(1) from ACTIVITIES where IS_VISIBLE='Y'")
				.getSingleResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> listItems(RequestType requestType, Integer pageSize,
			Integer pageOffset) {
		final Query query = entityManager
				.createQuery("from ActivityImpl where visible=true")
				.setMaxResults(pageSize).setFirstResult(pageOffset);
		//
		return query.getResultList();
	}
}
