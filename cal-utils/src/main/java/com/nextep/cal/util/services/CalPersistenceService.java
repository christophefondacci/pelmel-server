package com.nextep.cal.util.services;

import java.util.List;

import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This extension of a CAL service adds common persistence capabilities to a CAL
 * service.
 * 
 * @author cfondacci
 * 
 */
public interface CalPersistenceService {

	/**
	 * Saves the given CAL object
	 * 
	 * @param object
	 *            the {@link CalmObject} to save
	 */
	void saveItem(CalmObject object);

	/**
	 * Registers the association between an internal item and an external
	 * contributed item. The contributed item is the one whose {@link ItemKey}
	 * passed to a getItemsFor call will return the internalItem.
	 * 
	 * @param internalItemKey
	 * @param contributedItemKeys
	 *            an array of all items to bind to the external item
	 */
	List<? extends CalmObject> setItemFor(ItemKey contributedItemKey,
			ItemKey... internalItemKeys) throws CalException;

	/**
	 * Acts as a factory of the CAL object interface designed to be filled by
	 * the caller before calling
	 * {@link CalPersistenceService#saveItem(CalmObject)}
	 * 
	 * @return a {@link CalmObject} instance
	 */
	CalmObject createTransientObject();
}
