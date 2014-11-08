package com.nextep.proto.services.impl;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.proto.blocks.ActivitySupport;
import com.nextep.proto.services.NotificationService;
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

/**
 * Default implementation of notification service using push messages
 * 
 * @author cfondacci
 * 
 */
public class NotificationServiceImpl implements NotificationService {

	private static Log LOGGER = LogFactory
			.getLog(NotificationServiceImpl.class);
	private PushManager<SimpleApnsPushNotification> pushManager;
	// Push information
	private String pushKeyPath;
	private String pushKeyPassword;
	private boolean production;
	private boolean pushEnabled = true;
	// Email notification
	private String adminEmailAlias;
	private ActivitySupport activitySupport;

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
		// Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		// final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		if (adminEmailAlias == null || adminEmailAlias.trim().isEmpty()) {
			return;
		}
		// Get a Properties object
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.host", "smtp.pelmelguide.com");

		Session session = Session.getDefaultInstance(props);

		// -- Create a new message --
		final MimeMessage msg = new MimeMessage(session);

		// -- Set the FROM and TO fields --
		try {
			msg.setFrom(new InternetAddress("christophe@pelmelguide.com"));
			msg.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(adminEmailAlias, false));

			msg.setSubject("[PELMEL Guide] " + title);
			msg.setContent(html, "text/html");
			msg.setSentDate(new Date());

			Transport tr = session.getTransport("smtp");
			tr.connect("smtp.pelmelguide.com", "christophe@pelmelguide.com",
					"tdk;1558");
			msg.saveChanges();
			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();
			// Transport.send(msg);
			LOGGER.info("SUCCESS: Sent email notification");
		} catch (MessagingException e) {
			LOGGER.error(
					"Unable to send email notification: " + e.getMessage(), e);
		}
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

	public void setActivitySupport(ActivitySupport activitySupport) {
		this.activitySupport = activitySupport;
	}
}
