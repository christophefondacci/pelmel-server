package com.nextep.proto.apis.adapters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.activities.model.Activity;
import com.nextep.activities.model.ActivityType;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

public class ApisActivityExtraKeyAdapter implements ApisItemKeyAdapter {
	private static final Log LOGGER = LogFactory
			.getLog(ApisActivityExtraKeyAdapter.class);

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Activity) {
			final Activity a = (Activity) object;
			if (a.getActivityType() == ActivityType.CREATION) {
				try {
					// Trying to parse extra info (which should contain added
					// item key)
					return CalmFactory.parseKey(a.getExtraInformation());
				} catch (CalException e) {
					LOGGER.error("Unable to get item key from CREATE activity extra info "
							+ a.getKey());
					return null;
				}
			}
		}
		return null;
	}

}
