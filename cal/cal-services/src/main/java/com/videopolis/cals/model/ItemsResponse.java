package com.videopolis.cals.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.videopolis.calm.model.CalmObject;
import com.videopolis.cals.service.CalService;

/**
 * Default interface of the response of a {@link CalService} method call.
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ItemsResponse extends Serializable {

    /**
     * Returns all items retrieved by the {@link CalService} method call
     * 
     * @return a list of {@link CalmObject} of the returned items
     */
    List<? extends CalmObject> getItems();

    /**
     * Provides the last modification timestamp (as a java.util.Date) of the
     * items contained in this response.
     * 
     * @return the most recent modification date of the response, which should
     *         generally be the modification date of the most recent element of
     *         the items from this response, or <code>null</code> when this
     *         information cannot be computed
     */
    Date getLastUpdateTimestamp();

    /**
     * Defines the last modification timestamp of the items contained in this
     * response.
     * 
     * @param lastUpdateTimestamp
     *            the most recent modification date of the response, which
     *            should generally be the modification date of the most recent
     *            element of the items from this response
     */
    void setLastUpdateTimestamp(Date lastUpdateTimestamp);
}
