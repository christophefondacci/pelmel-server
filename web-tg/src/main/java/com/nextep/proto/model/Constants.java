package com.nextep.proto.model;

public interface Constants {

	String ACTION_CONTEXT_SUBDOMAIN = "subdomain";

	String DEFAULT_LANGUAGE = "en";

	String USER_COOKIE_NAME = "nxtpUserToken";

	// Activities
	String ALIAS_ACTIVITY_USER = "a.user";
	String ALIAS_ACTIVITY_OBJECT = "a.obj";
	String ALIAS_ACTIVITY_TARGET = "a.tgt";
	String ACTIVITIES_CREATION_TYPE = "CREATION";

	String ALIAS_FROM = "from";
	String ALIAS_TO = "to";
	String APIS_ALIAS_FAVORITE = "favorites";
	String APIS_ALIAS_EVENT = "event";
	String APIS_ALIAS_EVENT_SERIES = "eseries";
	String APIS_ALIAS_CURRENT_USER = "currentUser";
	String APIS_ALIAS_USER_ITEM_TAGS = "uitags";
	String APIS_ALIAS_USER_LOCATION = "userloc";
	String APIS_ALIAS_ADVERTISING_ITEM = "adItem";
	String APIS_ALIAS_USER_COUNT = "userCount";
	String APIS_ALIAS_EVENT_COUNT = "eventCount";
	String APIS_ALIAS_LIKES = "likeActivity";
	String APIS_ALIAS_BANNER_TARGET = "bannerTarget";

	int TOP_PLACES_PER_PAGE = 20;
	int OVERVIEW_NEARBY_PLACES = 20;
	int EVENTS_PER_PAGE = 16;
	int EVENTS_OTHER_COUNT = 7;

	int MAX_EVENT_POPULAR_CITIES = 15;
	int MAX_PLACE_POPULAR_CITIES = 15;
	int MAX_TOP_EVENTS = 5;
	int MAX_TOP_PLACES = 10;
	int MAX_TOP_CITIES = 20;
	int MAX_SITEMAP_CITIES = 99999;
	int MAX_NEAREST_CITIES = 50;
	int MAX_TOP_COUNTRIES = 20;
	int MAX_TOP_USERS = 3;
	int MAX_USER_POPULAR_CITIES = 15;
	int MAX_TOP_EVENTS_OVERVIEW = 3;
	int MAX_TOP_PLACES_OVERVIEW = 10;
	int MAX_COMMENTS = 10;
	int MAX_FAVORITE_MEN = 15;
	int MAX_ACTIVITIES = 10;
	int MAX_EVENT_USERS = 30;
	int MAX_EVENT_TAGS = 3;
	int MAX_ACTIVITIES_HOMEPAGE_FETCH = 50;
	int MAX_ACTIVITIES_HOMEPAGE = 36;

	String PLACE_TYPE_BAR = "bar";
	String PLACE_TYPE_RESTAURANT = "restaurant";

	String APIS_ALIAS_EVENT_ACTIVITIES = "event";
	String APIS_ALIAS_PLACE_ACTIVITIES = "place";
	String APIS_ALIAS_USER_ACTIVITIES = "user";
	String APIS_ALIAS_EVENT_PLACE = "eventPlace";
	int MIN_ACTIVITIES_FOR_NEWS_INDEX = 3;
	int MIN_RESULTS_FOR_SEARCH_INDEX = 1;

	String PAGE_TYPE_HOMEPAGE = "HP";
	String PAGE_TYPE_SEARCH = "SEARCH";
	String PAGE_TYPE_OVERVIEW = "OV";

	String ACTION_CODE_EDIT = "edit";
	String ACTION_CODE_LIKE = "like";
	String ACTION_CODE_DELETE = "delete";

	String PROPERTY_EVENT_START = "event.start";
	String PROPERTY_EVENT_END = "event.end";
	String PROPERTY_EVENT_FREQ = "event.freq";
	String PROPERTY_OPENING_HOUR = "openingHours";

	String PAGE_STYLE_EVENTS = "events";
	String PAGE_STYLE_CITY = "city";
	String PAGE_STYLE_STATE = "region";
	String PAGE_STYLE_COUNTRY = "country";
	String PAGE_STYLE_CONTINENT = "continent";

	String TAG_DISPLAY_FACET = "FACET";
	String TAG_DISPLAY_CHECKBOX = "CHECKBOX";

	String DEFAULT_IMAGE_PROFILE_URL = "/images/V2/no-photo-profile-small.png";

	String VIEW_TYPE_MOBILE = "MOBILE_NEARBY";
	String VIEW_TYPE_OVERVIEW = "OVERVIEW";

	String PROP_CODE_OPENING_HOURS = "openingHours";

	int REPORT_TYPE_ABUSE = 1;
	int REPORT_TYPE_CLOSED = 2;
	int REPORT_TYPE_LOCATION = 3;
	int REPORT_TYPE_NOTGAY = 4;
	int REPORT_TYPE_REMOVAL_REQUESTED = 5;

	public static final String APIS_ALIAS_NEARBY_PLACES = "nearbyPlaces";

	public static final String APIS_ALIAS_PLACE = "place";

	double METERS_PER_MILE = 1609.344;
}
