package com.nextep.proto.blocks.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.proto.blocks.HeaderSupport;
import com.nextep.proto.blocks.LoginSupport;
import com.nextep.proto.helpers.LocalizationHelper;
import com.nextep.proto.services.UrlService;

public class CommonLoginSupportImpl implements LoginSupport {

	private static final Log LOGGER = LogFactory
			.getLog(CommonLoginSupportImpl.class);

	private String domainName;

	private Locale locale;
	private HeaderSupport headerSupport;
	private String redirecturl;
	private boolean forceRedirect = false;

	@Override
	public void initialize(Locale locale, UrlService urlService,
			HeaderSupport headerSupport, String redirectUrl) {
		this.locale = locale;
		this.headerSupport = headerSupport;
		this.redirecturl = redirectUrl;
		this.forceRedirect = false;
	}

	@Override
	public void setForceRedirect(boolean forceRedirect) {
		this.forceRedirect = forceRedirect;
	}

	@Override
	public String getFacebookConnectUrl() {
		final UUID uuid = UUID.randomUUID();
		final StringBuilder buf = new StringBuilder();
		buf.append("https://www.facebook.com/dialog/oauth?client_id=302478486488872&redirect_uri=");

		// Retrieving our current URL
		String currentPageUrl = getLoginLandingPageUrl();
		final String callbackUrl = getFacebookCallbackUrl(currentPageUrl);
		try {
			buf.append(URLEncoder.encode(callbackUrl, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Unsupported encoding while encoding URL '"
					+ callbackUrl + "': " + e.getMessage());
		}
		buf.append("&scope=user_birthday,user_location,email");
		buf.append("&state=" + uuid.toString());
		return buf.toString();
	}

	@Override
	public String getLoginLandingPageUrl() {
		if (redirecturl == null || "".equals(redirecturl)) {
			String currentPageUrl = null;
			// Only using current page if not in force redirect mode
			if (!forceRedirect) {
				if (headerSupport != null) {
					final String lang = headerSupport.getLanguage();
					currentPageUrl = headerSupport.getAlternate(lang);
				}
			}
			return currentPageUrl;
		} else {
			return redirecturl;
		}
	}

	@Override
	public String getFacebookCallbackUrl(String landingPageUrl) {
		String callbackUrl = LocalizationHelper.buildUrl(locale, domainName,
				"/facebookLogin.action");
		if (landingPageUrl != null) {
			try {
				callbackUrl = callbackUrl + "?redirectUrl="
						+ URLEncoder.encode(landingPageUrl, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error("Unsupported encoding while encoding URL '"
						+ landingPageUrl + "': " + e.getMessage());
			}
		} else if (forceRedirect) {
			callbackUrl = callbackUrl + "?forceRedirect=true";
		}
		return callbackUrl;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
