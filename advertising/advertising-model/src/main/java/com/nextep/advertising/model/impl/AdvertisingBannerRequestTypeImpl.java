package com.nextep.advertising.model.impl;

import com.nextep.advertising.model.AdvertisingBannerRequestType;

public class AdvertisingBannerRequestTypeImpl implements
		AdvertisingBannerRequestType {

	private static final long serialVersionUID = 9116032439990766534L;
	private String searchType;

	public AdvertisingBannerRequestTypeImpl(String searchType) {
		this.searchType = searchType;
	}

	@Override
	public String getSearchType() {
		return searchType;
	}

}
