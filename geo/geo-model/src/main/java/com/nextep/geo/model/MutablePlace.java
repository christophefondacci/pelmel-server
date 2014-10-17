package com.nextep.geo.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutablePlace extends Place {

	void setPlaceType(String placeType);

	void setCity(City city);

	void setAddress1(String address1);

	void setAddress2(String address2);

	void setLatitude(Double latitude);

	void setLongitude(Double longitude);

	void setName(String name);

	/**
	 * Sets the online flag
	 * 
	 * @param online
	 *            set to <code>false</code> to set this place offline
	 */
	void setOnline(boolean online);

	/**
	 * Defines the {@link ItemKey} of the element to which this place points.
	 * 
	 * @param redirectionItemKey
	 */
	void setRedirectionItemKey(ItemKey redirectionItemKey);

	/**
	 * Sets the SEO-indexation state
	 * 
	 * @param indexed
	 *            <code>true</code> to enable indexation for this place,
	 *            <code>false</code> to disable it
	 */
	void setIndexed(boolean indexed);

	/**
	 * Defines the last update timestamp for this place
	 * 
	 * @param lastUpdate
	 *            last update {@link Date}
	 */
	void setLastUpdateTime(Date lastUpdate);

	/**
	 * Defines the number of times this place has been reported as closed
	 * 
	 * @param closedCount
	 *            number of reports
	 */
	void setClosedCount(int closedCount);
}
