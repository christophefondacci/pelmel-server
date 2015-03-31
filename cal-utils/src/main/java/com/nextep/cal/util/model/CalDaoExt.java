package com.nextep.cal.util.model;

import java.util.List;

import com.nextep.cal.util.services.base.AbstractDaoBasedCalServiceImpl;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.RequestType;

/**
 * An extension to the generic CAL DAO interface providing methods that supports
 * abstract paginated lists implementation through the
 * {@link AbstractDaoBasedCalServiceImpl} class. Implementing the
 * {@link CalDaoExt} interface while providing a subclass of
 * {@link AbstractDaoBasedCalServiceImpl} will provide automatic support for all
 * list service methods.
 * 
 * @author cfondacci
 *
 * @param <T>
 */
public interface CalDaoExt<T extends CalmObject> extends CalDao<T> {

	/**
	 * Provides the window elements for this request time, starting at the given
	 * page offset, and returning up to pageSize items.
	 * 
	 * @param requestType
	 *            the {@link RequestType} that may provide information on how
	 *            the request should be made
	 * @param pageSize
	 *            the max number of elements to return
	 * @param pageOffset
	 *            the page offset (will start at pageOffset x pageSize)
	 * @return the requested elements
	 */
	List<T> listItems(RequestType requestType, Integer pageSize,
			Integer pageOffset);

	/**
	 * @return the number of elements
	 * @deprecated implementations should now use the
	 *             {@link CalDaoExt#getListItemsCount(RequestType)} method with
	 *             the {@link RequestType} argument
	 * 
	 */
	@Deprecated
	int getCount();

	/**
	 * Provides the total number of elements for a list request with the given
	 * request type. Since request types may alter the actual query being made
	 * for the list query, the count need this information as well to provide
	 * the corresponding count. This is the reason why
	 * {@link CalDaoExt#getCount()} should no longer be used and implementation
	 * should provide count according to the request type.
	 * 
	 * @param requestType
	 *            the {@link RequestType} passed to the
	 *            {@link CalDaoExt#listItems(RequestType, Integer, Integer)}
	 *            method
	 * @return the total number of elements for a list query with this request
	 *         type
	 */
	int getListItemsCount(RequestType requestType);
}
