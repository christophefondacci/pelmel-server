package com.nextep.events.model;

import java.util.Date;

import com.nextep.cal.util.model.Named;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * This interface represents an event.
 * 
 * @author cfondacci
 * 
 */
public interface Event extends CalmObject, Named {

	String CAL_ID = "EVNT";

	/**
	 * Event's name
	 * 
	 * @return the name of this event
	 */
	@Override
	String getName();

	/**
	 * Event's start date. For ponctual events, the start date is the date of
	 * the event. For range events, this is the date when the event starts.
	 * 
	 * @return the event's start date
	 */
	Date getStartDate();

	/**
	 * Event's end date. May not be always provided. This is the time when the
	 * event ends
	 * 
	 * @return the event's end date
	 */
	Date getEndDate();

	/**
	 * The key of the place where this event takes place. May not be useful.
	 * This method is exposed as an experimentation. When possible please use
	 * dynamically connected CAL Place element through get(Place.class) method
	 * call on an event instance.
	 * 
	 * @return the {@link ItemKey} of the place where the event takes place
	 */
	ItemKey getLocationKey();

	/**
	 * If this event has been generated from a series, this information points
	 * to the parent series
	 * 
	 * @return the {@link ItemKey} of the series
	 */
	ItemKey getSeriesKey();
}
