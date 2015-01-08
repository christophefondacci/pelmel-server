package com.nextep.users.dao;

import java.util.Date;
import java.util.List;

import com.nextep.users.model.User;
import com.videopolis.calm.model.ItemKey;

/**
 * Data Access Object dealing with user operations
 * 
 * @author cfondacci
 * 
 */
public interface UsersDao {

	/**
	 * Gets the user identified by email / password. Authentication will be
	 * performed in the database so a user will only be returned when email and
	 * password are valid.
	 * 
	 * @param email
	 *            user's email
	 * @param password
	 *            user's password
	 * @return the {@link User}
	 */
	User getUser(String email, String password);

	/**
	 * Finds the user from his email, this method is not designed for
	 * authentication and further authentication and security checks must be
	 * made.
	 * 
	 * @param email
	 *            the email address of the user to look for
	 * @return the {@link User} or <code>null</code> if not found
	 */
	User getUserFromEmail(String email);

	/**
	 * Retrieves a user from an authentication token. The user is returned if
	 * and only if the following conditions are met :<br>
	 * - The token is uniquely assigned to a user<br>
	 * - The user's connection timeout is not yet reached<br>
	 * In any other cases, a <code>null</code> value is returned.
	 * 
	 * @param token
	 *            the authentication token to use for user retrieval
	 * @return the corresponding {@link User} or <code>null</code> if token is
	 *         invalid or if associated user reached its connection timeout
	 */
	User getUser(String token);

	/**
	 * Checks whether the user specified by the given email already exists in
	 * the database.
	 * 
	 * @param email
	 *            email address of the user to check
	 * @return <code>true</code> if the user exists, else <code>false</code>
	 */
	boolean userExists(String email);

	/**
	 * Retrieves a user through a facebook ID
	 * 
	 * @param facebookId
	 *            the facebook id
	 * @return the corresponding {@link User} or <code>null</code> if no user
	 *         defined with this facebook ID
	 */
	User getFacebookUser(String facebookId);

	/**
	 * Saves the user password
	 * 
	 * @param userKey
	 *            the {@link ItemKey} of the user
	 * @param oldPassword
	 *            the old password or <code>null</code> for new users
	 * @param newPassword
	 *            the new password to set for this user
	 */
	void savePassword(ItemKey userKey, String oldPassword, String newPassword);

	/**
	 * Saves the user password from the current valid token
	 * 
	 * @param userKey
	 *            the {@link ItemKey} of the user to alter
	 * @param nxtpUserToken
	 *            the current authentication token
	 * @param newPassword
	 *            the new password to set
	 */
	void resetPassword(ItemKey userKey, String nxtpUserToken, String newPassword);

	/**
	 * Assigns a timeout for the user
	 * 
	 * @param user
	 *            User to set as online with a timeout
	 * @param token
	 *            user's authentication token
	 * @param timeoutMinutes
	 *            the number of minutes the user will remain online with no
	 *            activity
	 * @return the timeout date
	 */
	Date setUserOnlineTimeout(User user, String token, int timeoutMinutes);

	String getEmailFromToken(String token);

	/**
	 * Binds an external item to a list of users. Used for example, when a user
	 * likes another user, then a user is bound to another user.
	 * 
	 * @param externalItem
	 *            external item key
	 * @param userKeys
	 *            user to bind to this external item
	 * @return a list of bound users
	 */
	List<User> bindUsers(ItemKey externalItem, List<ItemKey> userKeys);

	/**
	 * Provides the list of users registered under the specified external key.
	 * As of first implementation, the passed item key will generally be a user
	 * key.
	 * 
	 * @param itemKey
	 *            the external item key which can contain users
	 * @return the list of users
	 */
	List<User> getUsersFor(ItemKey itemKey, int pageSize, int pageOffset);

	int getUsersForCount(ItemKey itemKey);
}
