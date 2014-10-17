package com.videopolis.calm.model;

/**
 * Defines a localized element. Any element of the Content Abstraction Layer 
 * model which is localized should implement this interface for proper integration
 * with the search engine.
 * 
 * @author Christophe Fondacci
 */
public interface Localized {

    /**
     * Retrieves the longitude of this localized point.
     * 
     * @return the longitude of this element
     */
    double getLongitude();
    /**
     * Retrieves the latitude of this localized point
     * 
     * @return the latitude of this element
     */
    double getLatitude();
}
