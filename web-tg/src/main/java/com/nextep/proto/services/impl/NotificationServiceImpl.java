package com.nextep.proto.services.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.util.HtmlUtils;

import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.helpers.MediaHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.services.UrlService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.PushProvider;
import com.nextep.users.model.User;
import com.nextep.users.services.UsersService;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;
import com.videopolis.apis.exception.ApisException;
import com.videopolis.apis.factory.ApisFactory;
import com.videopolis.apis.factory.SearchRestriction;
import com.videopolis.apis.model.ApisRequest;
import com.videopolis.apis.service.ApiCompositeResponse;
import com.videopolis.apis.service.ApiService;
import com.videopolis.calm.exception.CalException;
import com.videopolis.calm.factory.CalmFactory;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;
import com.videopolis.cals.factory.ContextFactory;

/**
 * Default implementation of notification service using push messages
 * 
 * @author cfondacci
 * 
 */
public class NotificationServiceImpl implements NotificationService {

	private static final Log LOGGER = LogFactory.getLog("notifications");
	private static final String APIS_ALIAS_USER = "user";
	private static final String APNS_FIELD_UNREAD_NETWORK = "unreadNetwork";

	@Resource(mappedName = "smtpHostName")
	private String smtpHostName;
	@Resource(mappedName = "smtpAuthUser")
	private String smtpAuthUser;
	@Resource(mappedName = "smtpAuthPassword")
	private String smtpAuthPassword;
	@Resource(mappedName = "smtpAuthPort")
	private Integer smtpPort;
	@Resource(mappedName = "notification.enabled")
	private Boolean emailEnabled;

	@Resource(mappedName = "push.android.senderId")
	private String pushAndroidSenderId;

	// Injected services
	private UrlService urlService;
	private String baseUrl;
	@Autowired
	@Qualifier("apiService")
	private ApiService apiService;
	@Autowired
	@Qualifier("usersService")
	private CalPersistenceService usersService;
	@Autowired
	private EventManagementService eventManagementService;

	private ApnsService apnsService;
	private SendGrid sendgrid;

	// Push information
	private String pushKeyPath;
	private String pushKeyPassword;
	private boolean production;
	private boolean pushEnabled = true;
	// Email notification
	private String adminEmailAlias;

	// Date formatter
	private DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	public void init() throws Exception {
		if (pushEnabled) {
			final ApnsServiceBuilder builder = APNS.newService().withCert(pushKeyPath, pushKeyPassword);
			if (production) {
				apnsService = builder.withProductionDestination().build();
			} else {
				apnsService = builder.withSandboxDestination().build();
			}
		}
		// Initializing sendgrid client
		sendgrid = new SendGrid(smtpAuthUser, smtpAuthPassword);
	}

	@PreDestroy
	public void shutdown() throws Exception {
		apnsService.stop();
	}

	@Override
	public void checkExpiredPushDevices() {
		// pushManager.requestExpiredTokens();
		final Map<String, Date> expiredTokens = apnsService.getInactiveDevices();
		if (expiredTokens == null) {
			return;
		}
		LOGGER.info("PUSH: Processing " + expiredTokens.size() + " expired tokens");
		for (final String token : expiredTokens.keySet()) {
			final Date expirationDate = expiredTokens.get(token);
			LOGGER.info("PUSH: Processing expired token '" + token + "' expired on '" + expirationDate + "'");
			// Stop sending push notifications to each expired token if the
			// expiration
			// time is after the last time the app registered that token.
			try {
				ApisRequest request = ApisFactory.createCompositeRequest();
				request.addCriterion(
						SearchRestriction.alternateKey(User.class, CalmFactory.createKey(User.PUSH_TOKEN_TYPE, token))
								.aliasedBy(APIS_ALIAS_USER));
				final ApiCompositeResponse response = (ApiCompositeResponse) apiService.execute(request,
						ContextFactory.createContext(Locale.getDefault()));
				final User user = response.getUniqueElement(User.class, APIS_ALIAS_USER);
				if (user != null) {
					if (user.getOnlineTimeout().compareTo(expirationDate) < 0) {
						ContextHolder.toggleWrite();
						((MutableUser) user).setPushDeviceId(null);
						usersService.saveItem(user);
						LOGGER.info("PUSH: unregistering device ID for user " + user.getKey() + " - " + token);
					}
				}
			} catch (CalException | ApisException e) {
				LOGGER.error("PUSH: unable to create APIS criterion to fetch token [" + token + "]", e);
			}
		}
	}

