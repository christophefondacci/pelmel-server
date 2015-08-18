package com.nextep.proto.apis.adapters;

import com.nextep.advertising.model.Subscription;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ApisSubscriptionRelatedItemKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Subscription) {
			return ((Subscription) object).getRelatedItemKey();
		}
		return null;
	}

}
