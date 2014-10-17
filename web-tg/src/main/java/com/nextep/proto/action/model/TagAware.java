package com.nextep.proto.action.model;

import com.nextep.proto.blocks.TagSupport;

public interface TagAware {

	void setTagSupport(TagSupport tagSupport);

	TagSupport getTagSupport();
}