	@Override
	@Async
	public Future<Boolean> sendNotification(User user, String message, int badgeCount, int unreadNetwork,
			String sound) {
		if (user.getPushDeviceId() != null && pushEnabled) {

			LOGGER.info("Sending push notification to user [" + user.getKey() + "]");
			if (user.getPushProvider() == PushProvider.APPLE) {
				String payload = APNS.newPayload().badge(badgeCount).alertBody(message)
						.customField(APNS_FIELD_UNREAD_NETWORK, unreadNetwork).build();
				apnsService.push(user.getPushDeviceId(), payload);
			} else {
				final Sender sender = new Sender(pushAndroidSenderId);
				final com.google.android.gcm.server.Message gcmMsg = new com.google.android.gcm.server.Message.Builder()
						.collapseKey(user.getPushDeviceId()).timeToLive(30).delayWhileIdle(true)
						.addData("message", message).addData("unreadCount", String.valueOf(badgeCount)).build();
				try {
					MulticastResult result = sender.send(gcmMsg, Arrays.asList(user.getPushDeviceId()), 1);
					if (result.getResults() != null) {
						int canonicalRegId = result.getCanonicalIds();
						if (canonicalRegId != 0) {

						}
					} else {
						int error = result.getFailure();
						LOGGER.error("Broadcast failure while sending msg to " + user.getKey() + ": " + error);
					}
				} catch (Exception e) {
					LOGGER.error("Broadcast failure while sending msg to " + user.getKey() + ": " + e.getMessage(), e);
				}
			}
		}
		return new AsyncResult<Boolean>(true);
	}

	@Override
	public void notifyAdminByEmail(String title, String html) {
		notifyByEmail(title, html, "christophe@pelmelguide.com", adminEmailAlias);
	}

	@Override
	public void notifyByEmail(String title, String html, String toAddress, String bccAddress) {
		notifyByEmail(title, html, Arrays.asList(toAddress.split(" ")), "43c9d208-8167-48f8-bc90-0edc03575c5f",
				bccAddress != null ? bccAddress.split(" ") : null);
	}

