package com.nextep.cal.util.services;

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
}
