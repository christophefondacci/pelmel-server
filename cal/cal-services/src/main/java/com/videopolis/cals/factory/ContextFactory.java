package com.videopolis.cals.factory;

import java.util.Locale;

import com.videopolis.cals.model.CalContext;
import com.videopolis.cals.model.impl.CalContextImpl;

/**
 * A factory used to create {@link CalContext} instances.
 *
 * @author julien
 *
 */
public final class ContextFactory {

    private ContextFactory() {
    }

    /**
     * Creates a context implementation from the provided information.
     *
     * @deprecated please use the full {@link Locale} constructor, this method
     *             is here for compatibility only
     * @param locale
     *            locale to use when invoking CAL services
     * @return a {@link CalContext} implementation
     */
    @Deprecated
    public static CalContext createContext(final String locale) {
	return new CalContextImpl(new Locale(locale));
    }

    /**
     * Creates a new {@link CalContext} using the Locale information.
     *
     * @param locale
     *            locale to use with CAL service calls
     * @return a {@link CalContext} implementation
     */
    public static CalContext createContext(final Locale locale) {
	return new CalContextImpl(locale);
    }

    /**
     * Creates a new {@link CalContext}
     *
     * @param locale Locale
     * @param originTld Origin TLD
     * @param originServerName
     * @return
     */
    public static CalContext createContext(final Locale locale,
	    final String originTld, final String originServerName) {
	return new CalContextImpl(locale, originTld, originServerName);
    }
}
