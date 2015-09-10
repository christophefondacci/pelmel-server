package com.nextep.proto.apis.adapters;

import com.fgp.deals.model.Deal;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Adapts a deal to provide the related item key the deal is connected to
 * 
 * @author cfondacci
 *
 */
public class ApisDealPlaceItemKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Deal) {
			return ((Deal) object).getRelatedItemKey();
		}
		return null;
	}

}
