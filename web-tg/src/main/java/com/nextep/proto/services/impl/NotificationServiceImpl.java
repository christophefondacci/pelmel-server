package com.nextep.proto.services.impl;

import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
	private String pushKeyPath;
	private String pushKeyPassword;
	private boolean production;

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

	@Override
	public void sendNotification(User user, String message, int badgeCount,
			String sound) {
		if (user.getPushDeviceId() != null) {

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

	public void setPushKeyPath(String pushKeyPath) {
		this.pushKeyPath = pushKeyPath;
	}

	public void setPushKeyPassword(String pushKeyPassword) {
		this.pushKeyPassword = pushKeyPassword;
	}

	public void setProduction(boolean production) {
		this.production = production;
	}
}
