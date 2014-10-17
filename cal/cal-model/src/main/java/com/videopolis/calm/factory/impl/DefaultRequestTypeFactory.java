package com.videopolis.calm.factory.impl;

import java.io.Serializable;

import com.videopolis.calm.factory.RequestTypeFactory;
import com.videopolis.calm.model.RequestType;
import com.videopolis.calm.model.impl.DefaultRequestType;

/**
 * <p>
 * The default implementation of {@link RequestTypeFactory}.
 * </p>
 * <p>
 * This factory just create instances of {@link DefaultRequestType} which barely
 * stores all the provided parameters without any type & count checking.
 * </p>
 *
 * @author julien
 *
 */
public class DefaultRequestTypeFactory implements RequestTypeFactory {

    private static final long serialVersionUID = -1803355052264920750L;

    @Override
    public RequestType createRequestType(final Serializable... parameters) {
	return new DefaultRequestType(parameters);
    }

    @Override
    public RequestType createRequestType(final String type,
	    final Serializable... parameters) {
	return new DefaultRequestType(type, parameters);
    }
}
