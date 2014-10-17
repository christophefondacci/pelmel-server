package com.nextep.proto.apis.adapters;

import com.nextep.media.model.Media;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Adapts a media to provide its parent item key.
 * 
 * @author cfondacci
 * 
 */
public class ApisMediaParentKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Media) {
			return ((Media) object).getRelatedItemKey();
		}
		return null;
	}

}
