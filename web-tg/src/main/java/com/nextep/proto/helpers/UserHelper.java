package com.nextep.proto.helpers;

import java.util.Date;

import com.nextep.media.model.Media;
import com.nextep.media.model.MediaRequestTypes;
import com.nextep.proto.model.Constants;
import com.nextep.users.model.User;
import com.nextep.users.model.UserPrivateListRequestType;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisCriterion;
import com.videopolis.apis.model.WithCriterion;

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
		return user.getOnlineTimeout() != null && user.getOnlineTimeout().getTime() > System.currentTimeMillis();
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

	/**
	 * Appends private network APIS query to the base user criterion (which
	 * should query a user)
	 * 
	 * @param userCriterion
	 *            a root criterion which should query a user to which you like
	 *            to add private network information
	 * @return the same criterion as userCriterion, with added private network
	 *         queries
	 * @throws ApisException
	 */
	public static ApisCriterion addPrivateNetworkCriteria(ApisCriterion userCriterion) throws ApisException {
		userCriterion
				.with((WithCriterion) SearchRestriction
						.with(User.class,
								new UserPrivateListRequestType(UserPrivateListRequestType.LIST_PENDING_APPROVAL))
						.aliasedBy(Constants.APIS_ALIAS_NETWORK_PENDING)
						.with(Media.class, MediaRequestTypes.THUMB))
				.with((WithCriterion) SearchRestriction
						.with(User.class, new UserPrivateListRequestType(UserPrivateListRequestType.LIST_REQUESTED))
						.aliasedBy(Constants.APIS_ALIAS_NETWORK_TOAPPROVE)
						.with(Media.class, MediaRequestTypes.THUMB))
				.with((WithCriterion) SearchRestriction
						.with(User.class,
								new UserPrivateListRequestType(UserPrivateListRequestType.LIST_PRIVATE_NETWORK))
						.aliasedBy(Constants.APIS_ALIAS_NETWORK_MEMBER).with(Media.class, MediaRequestTypes.THUMB));
		return userCriterion;
	}
}
