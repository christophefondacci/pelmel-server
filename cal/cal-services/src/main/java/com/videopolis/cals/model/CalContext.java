package com.videopolis.cals.model;

import java.io.Serializable;
import java.util.Locale;

/**
 * This interface defines the context of a cal service invokation.
 *
 * @author Christophe Fondacci
 *
 */
public interface CalContext extends Serializable {

    /**
     * @return the current locale to use
     */
    Locale getLocale();

    /**
     * Returns the TLD information of the host which is at the origin of the
     * request.
     *
     * @return TLD, or {@code null} if this information is not available
     */
    String getOriginTld();

    /**
     * Returns the server name of the host which is at the origin of the
     * request.
     *
     * @return Server name, or {@code null} if this information is not available
     */
    String getOriginServerName();
}
