package com.nextep.proto.apis.adapters;

import com.nextep.activities.model.Activity;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ApisActivityUserAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Activity) {
			return ((Activity) object).getUserKey();
		}
		return null;
	}

}
