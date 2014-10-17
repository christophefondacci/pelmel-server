package com.videopolis.apis.model;

import java.util.List;

import com.videopolis.apis.exception.ApisException;
import com.videopolis.calm.model.CalmObject;

/**
 * This adapter is a way to change the {@link CalmObject} in the scope of an
 * {@link ApisRequest}. Typically, it is able to adapt any {@link CalmObject}
 * items in the pipeline of the APIS request so that any further APIS call will
 * be performed against the adapted object. Resulting child will also be
 * aggregated in the adapted object.<br>
 * For example :<br>
 * <code>
 * (...).with(Geopoint.class).adapt(GeopointCityAdapter.class).with(Statistic.class)
 * </code><br>
 * will adapt each element of the <code>Geopoint</code> collection using the
 * <code>
 * GeopointCityAdapter</code> (this adapter can provide the "city" geopoint of
 * any geopoint). The object resulting from the adaptation is a Geopoint
 * different from the one in the original collection. The statistic call will be
 * made against the adapted element and returned statistics will be aggregated
 * to the city geopoint.
 * 
 * 
 * @author Christophe Fondacci
 * 
 */
public interface ApisCalmObjectAdapter {

    /**
     * This method adapts the specified {@link CalmObject} into another.
     * 
     * @param element
     *            the {@link CalmObject} to adapt
     * @return the adapted CalmObject
     */
    List<? extends CalmObject> adapt(CalmObject element) throws ApisException;

}
