package com.nextep.proto.action.model;

import com.nextep.proto.blocks.CurrentUserSupport;

public interface CurrentUserAware {

	CurrentUserSupport getCurrentUserSupport();

	void setCurrentUserSupport(CurrentUserSupport currentUserSupport);
}