	@Override
	public void notifyByEmail(String title, String html, Collection<String> toAddress, String templateId,
			String... bccAddress) {
		LOGGER.info("<br>=================<br>" + title + "<br>" + html);

		if (adminEmailAlias == null || adminEmailAlias.trim().isEmpty() || !emailEnabled) {
			return;
		}
		Email sendgridEmail = new Email();

		sendgridEmail.setHtml(html);
		sendgridEmail.setFrom("no-reply@pelmelguide.com");
		sendgridEmail.setFromName("The PELMEL Team");
		sendgridEmail.setTemplateId(templateId);
		sendgridEmail.setSubject(title);

		sendgridEmail.addTo(toAddress.toArray(new String[toAddress.size()]));

		if (bccAddress != null) {
			// emails = bccAddress.split(" ");
			sendgridEmail.addBcc(bccAddress);
		}

		// Sends the email
		try {

			Response response = sendgrid.send(sendgridEmail);
			if (response.getStatus()) {
				LOGGER.info("SUCCESS: Sent email notification");
			} else {
				LOGGER.error("ERROR: Unable to send email - error #" + response.getCode() + " '" + response.getMessage()
						+ "'");
			}
		} catch (SendGridException e) {
			LOGGER.error("Unable to send email notification: " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public Future<Boolean> sendEventUpdateEmailNotification(Event event, User user, String oldKey, String oldName,
			GeographicItem oldPlace, String oldStart, String oldEnd, List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey, GeographicItem newEventPlace) {
		return sendEventSeriesUpdateEmailNotification(event, user, oldKey, oldName, oldPlace, oldStart, oldEnd, null,
				null, null, null, false, false, false, false, false, false, false, null, oldDescriptions,
				newDescriptions, descriptionKey, newEventPlace);
	}

	@Override
	@Async
	public Future<Boolean> sendEventSeriesUpdateEmailNotification(Event event, User user, String oldKey, String oldName,
			GeographicItem oldPlace, String oldStart, String oldEnd, Integer oldStartHour, Integer oldStartMinute,
			Integer oldEndHour, Integer oldEndMinute, boolean oldMonday, boolean oldTuesday, boolean oldWednesday,
			boolean oldThursday, boolean oldFriday, boolean oldSaturday, boolean oldSunday,
			Integer oldWeekOfMonthOffset, List<? extends Description> oldDescriptions, String[] newDescriptions,
			String[] descriptionKey, GeographicItem newEventPlace) {
		try {
			final StringBuilder buf = new StringBuilder();

			// Generic email header
			final String updated = oldKey == null ? "added" : "####".equals(oldKey) ? "deleted" : "updated";
			fillEmailHeaderFor(buf, event, user, updated);

			// Reporting changed information
			buf.append(
					"<table cellpadding=\"5\" style=\"border: 1px solid #ccc;\"><thead><tr><th>Field</th><th>Old Value</th><th>NEW value</th></thead><tbody>");
			appendField(buf, "Name", oldName, event.getName());

			String oldUrl = null;
			if (oldPlace != null) {
				oldUrl = urlService.getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), oldPlace);
			}
			appendFieldWithUrl(buf, "Place", DisplayHelper.getName(oldPlace), DisplayHelper.getName(newEventPlace),
					oldUrl, urlService.getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), newEventPlace));

			if (event instanceof EventSeries) {
				final EventSeries series = (EventSeries) event;
				appendField(buf, "Event Type", series.getCalendarType().name(), series.getCalendarType().name());
				appendField(buf, "Start time", pad(oldStartHour) + ":" + pad(oldStartMinute),
						pad(series.getStartHour()) + ":" + pad(series.getStartMinute()));
				appendField(buf, "End time", pad(oldEndHour) + ":" + pad(oldEndMinute),
						pad(series.getEndHour()) + ":" + pad(series.getEndMinute()));
				appendField(buf, "Monday", String.valueOf(oldMonday), String.valueOf(series.isMonday()));
				appendField(buf, "Tuesday", String.valueOf(oldTuesday), String.valueOf(series.isTuesday()));
				appendField(buf, "Wednesday", String.valueOf(oldWednesday), String.valueOf(series.isWednesday()));
				appendField(buf, "Thursday", String.valueOf(oldThursday), String.valueOf(series.isThursday()));
				appendField(buf, "Friday", String.valueOf(oldFriday), String.valueOf(series.isFriday()));
				appendField(buf, "Saturday", String.valueOf(oldSaturday), String.valueOf(series.isSaturday()));
				appendField(buf, "Sunday", String.valueOf(oldSunday), String.valueOf(series.isSunday()));
				appendField(buf, "Week of month",
						oldWeekOfMonthOffset == null ? null : String.valueOf(oldWeekOfMonthOffset),
						String.valueOf(series.getWeekOfMonthOffset()));
			} else {
				// Getting event timezone
				final String eventTimezone = eventManagementService.getEventTimezoneId(event);
				final Date localizedStart = eventManagementService.convertDate(event.getStartDate(),
						TimeZone.getDefault().getID(), eventTimezone);
				final Date localizedEnd = eventManagementService.convertDate(event.getEndDate(),
						TimeZone.getDefault().getID(), eventTimezone);

				appendField(buf, "Start date", oldStart, dateFormatter.format(localizedStart));
				appendField(buf, "End date", oldEnd, dateFormatter.format(localizedEnd));
			}
			appendDescriptions(buf, oldDescriptions, newDescriptions, descriptionKey);
			buf.append("</tbody></table>");
			fillEmailFooterFor(buf, event, user);
			notifyAdminByEmail("Event " + (event.getName() == null ? "" : event.getName() + " ") + updated + " by "
					+ user.getPseudo(), buf.toString());
		} catch (Exception e) {
			LOGGER.error("Exception while generating EVENT email notification: " + e.getMessage(), e);
		}
		return new AsyncResult<Boolean>(true);
	}

	@SuppressWarnings("unchecked")
	@Async
	@Override
	public Future<Boolean> sendEventDeletedNotification(Event event, Place eventPlace, User user) {
		return sendEventSeriesUpdateEmailNotification(event, user, "####", null, null, null, null, null, null, null,
				null, false, false, false, false, false, false, false, null, Collections.EMPTY_LIST, null, null,
				eventPlace);
	}

	private String pad(Integer hour) {
		if (hour == null) {
			return "00";
		} else {
			String result = String.valueOf(hour);
			if (hour < 10) {
				result = "0" + result;
			}
			return result;
		}
	}

	@Override
	@Async
	public Future<Boolean> sendPlaceUpdateEmailNotification(Place place, User user, String oldName, String oldAddress,
			String oldPlaceType, String oldCity, String oldLat, String oldLng, List<ItemKey> oldTagKeys,
			List<? extends Description> oldDescriptions, String[] newDescriptions, String[] descriptionKey) {
		final StringBuilder buf = new StringBuilder();

		// Generic email header
		final String updated = oldName == null ? "added" : "updated";
		fillEmailHeaderFor(buf, place, user, updated);

		// Reporting changed information
		buf.append(
				"<table cellpadding=\"5\" style=\"border: 1px solid #ccc;\"><thead><tr><th>Field</th><th>Old Value</th><th>NEW value</th></thead><tbody>");
		appendField(buf, "Name", oldName, place.getName());
		appendField(buf, "Address", oldAddress, place.getAddress1());
		appendField(buf, "Place type", oldPlaceType, place.getPlaceType());
		appendField(buf, "City", oldCity, place.getCity().getName());
		appendField(buf, "Latitude", oldLat, String.valueOf(place.getLatitude()));
		appendField(buf, "Longitude", oldLng, String.valueOf(place.getLongitude()));
		appendDescriptions(buf, oldDescriptions, newDescriptions, descriptionKey);
		buf.append("</tbody></table>");
		fillEmailFooterFor(buf, place, user);
		final String objType = getObjectTypeName(place);
		notifyAdminByEmail(objType + " " + place.getName() + " " + updated + " by " + user.getPseudo(), buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	private void appendDescriptions(StringBuilder buf, List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey) {
		// Descriptions
		final Map<String, Description> oldDescMap = new HashMap<String, Description>();
		if (descriptionKey != null) {
			for (Description oldDesc : oldDescriptions) {
				oldDescMap.put(oldDesc.getKey().toString(), oldDesc);
			}
			for (int i = 0; i < descriptionKey.length; i++) {
				final String key = descriptionKey[i];
				final String newDesc = newDescriptions[i];
				final Description oldDesc = oldDescMap.get(key);
				appendField(buf, "Description", oldDesc != null ? oldDesc.getDescription() : "", newDesc);
			}
		}
	}

	private String getObjectTypeName(CalmObject object) {
		String objectType = object.getKey().getType();
		if (object instanceof Place) {
			objectType = "Place";
		} else if (object instanceof User) {
			objectType = "User";
		} else if (object instanceof EventSeries) {
			objectType = "Recurring event";
		} else if (object instanceof Event) {
			objectType = "Event";
		}
		return objectType;
	}

	/**
	 * Fills a generic email header for the given object
	 * 
	 * @param buf
	 *            the {@link StringBuilder} buffer to fill
	 * @param object
	 *            the object being altered
	 * @param user
	 *            the user who originated the action
	 * @param actionType
	 *            the type of action
	 */
	private void fillEmailHeaderFor(StringBuilder buf, CalmObject object, User user, String actionType) {
		final String url = baseUrl + urlService.getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), object);
		final String userUrl = baseUrl + urlService.getOverviewUrl(DisplayHelper.getDefaultAjaxContainer(), user);

		final String objectType = getObjectTypeName(object);
		buf.append("Hello administrators,<br><br>A " + objectType.toLowerCase() + " has been " + actionType
				+ " on <a href=\"http://www.pelmelguide.com\">PELMEL Guide</a>:<br><br>");
		// buf.append("Hello administrators,<br><br>A place has been updated on
		// PELMEL Guide:<br>");
		final String name = DisplayHelper.getName(object);
		buf.append(objectType + " " + actionType + ": <a href=\"" + url + "\">"
				+ (name == null || name.trim().isEmpty() ? "[No name]" : name) + "</a><br>");
		buf.append(objectType + " " + actionType + " by: <a href=\"" + userUrl + "\">" + user.getPseudo()
				+ "</a><br><br>");
	}

	private void appendField(StringBuilder buf, String fieldName, String oldVal, String newVal) {
		boolean isBold = newVal != null && !newVal.equals(oldVal);
		buf.append("<tr><td style=\"text-align:right;\"><i>" + fieldName + "</i></td><td>"
				+ HtmlUtils.htmlEscape(oldVal) + "</td><td>" + (isBold ? "<b>" : "") + HtmlUtils.htmlEscape(newVal)
				+ (isBold ? "</b>" : "") + "</td></tr>");
	}

	private void appendFieldWithUrl(StringBuilder buf, String fieldName, String oldVal, String newVal, String oldUrl,
			String newUrl) {
		boolean isBold = newVal != null && !newVal.equals(oldVal);

		buf.append("<tr><td style=\"text-align:right;\"><i>" + fieldName + "</i></td><td><a href=\"" + baseUrl + oldUrl
				+ "\">" + HtmlUtils.htmlEscape(oldVal) + "</a></td><td><a href=\"" + baseUrl + newUrl + "\">"
				+ (isBold ? "<b>" : "") + HtmlUtils.htmlEscape(newVal) + (isBold ? "</b>" : "") + "</a></td></tr>");
	}

	@Override
	@Async
	public Future<Boolean> sendReportEmailNotification(CalmObject obj, User user, int reportType) {
		final StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "reported");

		buf.append("Reported as <b>");
		String reportTypeLabel = "n/a";
		switch (reportType) {
		case Constants.REPORT_TYPE_ABUSE:
			reportTypeLabel = "photo abuse";
			break;
		case Constants.REPORT_TYPE_CLOSED:
			reportTypeLabel = "closed / not existing";
			break;
		case Constants.REPORT_TYPE_LOCATION:
			reportTypeLabel = "misplaced";
			break;
		case Constants.REPORT_TYPE_NOTGAY:
			reportTypeLabel = "not gay";
			break;
		case Constants.REPORT_TYPE_REMOVAL_REQUESTED:
			reportTypeLabel = "removal requested";
			break;
		}
		buf.append(reportTypeLabel);
		buf.append("</b>");
		fillEmailFooterFor(buf, obj, user);
		final String objectType = getObjectTypeName(obj);
		notifyAdminByEmail(objectType + " " + DisplayHelper.getName(obj) + " reported as " + reportTypeLabel + " by "
				+ user.getPseudo(), buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	private void fillEmailFooterFor(StringBuilder buf, CalmObject object, User user) {
		buf.append("<br>");
		// buf.append("<span
		// style=\"float:right;padding-top:10px;padding-bottom:10px;\">The
		// PELMEL server ;)</span>");
	}

	@Override
	@Async
	public Future<Boolean> sendMediaAddedEmailNotification(CalmObject obj, User user, Media media) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "modified with a new photo");

		buf.append("Photo added: <a href=\"" + MediaHelper.getImageUrl(media.getUrl()) + "\">New photo</a><br>");
		buf.append("<img src=\"" + MediaHelper.getImageUrl(media.getUrl()) + "\">");
		fillEmailFooterFor(buf, obj, user);
		final String objType = getObjectTypeName(obj);
		notifyAdminByEmail("Photo added to " + objType + " " + DisplayHelper.getName(obj) + " by " + user.getPseudo(),
				buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	@Override
	@Async
	public Future<Boolean> sendCommentAddedEmailNotification(CalmObject obj, User user, Comment comment) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "commented");
		buf.append("Comment added: <br><p>" + comment.getMessage() + "</p>");
		try {
			final Media m = comment.getUnique(Media.class);
			if (m != null) {
				buf.append(
						"Comment Photo: <a href=\"" + MediaHelper.getImageUrl(m.getUrl()) + "\">Comment image</a><br>");
			}
		} catch (CalException e) {
			LOGGER.error("Unable to extract comment image for comment '" + comment.getKey() + "': " + e.getMessage(),
					e);
		}
		fillEmailFooterFor(buf, obj, user);
		final String objType = getObjectTypeName(obj);
		notifyAdminByEmail("Comment added to " + objType + " " + DisplayHelper.getName(obj) + " by " + user.getPseudo(),
				buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	@Override
	@Async
	public Future<Boolean> sendUserRegisteredEmailNotification(User user) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, user, user, "signed up");
		fillEmailFooterFor(buf, user, user);

		notifyAdminByEmail("User " + user.getPseudo() + " registered", buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	@Override
	public void sendChangePasswordEmail(User user) {

		final StringBuilder buf = new StringBuilder();
		buf.append("Hello " + user.getPseudo() + ",<br><br>");
		buf.append("Please follow this link to change your password:<br>");
		final String url = urlService.getResetPasswordUrl(user);
		buf.append("<a href=\"" + url + "\">" + url + "</a><br><br>");
		buf.append("If the link does not work try to copy / paste it in a new web browser window.");
		fillEmailFooterFor(buf, user, user);

		notifyByEmail("[PELMEL] Lost password link", buf.toString(), user.getEmail(), "cfondacci@gmail.com");

	}

	@Override
	public void sendEmailValidationEmail(User user) {
		// In case we don't have a token, we generate a new one
		if (user.getEmailValidationToken() == null) {
			ContextHolder.toggleWrite();
			final String emailToken = ((UsersService) usersService).generateUniqueToken(user);
			((MutableUser) user).setEmailValidationToken(emailToken);
			usersService.saveItem(user);
		}

		final StringBuilder buf = new StringBuilder();
		buf.append("Hello " + user.getPseudo() + ",<br><br>");
		buf.append("Please follow this link to validate your email address:<br>");
		final String url = urlService.getEmailValidationUrl(user);
		buf.append("<a href=\"" + url + "\">" + url + "</a><br><br>");
		buf.append("If the link does not work try to copy / paste it in a new web browser window.");
		fillEmailFooterFor(buf, user, user);

		notifyByEmail("[PELMEL] Email validation", buf.toString(), user.getEmail(), "cfondacci@gmail.com");

	}

	public void setPushKeyPath(String pushKeyPath) {
		this.pushKeyPath = pushKeyPath;
	}

	public void setPushKeyPassword(String pushKeyPassword) {
		this.pushKeyPassword = pushKeyPassword;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}

	public void setPushEnabled(boolean pushEnabled) {
		this.pushEnabled = pushEnabled;
	}

	public void setAdminEmailAlias(String adminEmailAlias) {
		this.adminEmailAlias = adminEmailAlias;
	}

	public void setUrlService(UrlService urlService) {
		this.urlService = urlService;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

}
