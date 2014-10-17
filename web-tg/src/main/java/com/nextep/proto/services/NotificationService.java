package com.nextep.proto.services;

import com.nextep.users.model.User;

/**
 * Common interface for sending notification to mobile devices
 * 
 * @author cfondacci
 * 
 */
public interface NotificationService {

	/**
	 * Sends a new push notification to the given user
	 * 
	 * @param user
	 *            the {@link User} to notify
	 * @param message
	 *            the message to send to the user
	 * @param badgeCount
	 *            the badge count
	 * @param sound
	 *            the sound to use when notifying (not used yet)
	 */
	void sendNotification(User user, String message, int badgeCount,
			String sound);
}
