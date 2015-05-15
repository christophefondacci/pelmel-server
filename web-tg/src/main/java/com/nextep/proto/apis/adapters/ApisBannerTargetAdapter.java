package com.nextep.proto.apis.adapters;

import com.nextep.advertising.model.AdvertisingBanner;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ApisBannerTargetAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof AdvertisingBanner) {
			return ((AdvertisingBanner) object).getTargetItemKey();
		}
		return null;
	}

}
