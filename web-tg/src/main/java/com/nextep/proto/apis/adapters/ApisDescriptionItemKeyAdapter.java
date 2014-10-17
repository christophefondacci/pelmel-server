package com.nextep.proto.apis.adapters;

import com.nextep.descriptions.model.Description;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This adapter provides the {@link ItemKey} of the described item allowing to
 * build queries that start from the description and retrieve the associated
 * described item.
 * 
 * @author cfondacci
 * 
 */
public class ApisDescriptionItemKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Description) {
			final Description d = (Description) object;
			return d.getDescribedItemKey();
		}
		return null;
	}
}
