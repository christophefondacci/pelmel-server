package com.nextep.proto.action.model;

import com.nextep.proto.blocks.CommentSupport;
import com.nextep.proto.blocks.SelectableTagSupport;

public interface Commentable {

	void setCommentSupport(CommentSupport commentSupport);

	CommentSupport getCommentSupport();

	SelectableTagSupport getCommentTagSupport();

	void setCommentTagSupport(SelectableTagSupport tagSupport);
}
