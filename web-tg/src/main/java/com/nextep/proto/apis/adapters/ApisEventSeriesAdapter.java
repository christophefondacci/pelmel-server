package com.nextep.proto.apis.adapters;

import com.nextep.events.model.Event;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Provides the parent series of an event so that one can have access to a
 * series from a child event.
 * 
 * @author cfondacci
 * 
 */
public class ApisEventSeriesAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Event) {
			return ((Event) object).getSeriesKey();
		}
		return null;
	}

}
