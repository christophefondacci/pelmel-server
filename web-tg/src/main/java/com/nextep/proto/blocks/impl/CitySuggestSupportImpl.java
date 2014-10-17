package com.nextep.proto.blocks.impl;

import com.nextep.proto.blocks.SuggestSupport;

public class CitySuggestSupportImpl implements SuggestSupport {

	@Override
	public String getSuggestUrl() {
		return "javascript:lookup('/ajaxSuggestCity.action?cityName=' + this.value)";
	}

}
