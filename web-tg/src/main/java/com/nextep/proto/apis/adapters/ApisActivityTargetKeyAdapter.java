package com.nextep.proto.apis.adapters;

import com.nextep.activities.model.Activity;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ApisActivityTargetKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Activity) {
			return ((Activity) object).getLoggedItemKey();
		}
		return null;
	}

}
