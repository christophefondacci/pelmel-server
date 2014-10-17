package com.nextep.proto.action.model;

import com.nextep.proto.blocks.HeaderSupport;

public interface HeaderAware {

	void setHeaderSupport(HeaderSupport headerSupport);

	HeaderSupport getHeaderSupport();
}
