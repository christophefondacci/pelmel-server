package com.videopolis.smaug.common.model;

/**
 * <p>
 * This enumeration defines the scope of a given search.
 * </p>
 * 
 * @author Christophe Fondacci
 * @author Shoun Ichida
 */
public enum SearchScope {
	/** Scope for generic nearby block search */
	NEARBY_BLOCK,
	/** Scope for the search of cities */
	CITY,
	/** Scope for the regions nearby block search */
	REGION_NEARBY_BLOCK,
	/** Scope for listview search */
	LISTVIEW,
	/** Scope for top POI surfacic search */
	TOP_POI_IN_BLOCK,
	/** Scope for children search (adm except cities) */
	CHILDREN,
	/** Scope for poi search */
	POI,
	/** Scope for hotel search */
	HOTEL, BAR, SAUNA, RESTAURANT, CLUB, SEXCLUB, SEXSHOP, ASSO,
	/** Scope for all user localization */
	USER_LOCALIZATION,
	/** Scope for boosted elements */
	BOOSTED_PLACES,
	/** Scope for user-related searches */
	USERS, USERS_OFFLINE, USERS_ONLINE,
	/** Scope for event-related searches */
	EVENTS, PLACES, NEARBY_ACTIVITIES, PHOTOS
}
