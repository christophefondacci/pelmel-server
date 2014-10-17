package com.nextep.proto.model;

public enum SearchType {

	NEWS(UrlConstants.SEARCH_TYPE_NEWS), MEN(UrlConstants.SEARCH_TYPE_USERS), BARS(
			"bar", UrlConstants.SEARCH_TYPE_BARS), SAUNAS("sauna",
			UrlConstants.SEARCH_TYPE_SAUNAS), CLUBS("club",
			UrlConstants.SEARCH_TYPE_CLUB), SEXCLUBS("sexclub",
			UrlConstants.SEARCH_TYPE_SEXCLUB), SHOPS("sexshop",
			UrlConstants.SEARCH_TYPE_SHOP), RESTAURANTS("restaurant",
			UrlConstants.sEARCH_TYPE_RESTAURANT), ASSOCIATIONS("asso",
			UrlConstants.SEARCH_TYPE_ASSOCIATION), EVENTS(
			UrlConstants.SEARCH_TYPE_EVENTS), HOTELS("hotel",
			UrlConstants.SEARCH_TYPE_HOTELS), MAP(UrlConstants.SEARCH_TYPE_MAP);

	private String subtype;
	private String urlAction;

	private SearchType(String urlAction) {
		this.urlAction = urlAction;
		this.subtype = name().toLowerCase();
	}

	private SearchType(String subtype, String urlAction) {
		this.subtype = subtype;
		this.urlAction = urlAction;
	}

	public String getSubtype() {
		return subtype;
	}

	public static SearchType fromPlaceType(String placeType) {
		for (SearchType t : SearchType.values()) {
			if (placeType.equals(t.getSubtype())) {
				return t;
			}
		}
		return null;
	}

	public static SearchType fromUrlAction(String urlAction) {
		for (SearchType t : SearchType.values()) {
			if (urlAction.equals(t.getUrlAction())) {
				return t;
			}
		}
		return null;
	}

	public String getUrlAction() {
		return urlAction;
	}
}
