package com.nextep.geo.model;

import java.util.Date;

import com.nextep.cal.util.model.Indexable;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;

public interface Place extends GeographicItem, Localized, Indexable {

	String CAL_TYPE = "PLAC";
	String CAL_FB_TYPE = "PFBK";

	/**
	 * Provides the type of place
	 * 
	 * @return the type of place
	 */
	String getPlaceType();

	/**
	 * The city where this place is located
	 * 
	 * @return the {@link City} of this place
	 */
	City getCity();

	/**
	 * The first address line of this place
	 * 
	 * @return the address line
	 */
	String getAddress1();

	/**
	 * Provides the second address line of this place
	 * 
	 * @return the second address line
	 */
	String getAddress2();

	/**
	 * Indicates whether this place is online or not
	 * 
	 * @return <code>true</code> when this place is online, else
	 *         <code>false</code>
	 */
	boolean isOnline();

	/**
	 * Provides the item key of the element to which this element should point.
	 * This information will typically be provided when a place is the duplicate
	 * of another one in which case this place will be switched to offline and
	 * point to the master place.
	 * 
	 * @return the {@link ItemKey} of the master place
	 */
	ItemKey getRedirectionItemKey();

	/**
	 * Provides the last update time of the element
	 * 
	 * @return the last update {@link Date}
	 */
	Date getLastUpdateTime();

	/**
	 * Provides the number of times this place has been reported has being
	 * closed
	 * 
	 * @return the number of reports
	 */
	int getClosedCount();

	/**
	 * Provides the facebook page ID for this place
	 * 
	 * @return
	 */
	String getFacebookId();
}
