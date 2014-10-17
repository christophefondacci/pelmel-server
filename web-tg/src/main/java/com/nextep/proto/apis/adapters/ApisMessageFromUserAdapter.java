package com.nextep.proto.apis.adapters;

import com.nextep.messages.model.Message;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ApisMessageFromUserAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Message) {
			return ((Message) object).getFromKey();
		} else {
			return null;
		}
	}

}
