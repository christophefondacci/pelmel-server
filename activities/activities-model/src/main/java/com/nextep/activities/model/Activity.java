package com.nextep.activities.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * An activity represents an action made by a user on an item. Every user action
 * might generate an activity log. For example, a user which modified a place
 * will generate an "update" activity for this "user" on the "place".
 * 
 * @author cfondacci
 * 
 */
public interface Activity extends CalmObject {

	String CAL_TYPE = "ACTI";

	/**
	 * Date when this activity has been logged
	 * 
	 * @return the date of this activity
	 */
	Date getDate();

	/**
	 * Provides the {@link ItemKey} of the user which originated the action
	 * which triggered this log.
	 * 
	 * @return the user's {@link ItemKey}
	 */
	ItemKey getUserKey();

	/**
	 * Provides the {@link ItemKey} of the element on which the action has been
	 * made.
	 * 
	 * @return the {@link ItemKey} of the element which has been modified
	 */
	ItemKey getLoggedItemKey();

	/**
	 * Provides type-specific extra information about this activity
	 * 
	 * @return extra log information (type-specific)
	 */
	String getExtraInformation();

	/**
	 * Provides the activity type
	 * 
	 * @return the activity type
	 */
	ActivityType getActivityType();

	/**
	 * Whether or not this activity is visible on the website. This flag will
	 * generally always be true as non visible activities will generally not be
	 * provided by the database
	 * 
	 * @return <code>true</code> when visible, else <code>false</code> when
	 *         hidden and unindexed
	 */
	boolean isVisible();
}
