package com.nextep.users.services;

import com.nextep.users.model.User;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.model.ItemKey;

public interface UsersService {

	/**
	 * Login to the user account by returning the {@link User} bean if and only
	 * if authentication succeeds.
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	User login(String email, String password, String deviceId, String providerId)
			throws CalException;

	/**
	 * Login to the user account by returning the {@link User} bean if and only
	 * if authentication succeeds.
	 * 
	 * @param email
	 *            email to login to
	 * @param password
	 *            password of the user account
	 * @param mobileLogin
	 *            whether or not this is a mobile login. Mobile logins has a
	 *            specificity which make the token never expires.
	 * @return
	 */
	// User login(String email, String password, boolean mobileLogin)
	// throws CalException;

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

	String getEmailFromToken(String token);

	void refreshUserOnlineTimeout(User user, String token);

	/**
	 * Unique token generator
	 * 
	 * @return a unique token string
	 */
	String generateUniqueToken();
}
