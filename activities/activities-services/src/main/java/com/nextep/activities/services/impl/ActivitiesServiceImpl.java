package com.nextep.activities.services.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.dao.ActivitiesDao;
import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityRequestTypeFromUser;
import com.nextep.activities.model.ActivityType;
import com.nextep.activities.model.impl.ActivityImpl;
import com.nextep.activities.model.impl.RequestTypeLatestActivities;
import com.nextep.cal.util.helpers.CalHelper;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.RequestType;
import com.videopolis.cals.exception.UnsupportedCalServiceException;
import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.ItemsResponse;
import com.videopolis.cals.model.MultiKeyItemsResponse;
import com.videopolis.cals.model.PaginatedItemsResponse;
import com.videopolis.cals.model.impl.ItemsResponseImpl;
import com.videopolis.cals.model.impl.MultiKeyItemsResponseImpl;
import com.videopolis.cals.model.impl.PaginatedItemsResponseImpl;

public class ActivitiesServiceImpl extends AbstractDaoBasedCalServiceImpl
		implements CalPersistenceService {

	private static final Log LOGGER = LogFactory
			.getLog(ActivitiesServiceImpl.class);

	@Override
	public Class<? extends CalmObject> getProvidedClass() {
		return Activity.class;
	}

	@Override
	public String getProvidedType() {
		return Activity.CAL_TYPE;
	}

	@Override
	public CalmObject createTransientObject() {
		return new ActivityImpl();
	}

	@Override
	public void saveItem(CalmObject object) {
		getCalDao().save(object);
	}

	@Override
	public List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException {
		throw new UnsupportedCalServiceException(
				"Method setItemFor is not supported for Activities");
	}

	@Override
	public MultiKeyItemsResponse getItemsFor(List<ItemKey> itemKeys,
			CalContext context) throws CalException {

		// Querying DAO for activities
		final Map<ItemKey, List<Activity>> activitiesKeyMap = ((ActivitiesDao) getCalDao())
				.getActivitiesFor(itemKeys);

		// Preparing response
		final MultiKeyItemsResponseImpl response = new MultiKeyItemsResponseImpl();

		// Filling response
		for (ItemKey key : activitiesKeyMap.keySet()) {
			final List<Activity> activities = activitiesKeyMap.get(key);
			response.setItemsFor(key, activities);
		}

		// Returning
		return response;
	}

	@Override
	protected ItemsResponse getItemsFor(ItemKey itemKey, CalContext context,
			RequestType requestType) throws CalException {
		if (requestType instanceof ActivityRequestTypeFromUser) {
			final ActivityRequestTypeFromUser userType = (ActivityRequestTypeFromUser) requestType;
			final List<Activity> activities = ((ActivitiesDao) getCalDao())
					.getActivitiesCreatedByUser(itemKey, 30, 0,
							userType.getActivityTypes());
			final ItemsResponseImpl response = new ItemsResponseImpl();
			response.setItems(activities);
			return response;
		} else if (requestType instanceof RequestTypeLatestActivities) {

			// Extracting activity type
			final RequestTypeLatestActivities activityTypeRequestType = (RequestTypeLatestActivities) requestType;
			final ActivityType[] activityTypes = activityTypeRequestType
					.getActivityTypes();
			final int maxActivities = activityTypeRequestType
					.getActivitiesCount();

			// Querying data source through DAO
			final List<Activity> activities = ((ActivitiesDao) getCalDao())
					.getTypedActivitiesFor(itemKey, maxActivities,
							activityTypes);

			// Building response
			final ItemsResponseImpl response = new ItemsResponseImpl();
			response.setItems(activities);
			return response;
		} else {
			LOGGER.warn("Fallbacking from RequestType getItemsFor call to standard getItemsFor");
			return super.getItemsFor(itemKey, context, requestType);
		}
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber,
			RequestType requestType) throws CalException {
		final ActivitiesDao dao = (ActivitiesDao) getCalDao();
		List<Activity> activities = null;
		int activitiesCount = 0;

		if (requestType instanceof ActivityRequestTypeFromUser) {
			final ActivityRequestTypeFromUser userType = (ActivityRequestTypeFromUser) requestType;
			activities = dao.getActivitiesCreatedByUser(itemKey,
					resultsPerPage, pageNumber, userType.getActivityTypes());
			activitiesCount = dao.getActivitiesCreatedByUserCount(itemKey,
					userType.getActivityTypes());
		} else if (requestType instanceof RequestTypeLatestActivities) {

			// Extracting activity type from request type
			final RequestTypeLatestActivities typeRequestType = (RequestTypeLatestActivities) requestType;
			final ActivityType[] activityTypes = typeRequestType
					.getActivityTypes();

			// Querying activities for this type
			activities = dao.getTypedActivitiesFor(itemKey, resultsPerPage,
					pageNumber, activityTypes);
			// Counting activities for this type
			activitiesCount = dao.getTypedActivitiesForCount(itemKey,
					activityTypes);

		} else {
			activities = dao.getItemsFor(itemKey, resultsPerPage, pageNumber);
			activitiesCount = dao.getActivitiesForCount(itemKey);

		}

		// Building response
		final PaginatedItemsResponseImpl response = new PaginatedItemsResponseImpl(
				resultsPerPage, pageNumber);
		response.setItems(activities);
		response.setItemCount(activitiesCount);
		response.setPageCount(CalHelper.getPageCount(resultsPerPage,
				activitiesCount));
		return response;
	}

	@Override
	public PaginatedItemsResponse getPaginatedItemsFor(ItemKey itemKey,
			CalContext context, int resultsPerPage, int pageNumber)
			throws CalException {
		return getPaginatedItemsFor(itemKey, context, resultsPerPage,
				pageNumber, null);
	}
}
