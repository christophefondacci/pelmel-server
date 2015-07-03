package com.nextep.cal.util.services;

import java.util.List;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.service.CalService;

/**
 * This interface extends the base {@link CalPersistenceService} by giving
 * access to deletion features.
 * 
 * @author cfondacci
 *
 */
public interface CalExtendedPersistenceService extends CalPersistenceService {

	/**
	 * Deletes an object from the underlying data store. Depending on the
	 * implementation, the data may only be made invisible to the CAL api. The
	 * contract of this service is that after calling this method any further
	 * call to
	 * {@link CalService#getItems(java.util.List, com.videopolis.cals.model.CalContext)}
	 * or
	 * {@link CalService#getItemsFor(java.util.List, com.videopolis.cals.model.CalContext)}
	 * and every derived method should not return the object.
	 * 
	 * @param objectKey
	 *            the {@link ItemKey} of the object to delete
	 */
	void delete(ItemKey objectKey);

	/**
	 * Registers the association between an internal item and an external
	 * contributed item by specifying the type of connection with a name. The
	 * contributed item is the one whose {@link ItemKey} passed to a getItemsFor
	 * call will return the internalItem. <br>
	 * This method allows to store same type of items in different collections
	 * (for example: a list of users/places that are like VERSUS a list of users
	 * for a private network)
	 * 
	 * @param internalItemKey
	 * @param contributedItemKeys
	 *            an array of all items to bind to the external item
	 */
	List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			String connectionType, ItemKey... internalItemKeys)
			throws CalException;

	/**
	 * Deletes the given association
	 * 
	 * @param contributedItemKey
	 *            the external item key that can be passed to a getItemsFor
	 *            request on a CAL service to get internal objects contributing
	 *            to this key
	 * @param connectionType
	 *            the type of connection to target
	 * @param internalItemKey
	 *            the {@link ItemKey} of internal item currently associated with
	 *            the contributedItemKey
	 * @return <code>true</code> if an item has been deleted or
	 *         <code>false</code> if it was already deleted, up to the caller to
	 *         decide if this is an exception case or not
	 * @throws CalException
	 */
	boolean deleteItemFor(ItemKey contributedItemKey, String connectionType,
			ItemKey internalItemKey) throws CalException;

}
