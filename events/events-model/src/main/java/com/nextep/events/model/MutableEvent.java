package com.nextep.events.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

/**
 * This interface represents events that can be modified.
 * 
 * @author cfondacci
 * 
 */
public interface MutableEvent extends Event {

	/**
	 * Defines the event name
	 * 
	 * @param name
	 *            new event name
	 */
	void setName(String name);

	/**
	 * Sets the event start date/time
	 * 
	 * @param startDate
	 *            the event start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Sets the event end date/time
	 * 
	 * @param endDate
	 *            the event end date
	 */
	void setEndDate(Date endDate);

	/**
	 * Sets the key of the location where the event takes place
	 * 
	 * @param locationKey
	 *            the key of the place where the event takes place
	 */
	void setLocationKey(ItemKey locationKey);

	/**
	 * Defines the {@link ItemKey} of the series to which this event belongs
	 * 
	 * @param seriesKey
	 *            the parent series {@link ItemKey}
	 */
	void setSeriesKey(ItemKey seriesKey);

	/**
	 * Sets the last time this event was updated
	 * 
	 * @param lastUpdateTime
	 */
	void setLastUpdateTime(Date lastUpdateTime);

	/**
	 * Defines the very first author of the event
	 * 
	 * @param authorKey
	 */
	void setAuthorKey(ItemKey authorKey);

	/**
	 * Sets the visibility of this event
	 * 
	 * @param isOnline
	 *            <code>true</code> to make the event visible, else
	 *            <code>false</code> to hide/cancel it
	 */
	void setOnline(boolean isOnline);
}
