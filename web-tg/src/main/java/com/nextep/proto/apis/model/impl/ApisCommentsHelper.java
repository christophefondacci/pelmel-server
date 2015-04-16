package com.nextep.proto.apis.model.impl;

import com.nextep.comments.model.Comment;
import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.apis.adapters.ApisCommentUserKeyAdapter;
import com.nextep.proto.model.Constants;
import com.nextep.tags.model.Tag;
import com.nextep.tags.model.impl.TagRequestTypeFor;
import com.nextep.tags.model.impl.TagTypeRequestType;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.ApisItemKeyAdapter;
import com.videopolis.apis.model.ForCriterion;
import com.videopolis.apis.model.ListCriterion;
import com.videopolis.apis.model.WithCriterion;
import com.videopolis.calm.model.ItemKey;

public final class ApisCommentsHelper {

	private static final ApisItemKeyAdapter commentUserKeyAdapter = new ApisCommentUserKeyAdapter();

	private ApisCommentsHelper() {
	}

	public static ListCriterion listCommentTags(String commentedCalType)
			throws ApisException {
		return SearchRestriction.list(Tag.class, new TagTypeRequestType(
				Comment.CAL_ID + "_" + commentedCalType));
	}

	public static WithCriterion getUserTagsFor(ItemKey itemKey)
			throws ApisException {
		return SearchRestriction
				.with(Tag.class, new TagRequestTypeFor(itemKey)).aliasedBy(
						Constants.APIS_ALIAS_USER_ITEM_TAGS);
	}

	public static ForCriterion getCommentsFor(ItemKey commentedKey, int page)
			throws ApisException {
		return (ForCriterion) SearchRestriction
				.forKey(Comment.class, commentedKey.getType(),
						commentedKey.getId(), Constants.MAX_COMMENTS, page)
				.with(Media.class)
				.addCriterion(
						(ApisCriterion) SearchRestriction.adaptKey(
								commentUserKeyAdapter).with(Media.class,
								MediaRequestTypes.THUMB));
	}

	public static ApisCriterion withComments() throws ApisException {
		return (ApisCriterion) SearchRestriction.with(Comment.class,
				Constants.MAX_COMMENTS, 0).addCriterion(
				(ApisCriterion) SearchRestriction.adaptKey(
						commentUserKeyAdapter).with(Media.class,
						MediaRequestTypes.THUMB));
	}
}
