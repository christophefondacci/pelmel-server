package com.nextep.proto.apis.model.impl;

import com.nextep.comments.model.Comment;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.apis.adapters.ApisCommentUserKeyAdapter;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.proto.model.Constants;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.calm.model.ItemKey;

public class ApisCommentsCriterionFactory implements ApisCriterionFactory {

	private static final ApisItemKeyAdapter commentUserKeyAdapter = new ApisCommentUserKeyAdapter();

	@Override
	public ApisCriterion createApisCriterion(ItemKey itemKey)
			throws ApisException {
		return (ApisCriterion) SearchRestriction.with(Comment.class,
				Constants.MAX_COMMENTS, 0).addCriterion(
				(ApisCriterion) SearchRestriction.adaptKey(
						commentUserKeyAdapter).with(Media.class,
						MediaRequestTypes.THUMB));
	}
}
