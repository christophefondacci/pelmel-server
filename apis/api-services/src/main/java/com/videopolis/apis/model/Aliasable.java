package com.videopolis.apis.model;

import com.videopolis.apis.exception.ApisException;

/**
 * An aliasable entity is an element for which an alias string could be
 * associated with it.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface Aliasable<T> {

    /**
     * Retrieves the alias of this element.
     * 
     * @return the alias string
     */
    String getAlias();

    /**
     * Defines the alias to assign to this element.
     * 
     * @param alias
     *            the alias string to assign to this element.
     */
    T aliasedBy(String alias) throws ApisException;
}
