package com.nextep.users.model;

import java.util.Date;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.calm.model.Localized;

/**
 * Definition of a user
 * 
 * @author cfondacci
 * 
 */
public interface User extends CalmObject, Localized {

	String CAL_TYPE = "USER";
	String TOKEN_TYPE = "TOKN";
	String PUSH_TOKEN_TYPE = "PTKN";
	String FACEBOOK_TYPE = "FCBK";
	String EMAIL_TYPE = "EMAL";

	/**
	 * Retrieves the user pseudonym.
	 * 
	 * @return the user pseudonym
	 */
	String getPseudo();

	/**
	 * User's email address
	 * 
	 * @return the user's email
	 */
	String getEmail();

	/**
	 * Number of credits the user has
	 * 
	 * @return the amount of credits of this user
	 */
	int getCredits();

	/**
	 * The user's status message
	 * 
	 * @return
	 */
	String getStatusMessage();

	/**
	 * Retrieves the unit system to use for this user
	 * 
	 * @return the unit system
	 */
	String getUnitSystemCode();

	/**
	 * The user's birthday
	 * 
	 * @return the user's birthday
	 */
	Date getBirthday();

	/**
	 * The user's height
	 * 
	 * @return the height in cm, conversion is always required before displaying
	 */
	Integer getHeightInCm();

	/**
	 * The user's weight
	 * 
	 * @return the user's weight in KG, conversion is always required before
	 *         displaying
	 */
	Integer getWeightInKg();

	/**
	 * Retrieves the online timeout of this user, which corresponds to the time
	 * when the user will go offline if he does not interact with the system
	 * 
	 * @return the timeout date
	 */
	Date getOnlineTimeout();

	/**
	 * Provides the user's authentication token. This information is only
	 * available after a "login" so that the caller of the authentication could
	 * assign the token to the outside world.
	 * 
	 * @return the user's authentication token, or <code>null</code> if
	 *         information is not available
	 */
	String getToken();

	/**
	 * Provides the unique key of the location where the user has last been
	 * seen.
	 * 
	 * @return the {@link ItemKey} of the last location where this user has been
	 *         located
	 */
	ItemKey getLastLocationKey();

	/**
	 * Provides the date where the last location has been computed.
	 * 
	 * @return the date when the users has last been localized
	 */
	Date getLastLocationTime();

	/**
	 * Provides the unique key of the location automatically determined by the
	 * system for statistic information (counting the number of persons at a
	 * place)
	 * 
	 * @return the {@link ItemKey} of the location, or <code>null</code> when
	 *         not currently near a place
	 */
	ItemKey getStatLocationKey();

	/**
	 * Provides facebook's unique ID. This information is only provided for
	 * facebook linked users.
	 * 
	 * @return the facebook user ID
	 */
	String getFacebookId();

	/**
	 * Retrieves the facebook access token
	 * 
	 * @return facebook's access token
	 */
	// String getFacebookToken();

	/**
	 * Informs about the provider to use when sending push notifications. Not
	 * used, here for future multi-provider push support
	 * 
	 * @return the push provider
	 */
	PushProvider getPushProvider();

	/**
	 * Provides the unique device identifier to use when sending push
	 * notifications
	 * 
	 * @return the device ID of this user
	 */
	String getPushDeviceId();
}
