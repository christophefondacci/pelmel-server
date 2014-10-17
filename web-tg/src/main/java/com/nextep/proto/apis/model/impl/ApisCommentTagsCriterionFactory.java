package com.nextep.proto.apis.model.impl;

import com.nextep.comments.model.Comment;
import com.nextep.proto.apis.model.ApisCriterionFactory;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.impl.TagTypeRequestType;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.calm.model.ItemKey;

public class ApisCommentTagsCriterionFactory implements ApisCriterionFactory {

	private final String commentedCalType;

	public ApisCommentTagsCriterionFactory(String commentedCalType) {
		this.commentedCalType = commentedCalType;
	}

	@Override
	public ApisCriterion createApisCriterion(ItemKey itemKey)
			throws ApisException {
		return SearchRestriction.list(Tag.class, new TagTypeRequestType(
				Comment.CAL_ID + "_" + commentedCalType));

	}
}
