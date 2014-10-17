package com.nextep.proto.blocks;

import java.util.Locale;

import com.nextep.proto.services.UrlService;

/**
 * This supports provides methods useful for consistent login and routing after
 * login
 * 
 * @author cfondacci
 * 
 */
public interface LoginSupport {

	/**
	 * Initializes this support
	 * 
	 * @param locale
	 *            the current supported {@link Locale}
	 * @param urlService
	 *            current {@link UrlService}
	 * @param headerSupport
	 *            the current {@link HeaderSupport}
	 * @param redirectUrl
	 *            the optional redirectUrl that the user should be redirected
	 *            to, if different from the current page, after being logged in
	 */
	void initialize(Locale locale, UrlService urlService,
			HeaderSupport headerSupport, String redirectUrl);

	/**
	 * Toggles the forced redirection mode. It means that the server will decide
	 * the most appropriate landing page after login, different form current
	 * page. This flag should be set on the index page where we should not land
	 * the user on the index page, which is a logging page
	 * 
	 * @param force
	 *            <code>true</code> to force redirect, default is
	 *            <code>false</code>
	 */
	void setForceRedirect(boolean force);

	/**
	 * Provides the facebook connect URL that will be added on facebook connect
	 * button
	 * 
	 * @return
	 */
	String getFacebookConnectUrl();

	/**
	 * The URL to which the user should land after login
	 * 
	 * @return the landing URL
	 */
	String getLoginLandingPageUrl();

	/**
	 * The callback that facebook will call after the facebook login is done.
	 * This URL will itself contain an internal redirection since the page is
	 * the togayther login page, which will route to the landing page
	 * 
	 * @param landingPageUrl
	 *            the landing page URL (MUST ALWAYS BE provided)
	 * @return the facebook callback url
	 */
	String getFacebookCallbackUrl(String landingPageUrl);
}
