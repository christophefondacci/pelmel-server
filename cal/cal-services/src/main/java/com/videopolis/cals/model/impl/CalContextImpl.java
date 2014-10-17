package com.videopolis.cals.model.impl;

import java.util.Locale;

import com.videopolis.cals.model.CalContext;

/**
 * Default implementation of {@link CalContext}
 *
 * @author julien
 *
 */
public class CalContextImpl implements CalContext {

    private static final long serialVersionUID = 706091437032592702L;

    /** Current locale */
    private final Locale locale;

    private final String originTld;

    private final String originServerName;

    /**
     * <p>
     * Default constructor, kept for compatibility.
     * </p>
     * <p>
     * {@code originTld} and {@code originServerName} will be set to
     * {@code null}
     * </p>
     *
     * @param locale
     *            Locale of the context
     */
    public CalContextImpl(final Locale locale) {
	this(locale, null, null);
    }

    /**
     * Default constructor
     *
     * @param locale
     *            Locale of the context
     * @param originTld
     *            TLD of the origin host
     * @param originServerName
     *            Server name of the origin host
     */
    public CalContextImpl(final Locale locale, final String originTld,
	    final String originServerName) {
	this.locale = locale;
	this.originTld = originTld;
	this.originServerName = originServerName;
    }

    @Override
    public Locale getLocale() {
	return locale;
    }

    @Override
    public String getOriginTld() {
	return originTld;
    }

    @Override
    public String getOriginServerName() {
	return originServerName;
    }

    @Override
    public String toString() {
	return "CalContext[" + locale.toString() + ", " + originTld + ", "
		+ originServerName + "]";
    }
}
