package com.videopolis.calm.factory;

import java.io.Serializable;

import com.videopolis.calm.factory.base.AbstractRequestTypeFactory;
import com.videopolis.calm.model.RequestType;

/**
 * <p>
 * A factory which creates instances of {@link RequestType}.
 * </p>
 * <p>
 * The {@link RequestTypeFactory} is usually not used by Java clients which will
 * construct {@link RequestType}s directly. However, query parsers (like the AQL
 * parser) will make use of the factory to dynamically create required request
 * types.
 * </p>
 * <p>
 * Implementors should not implement directly this interface, but should rather
 * extend {@link AbstractRequestTypeFactory}, which provides utility methods.
 * </p>
 *
 * @author julien
 *
 */
public interface RequestTypeFactory extends Serializable {

    /**
     * <p>
     * Given an arbitrary set of parameters, returns an appropriate
     * {@link RequestType}.
     * </p>
     * <p>
     * If the parameters does not match the requirements, this method may throw
     * an {@link IllegalArgumentException}
     * </p>
     *
     * @param parameters
     *            Parameters
     * @throws IllegalArgumentException
     *             If the parameters are not appropriate
     * @return New {@link RequestType}
     */
    RequestType createRequestType(Serializable... parameters);

    /**
     * <p>
     * Given a type and an arbitrary set of parameters, returns an appropriate
     * {@link RequestType}.
     * </p>
     * <p>
     * If the parameters does not match the requirements, this method may throw
     * an {@link IllegalArgumentException}
     * </p>
     *
     * @param type
     *            Type of request type
     * @param parameters
     *            Parameters
     * @throws IllegalArgumentException
     *             If the parameters are not appropriate
     * @return New {@link RequestType}
     */
    RequestType createRequestType(String type, Serializable... parameters);
}
