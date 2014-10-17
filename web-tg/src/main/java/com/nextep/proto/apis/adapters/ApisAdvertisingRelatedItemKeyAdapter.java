package com.nextep.proto.apis.adapters;

import com.nextep.advertising.model.AdvertisingBooster;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This adapter provides access to the related item of an
 * {@link AdvertisingBooster}
 * 
 * @author cfondacci
 * 
 */
public class ApisAdvertisingRelatedItemKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof AdvertisingBooster) {
			return ((AdvertisingBooster) object).getRelatedItemKey();
		}
		return null;
	}

}
