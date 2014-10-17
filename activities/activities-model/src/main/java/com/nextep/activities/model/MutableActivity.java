package com.nextep.activities.model;

import java.util.Date;

import com.videopolis.calm.model.ItemKey;

public interface MutableActivity extends Activity {

	/**
	 * Sets the date / time when this activity occurred.
	 * 
	 * @param date
	 *            the time when this activity took place
	 */
	void setDate(Date date);

	/**
	 * Sets the user who initiated this activity
	 * 
	 * @param userKey
	 *            a user {@link ItemKey}
	 */
	void setUserKey(ItemKey userKey);

	/**
	 * Defines the item on which an action has been done
	 * 
	 * @param loggedItemKey
	 *            an {@link ItemKey} of the target element
	 */
	void setLoggedItemKey(ItemKey loggedItemKey);

	/**
	 * Sets extra information which is specific to the type of activity.
	 * 
	 * @param extraInformation
	 *            extra information
	 */
	void setExtraInformation(String extraInformation);

	/**
	 * Sets the type of activity
	 * 
	 * @param activityType
	 *            the {@link ActivityType}
	 */
	void setActivityType(ActivityType activityType);

}