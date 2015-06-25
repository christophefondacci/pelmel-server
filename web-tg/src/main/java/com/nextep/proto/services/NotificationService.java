package com.nextep.proto.services;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.users.model.User;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

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
	Future<Boolean> sendNotification(User user, String message, int badgeCount,
			String sound);

	/**
	 * Queries the various push providers feedback services to check for any app
	 * uninstall, and unregisters the app id accordingly
	 */
	void checkExpiredPushDevices();

	/**
	 * Notifies administrators of the given information expressed as html
	 * 
	 * @param html
	 *            the HTML source of the mail to send
	 */
	void notifyAdminByEmail(String title, String html);

	/**
	 * Sends an email notification to the given address(es)
	 * 
	 * @param title
	 *            title of the email
	 * @param html
	 *            HTML content of the email
	 * @param toAddress
	 *            list of TO addresses
	 * @param templateId
	 *            ID of the sendgrid template
	 * @param bccAddress
	 *            optional list of BCC address
	 */
	void notifyByEmail(String title, String html, Collection<String> toAddress,
			String templateId, String... bccAddress);

	/**
	 * Notifies administrators about place update
	 * 
	 * @param place
	 *            the {@link Place} being updated
	 * @param user
	 *            the {@link User} who made the update
	 * @param oldName
	 *            previous name
	 * @param oldAddress
	 *            previous address
	 * @param oldPlaceType
	 *            previous place type
	 * @param oldCity
	 *            previous city name
	 * @param oldLat
	 *            previous latitude
	 * @param oldLng
	 *            previous longitude
	 * @param oldTagKeys
	 *            previous tags
	 * @param oldDescriptions
	 *            previous descriptions
	 * @param newDescriptions
	 *            new descriptions
	 * @param descriptionKey
	 *            new description keys
	 */
	Future<Boolean> sendPlaceUpdateEmailNotification(Place place, User user,
			String oldName, String oldAddress, String oldPlaceType,
			String oldCity, String oldLat, String oldLng,
			List<ItemKey> oldTagKeys,
			List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey);

	/**
	 * Notifies adminitrator about event update
	 * 
	 * @param event
	 * @param user
	 * @param oldKey
	 * @param oldName
	 * @param oldPlace
	 * @param oldStart
	 * @param oldEnd
	 * @param oldDescriptions
	 * @param newDescriptions
	 * @param descriptionKey
	 * @param newEventPlace
	 * @return
	 */
	Future<Boolean> sendEventUpdateEmailNotification(Event event, User user,
			String oldKey, String oldName, GeographicItem oldPlace,
			String oldStart, String oldEnd,
			List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey,
			GeographicItem newEventPlace);

	/**
	 * Notifies administrator about event update
	 * 
	 * @param event
	 * @param user
	 * @param oldKey
	 * @param oldName
	 * @param oldPlace
	 * @param oldStart
	 * @param oldEnd
	 * @param oldStartHour
	 * @param oldStartMinute
	 * @param oldEndHour
	 * @param oldEndMinute
	 * @param oldMonday
	 * @param oldTuesday
	 * @param oldWednesday
	 * @param oldThursday
	 * @param oldFriday
	 * @param oldSaturday
	 * @param oldSunday
	 * @param oldWeekOfMonthOffset
	 * @param oldDescriptions
	 * @param newDescriptions
	 * @param descriptionKey
	 * @param newEventPlace
	 * @return
	 */
	Future<Boolean> sendEventSeriesUpdateEmailNotification(Event event,
			User user, String oldKey, String oldName, GeographicItem oldPlace,
			String oldStart, String oldEnd, Integer oldStartHour,
			Integer oldStartMinute, Integer oldEndHour, Integer oldEndMinute,
			boolean oldMonday, boolean oldTuesday, boolean oldWednesday,
			boolean oldThursday, boolean oldFriday, boolean oldSaturday,
			boolean oldSunday, Integer oldWeekOfMonthOffset,
			List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey,
			GeographicItem newEventPlace);

	Future<Boolean> sendEventDeletedNotification(Event event, Place eventPlace,
			User user);

	/**
	 * Notifies administrator about a reported information
	 * 
	 * @param obj
	 *            object being reported
	 * @param user
	 *            user who reported the information
	 * @param reportType
	 *            type of report (from Constants.REPORT_TYPE)
	 */
	Future<Boolean> sendReportEmailNotification(CalmObject obj, User user,
			int reportType);

	Future<Boolean> sendMediaAddedEmailNotification(CalmObject obj, User user,
			Media media);

	/**
	 * Notifies administrtor about a comment on a place
	 * 
	 * @param obj
	 *            {@link CalmObject} being commented
	 * @param user
	 *            the {@link User} who wrote the comment
	 * @param comment
	 *            the comment added
	 */
	Future<Boolean> sendCommentAddedEmailNotification(CalmObject obj,
			User user, Comment comment);

	/**
	 * Sends an email to the user that offers him to change his password
	 * 
	 * @param user
	 *            the {@link User} to send the email to
	 */
	void sendChangePasswordEmail(User user);
}
