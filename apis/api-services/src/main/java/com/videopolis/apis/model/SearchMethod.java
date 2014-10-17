package com.videopolis.apis.model;

/**
 * This enum type defines the various available search methods.
 * 
 * @author Christophe Fondacci
 * 
 */
public enum SearchMethod {
    /**
     * Indicates that elements will be searched from their relationship with a
     * parent element.
     */
    CONTAINED,
    /**
     * Indicates that elements will be searched from their distance to a parent
     * element.
     */
    NEARBY, ALL
}
