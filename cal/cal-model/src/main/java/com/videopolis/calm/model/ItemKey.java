package com.videopolis.calm.model;

import java.io.Serializable;

/**
 * Describes a unique key of an abstract item.
 * 
 * @author Christophe Fondacci
 */
public interface ItemKey extends Serializable {

    /**
     * @return the type of the item defined by this key
     */
    String getType();

    /**
     * Defines the type of the item defined by this key
     * 
     * @param type
     *            item type
     */
    void setType(String type);

    /**
     * @return the identified of the item defined by this key
     */
    String getId();

    /**
     * Defines the identifier of the item defined by this key
     * 
     * @param id
     *            item identifier
     */
    void setId(String id);
    /**
     * A helper method which allows to access to a casted numeric identifier.
     * <br><b>Warning :</b> This method may raise ClassCastException when the 
     * underlying id of this key is not a numeric id.
     * 
     * @return the numeric id
     */
    long getNumericId();
}
