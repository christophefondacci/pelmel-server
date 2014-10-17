package com.nextep.proto.helpers;

import java.util.Date;

import com.nextep.users.model.User;

/**
 * Helper class providing some common basic operations related to user
 * management
 * 
 * @author cfondacci
 * 
 */
public final class UserHelper {

	private UserHelper() {
	}

	/**
	 * Informs whether the specified user is currently online.
	 * 
	 * @param user
	 *            the {@link User} to check for online status
	 * @return <code>true</code> if the given user is online, else
	 *         <code>false</code>
	 */
	public static boolean isOnline(User user) {
		return user.getOnlineTimeout() != null
				&& user.getOnlineTimeout().getTime() > System
						.currentTimeMillis();
	}

	/**
	 * Computes the age of the specified user
	 * 
	 * @param user
	 *            the {@link User} to compute the age for
	 * @return the age the user's current age
	 */
	public static Integer getAge(User user) {
		final Date birthdate = user.getBirthday();
		if (birthdate != null) {
			final long age = System.currentTimeMillis() - birthdate.getTime();
			final int ageYears = (int) ((age) / (1000d * 60d * 60d * 24d * 365.25d));
			return ageYears;
		} else {
			return null;
		}
	}
}
