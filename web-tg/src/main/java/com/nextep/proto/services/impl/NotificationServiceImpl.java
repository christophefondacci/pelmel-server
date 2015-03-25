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
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.util.HtmlUtils;

import com.nextep.cal.util.services.CalPersistenceService;
import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.events.model.EventSeries;
import com.nextep.geo.model.GeographicItem;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.EventManagementService;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.services.UrlService;
import com.nextep.proto.spring.ContextHolder;
import com.nextep.users.model.MutableUser;
import com.nextep.users.model.User;
import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.ExpiredToken;
import com.relayrides.pushy.apns.ExpiredTokenListener;
import com.relayrides.pushy.apns.FailedConnectionListener;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerConfiguration;
import com.relayrides.pushy.apns.RejectedNotificationListener;
import com.relayrides.pushy.apns.RejectedNotificationReason;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.MalformedTokenStringException;
import com.relayrides.pushy.apns.util.SSLContextUtil;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;
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

	private PushManager<SimpleApnsPushNotification> pushManager;

	// Push information
	private String pushKeyPath;
	private String pushKeyPassword;
	private boolean production;
	private boolean pushEnabled = true;
	// Email notification
	private String adminEmailAlias;

	// Date formatter
	private DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	private class MyFailedConnectionListener implements
			FailedConnectionListener<SimpleApnsPushNotification> {

		@Override
		public void handleFailedConnection(
				final PushManager<? extends SimpleApnsPushNotification> pushManager,
				final Throwable cause) {

			LOGGER.error("PUSH: connection failed: " + cause.getMessage(),
					cause);
			if (cause instanceof SSLHandshakeException) {
				// This is probably a permanent failure, and we should shut down
				// the PushManager.
				try {
					pushManager.shutdown();
				} catch (InterruptedException e) {
					LOGGER.error("Unable to shutdown: " + e.getMessage(), e);
				}
			}
		}
	}

	private class MyRejectedNotificationListener implements
			RejectedNotificationListener<SimpleApnsPushNotification> {
		@Override
		public void handleRejectedNotification(
				PushManager<? extends SimpleApnsPushNotification> pushManager,
				SimpleApnsPushNotification notification,
				RejectedNotificationReason rejectionReason) {
			LOGGER.error("PUSH: Notification rejected (reason: "
					+ rejectionReason.getErrorCode() + ") '"
					+ notification.getPayload() + "'");
		}
	}

	private class MyExpiredTokenListener implements
			ExpiredTokenListener<SimpleApnsPushNotification> {

		@Override
		public void handleExpiredTokens(
				final PushManager<? extends SimpleApnsPushNotification> pushManager,
				final Collection<ExpiredToken> expiredTokens) {
			if (expiredTokens == null) {
				return;
			}
			LOGGER.info("PUSH: Processing " + expiredTokens.size()
					+ " expired tokens");
			for (final ExpiredToken expiredToken : expiredTokens) {
				final String token = Arrays.toString(expiredToken.getToken());
				LOGGER.info("PUSH: Processing expired token '" + token + "'");
				// Stop sending push notifications to each expired token if the
				// expiration
				// time is after the last time the app registered that token.
				try {
					ApisRequest request = ApisFactory.createCompositeRequest();
					request.addCriterion(SearchRestriction.alternateKey(
							User.class,
							CalmFactory.createKey(User.PUSH_TOKEN_TYPE, token))
							.aliasedBy(APIS_ALIAS_USER));
					final ApiCompositeResponse response = (ApiCompositeResponse) apiService
							.execute(request, ContextFactory
									.createContext(Locale.getDefault()));
					final User user = response.getUniqueElement(User.class,
							APIS_ALIAS_USER);
					if (user != null) {
						if (user.getOnlineTimeout().compareTo(
								expiredToken.getExpiration()) < 0) {
							ContextHolder.toggleWrite();
							((MutableUser) user).setPushDeviceId(null);
							usersService.saveItem(user);
							LOGGER.info("PUSH: unregistering device ID for user "
									+ user.getKey()
									+ " - "
									+ Arrays.toString(expiredToken.getToken()));
						}
					}
				} catch (CalException | ApisException e) {
					LOGGER.error(
							"PUSH: unable to create APIS criterion to fetch token ["
									+ expiredToken + "]", e);
				}
			}
		}
	}

	public void init() throws Exception {
		if (pushEnabled) {
			final ApnsEnvironment apnsEnv = production ? ApnsEnvironment
					.getProductionEnvironment() : ApnsEnvironment
					.getSandboxEnvironment();
			pushManager = new PushManager<SimpleApnsPushNotification>(apnsEnv,
					SSLContextUtil.createDefaultSSLContext(pushKeyPath,
							pushKeyPassword), null, null, null,
					new PushManagerConfiguration(), "PelmelPush");
			pushManager.start();
			pushManager
					.registerFailedConnectionListener(new MyFailedConnectionListener());
			pushManager
					.registerExpiredTokenListener(new MyExpiredTokenListener());
			pushManager
					.registerRejectedNotificationListener(new MyRejectedNotificationListener());
		}
	}

	@PreDestroy
	public void shutdown() throws Exception {
		if (pushManager != null) {
			pushManager.shutdown();
		}
	}

	@Override
	public void checkExpiredPushDevices() {
		pushManager.requestExpiredTokens();
	}

	@Override
	@Async
	public Future<Boolean> sendNotification(User user, String message,
			int badgeCount, String sound) {
		if (user.getPushDeviceId() != null && pushEnabled) {

			LOGGER.info("Sending push notification to user [" + user.getKey()
					+ "]");
			byte[] token;
			try {
				token = TokenUtil
						.tokenStringToByteArray(user.getPushDeviceId());

				final ApnsPayloadBuilder payloadBuilder = new ApnsPayloadBuilder();

				payloadBuilder.setAlertBody(message);
				payloadBuilder.setBadgeNumber(badgeCount);
				// payloadBuilder.setSoundFileName("ring-ring.aiff");

				final String payload = payloadBuilder
						.buildWithDefaultMaximumLength();

				pushManager.getQueue().put(
						new SimpleApnsPushNotification(token, payload));
			} catch (MalformedTokenStringException e) {
				LOGGER.error(
						"PUSH: Invalid token string '" + user.getPushDeviceId(),
						e);
			} catch (InterruptedException e) {
				LOGGER.error("PUSH: Interrupted while adding PUSH to queue '"
						+ user.getPushDeviceId(), e);
			}
		}
		return new AsyncResult<Boolean>(true);
	}

	// Authenticates to SendGrid
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			String username = smtpAuthUser;
			String password = smtpAuthPassword;
			return new PasswordAuthentication(username, password);
		}
	}

	@Override
	public void notifyAdminByEmail(String title, String html) {
		notifyByEmail(title, html, "christophe@pelmelguide.com",
				adminEmailAlias);
	}

	private void notifyByEmail(String title, String html, String toAddress,
			String bccAddress) {
		LOGGER.info("<br>=================<br>" + title + "<br>" + html);

		if (adminEmailAlias == null || adminEmailAlias.trim().isEmpty()
				|| !emailEnabled) {
			return;
		}
		try {
			Properties props = new Properties();
			props.put("mail.transport.protocol", "smtp");
			props.put("mail.smtp.auth", "true");

			Authenticator auth = new SMTPAuthenticator();
			Session mailSession = Session.getDefaultInstance(props, auth);
			Transport transport = mailSession.getTransport();

			MimeMessage message = new MimeMessage(mailSession);

			message.setContent(html, "text/html");
			message.setFrom(new InternetAddress("no-reply@pelmelguide.com"));
			message.setSubject(title);
			String[] emails = toAddress.split(" ");
			for (String email : emails) {
				message.addRecipient(Message.RecipientType.TO,
						new InternetAddress(email));
			}
			emails = bccAddress.split(" ");
			for (String email : emails) {
				try {
					message.addRecipient(Message.RecipientType.BCC,
							new InternetAddress(email));
				} catch (MessagingException e) {
					LOGGER.error("Unable to add recepient to email: " + email
							+ " reason '" + e.getMessage() + "'", e);
				}
			}

			// Sends the email
			transport.connect(smtpHostName, smtpPort, smtpAuthUser,
					smtpAuthPassword);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			LOGGER.info("SUCCESS: Sent email notification");
		} catch (MessagingException e) {
			LOGGER.error(
					"Unable to send email notification: " + e.getMessage(), e);
		}

	}

	@Override
	@Async
	public Future<Boolean> sendEventUpdateEmailNotification(Event event,
			User user, String oldKey, String oldName, GeographicItem oldPlace,
			String oldStart, String oldEnd,
			List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey,
			GeographicItem newEventPlace) {
		return sendEventSeriesUpdateEmailNotification(event, user, oldKey,
				oldName, oldPlace, oldStart, oldEnd, null, null, null, null,
				false, false, false, false, false, false, false,
				oldDescriptions, newDescriptions, descriptionKey, newEventPlace);
	}

	@Override
	@Async
	public Future<Boolean> sendEventSeriesUpdateEmailNotification(Event event,
			User user, String oldKey, String oldName, GeographicItem oldPlace,
			String oldStart, String oldEnd, Integer oldStartHour,
			Integer oldStartMinute, Integer oldEndHour, Integer oldEndMinute,
			boolean oldMonday, boolean oldTuesday, boolean oldWednesday,
			boolean oldThursday, boolean oldFriday, boolean oldSaturday,
			boolean oldSunday, List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey,
			GeographicItem newEventPlace) {
		try {
			final StringBuilder buf = new StringBuilder();

			// Generic email header
			final String updated = oldKey == null ? "added" : "####"
					.equals(oldKey) ? "deleted" : "updated";
			fillEmailHeaderFor(buf, event, user, updated);

			// Reporting changed information
			buf.append("<table cellpadding=\"5\" style=\"border: 1px solid #ccc;\"><thead><tr><th>Field</th><th>Old Value</th><th>NEW value</th></thead><tbody>");
			appendField(buf, "Name", oldName, event.getName());

			String oldUrl = null;
			if (oldPlace != null) {
				oldUrl = urlService.getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), oldPlace);
			}
			appendFieldWithUrl(buf, "Place", DisplayHelper.getName(oldPlace),
					DisplayHelper.getName(newEventPlace), oldUrl,
					urlService.getOverviewUrl(
							DisplayHelper.getDefaultAjaxContainer(),
							newEventPlace));

			if (event instanceof EventSeries) {
				final EventSeries series = (EventSeries) event;
				appendField(buf, "Event Type", series.getCalendarType().name(),
						series.getCalendarType().name());
				appendField(buf, "Start time", pad(oldStartHour) + ":"
						+ pad(oldStartMinute), pad(series.getStartHour()) + ":"
						+ pad(series.getStartMinute()));
				appendField(buf, "End time", pad(oldEndHour) + ":"
						+ pad(oldEndMinute), pad(series.getEndHour()) + ":"
						+ pad(series.getEndMinute()));
				appendField(buf, "Monday", String.valueOf(oldMonday),
						String.valueOf(series.isMonday()));
				appendField(buf, "Tuesday", String.valueOf(oldTuesday),
						String.valueOf(series.isTuesday()));
				appendField(buf, "Wednesday", String.valueOf(oldWednesday),
						String.valueOf(series.isWednesday()));
				appendField(buf, "Thursday", String.valueOf(oldThursday),
						String.valueOf(series.isThursday()));
				appendField(buf, "Friday", String.valueOf(oldFriday),
						String.valueOf(series.isFriday()));
				appendField(buf, "Saturday", String.valueOf(oldSaturday),
						String.valueOf(series.isSaturday()));
				appendField(buf, "Sunday", String.valueOf(oldSunday),
						String.valueOf(series.isSunday()));
			} else {
				// Getting event timezone
				final String eventTimezone = eventManagementService
						.getEventTimezoneId(event);
				final Date localizedStart = eventManagementService.convertDate(
						event.getStartDate(), eventTimezone, TimeZone
								.getDefault().getID());
				final Date localizedEnd = eventManagementService.convertDate(
						event.getEndDate(), eventTimezone, TimeZone
								.getDefault().getID());

				appendField(buf, "Start date", oldStart,
						dateFormatter.format(localizedStart));
				appendField(buf, "End date", oldEnd,
						dateFormatter.format(localizedEnd));
			}
			appendDescriptions(buf, oldDescriptions, newDescriptions,
					descriptionKey);
			buf.append("</tbody></table>");
			fillEmailFooterFor(buf, event, user);
			notifyAdminByEmail(
					"Event "
							+ (event.getName() == null ? "" : event.getName()
									+ " ") + updated + " by "
							+ user.getPseudo(), buf.toString());
		} catch (Exception e) {
			LOGGER.error(
					"Exception while generating EVENT email notification: "
							+ e.getMessage(), e);
		}
		return new AsyncResult<Boolean>(true);
	}

	@Async
	@Override
	public Future<Boolean> sendEventDeletedNotification(Event event,
			Place eventPlace, User user) {
		return sendEventSeriesUpdateEmailNotification(event, user, "####",
				null, null, null, null, null, null, null, null, false, false,
				false, false, false, false, false, Collections.EMPTY_LIST,
				null, null, eventPlace);
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
	public Future<Boolean> sendPlaceUpdateEmailNotification(Place place,
			User user, String oldName, String oldAddress, String oldPlaceType,
			String oldCity, String oldLat, String oldLng,
			List<ItemKey> oldTagKeys,
			List<? extends Description> oldDescriptions,
			String[] newDescriptions, String[] descriptionKey) {
		final StringBuilder buf = new StringBuilder();

		// Generic email header
		final String updated = oldName == null ? "added" : "updated";
		fillEmailHeaderFor(buf, place, user, updated);

		// Reporting changed information
		buf.append("<table cellpadding=\"5\" style=\"border: 1px solid #ccc;\"><thead><tr><th>Field</th><th>Old Value</th><th>NEW value</th></thead><tbody>");
		appendField(buf, "Name", oldName, place.getName());
		appendField(buf, "Address", oldAddress, place.getAddress1());
		appendField(buf, "Place type", oldPlaceType, place.getPlaceType());
		appendField(buf, "City", oldCity, place.getCity().getName());
		appendField(buf, "Latitude", oldLat,
				String.valueOf(place.getLatitude()));
		appendField(buf, "Longitude", oldLng,
				String.valueOf(place.getLongitude()));
		appendDescriptions(buf, oldDescriptions, newDescriptions,
				descriptionKey);
		buf.append("</tbody></table>");
		fillEmailFooterFor(buf, place, user);
		notifyAdminByEmail("Place " + place.getName() + " " + updated + " by "
				+ user.getPseudo(), buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	private void appendDescriptions(StringBuilder buf,
			List<? extends Description> oldDescriptions,
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
				appendField(buf, "Description",
						oldDesc != null ? oldDesc.getDescription() : "",
						newDesc);
			}
		}
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
	private void fillEmailHeaderFor(StringBuilder buf, CalmObject object,
			User user, String actionType) {
		final String url = baseUrl
				+ urlService.getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), object);
		final String userUrl = baseUrl
				+ urlService.getOverviewUrl(
						DisplayHelper.getDefaultAjaxContainer(), user);

		String objectType = "Place";
		if (object instanceof User) {
			objectType = "User";
		} else if (object instanceof Event) {
			objectType = "Event";
		}
		buf.append("Hello administrators,<br><br>A "
				+ objectType.toLowerCase()
				+ " has been "
				+ actionType
				+ " on <a href=\"http://www.pelmelguide.com\">PELMEL Guide</a>:<br><br>");
		// buf.append("Hello administrators,<br><br>A place has been updated on PELMEL Guide:<br>");
		final String name = DisplayHelper.getName(object);
		buf.append(objectType + " " + actionType + ": <a href=\"" + url + "\">"
				+ (name == null || name.trim().isEmpty() ? "[No name]" : name)
				+ "</a><br>");
		buf.append(objectType + " " + actionType + " by: <a href=\"" + userUrl
				+ "\">" + user.getPseudo() + "</a><br><br>");
	}

	private void appendField(StringBuilder buf, String fieldName,
			String oldVal, String newVal) {
		boolean isBold = newVal != null && !newVal.equals(oldVal);
		buf.append("<tr><td style=\"text-align:right;\"><i>" + fieldName
				+ "</i></td><td>" + HtmlUtils.htmlEscape(oldVal) + "</td><td>"
				+ (isBold ? "<b>" : "") + HtmlUtils.htmlEscape(newVal)
				+ (isBold ? "</b>" : "") + "</td></tr>");
	}

	private void appendFieldWithUrl(StringBuilder buf, String fieldName,
			String oldVal, String newVal, String oldUrl, String newUrl) {
		boolean isBold = newVal != null && !newVal.equals(oldVal);

		buf.append("<tr><td style=\"text-align:right;\"><i>" + fieldName
				+ "</i></td><td><a href=\"" + baseUrl + oldUrl + "\">"
				+ HtmlUtils.htmlEscape(oldVal) + "</a></td><td><a href=\""
				+ baseUrl + newUrl + "\">" + (isBold ? "<b>" : "")
				+ HtmlUtils.htmlEscape(newVal) + (isBold ? "</b>" : "")
				+ "</a></td></tr>");
	}

	@Override
	@Async
	public Future<Boolean> sendReportEmailNotification(CalmObject obj,
			User user, int reportType) {
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
		}
		buf.append(reportTypeLabel);
		buf.append("</b>");
		fillEmailFooterFor(buf, obj, user);
		notifyAdminByEmail(
				"Place " + DisplayHelper.getName(obj) + " reported as "
						+ reportTypeLabel + " by " + user.getPseudo(),
				buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	private void fillEmailFooterFor(StringBuilder buf, CalmObject object,
			User user) {
		buf.append("<br>");
		buf.append("<span style=\"float:right;padding-top:10px;padding-bottom:10px;\">The PELMEL server ;)</span>");
	}

	@Override
	@Async
	public Future<Boolean> sendMediaAddedEmailNotification(CalmObject obj,
			User user, Media media) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "modified with a new photo");

		buf.append("Photo added: <a href=\"" + baseUrl + media.getUrl()
				+ "\">New photo</a><br>");
		fillEmailFooterFor(buf, obj, user);
		notifyAdminByEmail("Photo added to " + DisplayHelper.getName(obj)
				+ " by " + user.getPseudo(), buf.toString());
		return new AsyncResult<Boolean>(true);
	}

	@Override
	@Async
	public Future<Boolean> sendCommentAddedEmailNotification(CalmObject obj,
			User user, Comment comment) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "commented");
		buf.append("Comment added: <br><p>" + comment.getMessage() + "</p>");
		fillEmailFooterFor(buf, obj, user);
		notifyAdminByEmail("Comment added to " + DisplayHelper.getName(obj)
				+ " by " + user.getPseudo(), buf.toString());
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

		notifyByEmail("[PELMEL Guide] Lost password link", buf.toString(),
				user.getEmail(), "cfondacci@gmail.com");

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
