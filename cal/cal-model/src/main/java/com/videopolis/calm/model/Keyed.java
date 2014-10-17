package com.videopolis.calm.model;

/**
 * This interface describes any element which can be referenced through a 
 * {@link ItemKey}.
 * 
 * @author Christophe Fondacci
 *
 */
public interface Keyed {

    /**
     * @return the unique key of this item
     */
    ItemKey getKey();
}
