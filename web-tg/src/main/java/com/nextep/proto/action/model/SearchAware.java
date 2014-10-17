package com.nextep.proto.action.model;

import com.nextep.proto.blocks.SearchSupport;
import com.nextep.proto.model.SearchType;

public interface SearchAware {

	SearchSupport getSearchSupport();

	void setSearchSupport(SearchSupport searchSupport);

	void setSearchType(SearchType searchType);

	SearchType getSearchType();

}
