package com.nextep.proto.helpers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.nextep.proto.model.Constants;
import com.opensymphony.xwork2.ActionContext;

public final class LocalizationHelper {

	private static final List<String> supportedLanguages = Arrays.asList("en",
			"fr"); // , "es");
	private static Locale FR = new Locale("fr", "FR");
	private static Locale US = new Locale("en", "US");
	private static Locale ES = new Locale("es", "ES");

	private LocalizationHelper() {
	}

	public static String buildUrl(Locale locale, String domainName, String url) {
		return buildUrl(locale.getLanguage(), domainName, url);
	}

	public static String buildUrl(String language, String domainName, String url) {
		return buildUrl(language, domainName, url, false);
	}

	public static String buildUrl(String language, String domainName,
			String url, boolean secured) {
		final String domainExt = getDomainExt(language);
		String subdomain = ActionContext.getContext() == null ? null
				: (String) ActionContext.getContext().get(
						Constants.ACTION_CONTEXT_SUBDOMAIN);
		if (subdomain == null) {
			subdomain = "www";
		}
		return (secured ? "https" : "http") + "://" + subdomain + "."
				+ domainName + "." + domainExt + url;
	}

	private static String getDomainExt(String language) {
		String domainExt = null;
		if ("en".equals(language)) {
			domainExt = "com";
		} else if (supportedLanguages.contains(language)) {
			domainExt = language;
		} else {
			domainExt = "com";
		}
		return domainExt;
	}

	public static String buildFullDomain(String domain, Locale locale) {
		final String domainExt = getDomainExt(locale.getLanguage());
		return domain + "." + domainExt;
	}

	public static boolean isLanguageSupported(Locale l) {
		final String language = l.getLanguage();
		return supportedLanguages.contains(language);
	}

	public static List<String> getSupportedLanguages() {
		return Collections.unmodifiableList(supportedLanguages);
	}

	public static Locale getLocaleForDomain(String domain) {
		if ("fr".equals(domain)) {
			return FR;
		} else if ("es".equals(domain)) {
			return ES;
		} else {
			return US;
		}
	}

	public static List<String> getSupportedDomains() {
		return Arrays.asList("com", "fr");
	}
}
