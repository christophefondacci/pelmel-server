package com.nextep.proto.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.HtmlUtils;

import com.nextep.comments.model.Comment;
import com.nextep.descriptions.model.Description;
import com.nextep.events.model.Event;
import com.nextep.geo.model.Place;
import com.nextep.media.model.Media;
import com.nextep.proto.helpers.DisplayHelper;
import com.nextep.proto.model.Constants;
import com.nextep.proto.services.NotificationService;
import com.nextep.proto.services.UrlService;
import com.nextep.users.model.User;
import com.relayrides.pushy.apns.ApnsEnvironment;
import com.relayrides.pushy.apns.FailedConnectionListener;
import com.relayrides.pushy.apns.PushManager;
import com.relayrides.pushy.apns.PushManagerConfiguration;
import com.relayrides.pushy.apns.util.ApnsPayloadBuilder;
import com.relayrides.pushy.apns.util.MalformedTokenStringException;
import com.relayrides.pushy.apns.util.SSLContextUtil;
import com.relayrides.pushy.apns.util.SimpleApnsPushNotification;
import com.relayrides.pushy.apns.util.TokenUtil;
import com.videopolis.calm.model.CalmObject;
import com.videopolis.calm.model.ItemKey;

/**
 * Default implementation of notification service using push messages
 * 
 * @author cfondacci
 * 
 */
public class NotificationServiceImpl implements NotificationService {

	private static Log LOGGER = LogFactory.getLog("notifications");

	// Injected services
	private UrlService urlService;
	private String baseUrl;

	private PushManager<SimpleApnsPushNotification> pushManager;
	// Push information
	private String pushKeyPath;
	private String pushKeyPassword;
	private boolean production;
	private boolean pushEnabled = true;
	// Email notification
	private String adminEmailAlias;

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
		}
	}

	public void shutdown() throws Exception {
		if (pushManager != null) {
			pushManager.shutdown();
		}
	}

	@Override
	public void sendNotification(User user, String message, int badgeCount,
			String sound) {
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
	}

	@Override
	public void notifyAdminByEmail(String title, String html) {
		LOGGER.info("<br>=================<br>" + title + "<br>" + html);
		//
		// if (adminEmailAlias == null || adminEmailAlias.trim().isEmpty()) {
		// return;
		// }
		// // Get a Properties object
		// Properties props = System.getProperties();
		// props.setProperty("mail.smtp.host", "smtp.pelmelguide.com");
		// props.put("mail.smtp.auth", "true");
		//
		// Session session = Session.getDefaultInstance(props);
		//
		// // -- Create a new message --
		// final MimeMessage msg = new MimeMessage(session);
		//
		// // -- Set the FROM and TO fields --
		// try {
		// msg.setFrom(new InternetAddress("no-reply@pelmelguide.com"));
		// msg.setRecipients(Message.RecipientType.TO,
		// InternetAddress.parse("christophe@pelmelguide.com", false));
		// msg.setRecipients(Message.RecipientType.BCC,
		// InternetAddress.parse(adminEmailAlias, false));
		//
		// msg.setSubject("[PELMEL Guide] " + title);
		// msg.setContent(html, "text/html");
		// msg.setSentDate(new Date());
		//
		// Transport tr = session.getTransport("smtp");
		// tr.connect("smtp.pelmelguide.com", "christophe@pelmelguide.com",
		// "tdk;1558");
		// msg.saveChanges();
		// tr.sendMessage(msg, msg.getAllRecipients());
		// tr.close();
		// // Transport.send(msg);
		// LOGGER.info("SUCCESS: Sent email notification");
		// } catch (MessagingException e) {
		// LOGGER.error(
		// "Unable to send email notification: " + e.getMessage(), e);
		// }
	}

	@Override
	public void sendPlaceUpdateEmailNotification(Place place, User user,
			String oldName, String oldAddress, String oldPlaceType,
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
		buf.append("</tbody></table>");
		fillEmailFooterFor(buf, place, user);
		notifyAdminByEmail("Place " + place.getName() + " " + updated + " by "
				+ user.getPseudo(), buf.toString());
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

		buf.append("Hello administrators,<br><br>A place has been "
				+ actionType
				+ " on <a href=\"http://www.pelmelguide.com\">PELMEL Guide</a>:<br><br>");
		// buf.append("Hello administrators,<br><br>A place has been updated on PELMEL Guide:<br>");
		String objectType = "place";
		if (object instanceof User) {
			objectType = "User";
		} else if (object instanceof Event) {
			objectType = "Event";
		}
		buf.append(objectType + " " + actionType + ": <a href=\"" + url + "\">"
				+ DisplayHelper.getName(object) + "</a><br>");
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

	@Override
	public void sendReportEmailNotification(CalmObject obj, User user,
			int reportType) {
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
	}

	private void fillEmailFooterFor(StringBuilder buf, CalmObject object,
			User user) {
		buf.append("<br>");
		buf.append("<span style=\"float:right;padding-top:10px;padding-bottom:10px;\">The PELMEL server ;)</span>");
	}

	@Override
	public void sendMediaAddedEmailNotification(CalmObject obj, User user,
			Media media) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "modified with a new photo");

		buf.append("Photo added: <a href=\"" + baseUrl + media.getUrl()
				+ "\">New photo</a><br>");
		fillEmailFooterFor(buf, obj, user);
		notifyAdminByEmail("Photo added to " + DisplayHelper.getName(obj)
				+ " by " + user.getPseudo(), buf.toString());
	}

	@Override
	public void sendCommentAddedEmailNotification(CalmObject obj, User user,
			Comment comment) {
		StringBuilder buf = new StringBuilder();
		fillEmailHeaderFor(buf, obj, user, "commented");
		buf.append("Comment added: <br><p>" + comment.getMessage() + "</p>");
		fillEmailFooterFor(buf, obj, user);
		notifyAdminByEmail("Comment added to " + DisplayHelper.getName(obj)
				+ " by " + user.getPseudo(), buf.toString());
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
