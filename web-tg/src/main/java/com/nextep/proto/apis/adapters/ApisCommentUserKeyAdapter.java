package com.nextep.proto.apis.adapters;

import com.nextep.comments.model.Comment;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This adapter provides the user of a comment
 * 
 * @author cfondacci
 * 
 */
public class ApisCommentUserKeyAdapter implements ApisItemKeyAdapter {

	@Override
	public ItemKey getItemKey(CalmObject object) throws ApisException {
		if (object instanceof Comment) {
			return ((Comment) object).getAuthorItemKey();
		}
		return null;
	}

}
