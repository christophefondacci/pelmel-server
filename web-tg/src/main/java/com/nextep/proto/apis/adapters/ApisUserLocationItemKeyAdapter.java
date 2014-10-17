package com.nextep.proto.apis.adapters;

import com.nextep.users.model.User;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Exposes the key of the last location of the user traversing this adapter.
 * 
 * @author cfondacci
 * 
 */
public class ApisUserLocationItemKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof User) {
			return ((User) object).getLastLocationKey();
		}
		return null;
	}

}
