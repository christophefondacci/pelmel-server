package com.videopolis.calm.model.impl;

import java.io.Serializable;
import java.util.Arrays;

import com.videopolis.calm.model.RequestType;

/**
 * A default {@link RequestType} implementation which just stores parameters.
 *
 * @author julien
 *
 */
public class DefaultRequestType implements RequestType {

    private static final long serialVersionUID = 915709494092743591L;

    /** Stored parameters */
    private final Serializable[] parameters;

    /** Stored type */
    private final String type;

    /**
     * Creates a new instance
     *
     * @param parameters
     *            Parameters to store
     */
    public DefaultRequestType(final Serializable[] parameters) {
	this(null, parameters);
    }

    /**
     * Creates a new instance
     *
     * @param type
     *            Type to store
     * @param parameters
     *            Parameters to store
     */
    public DefaultRequestType(final String type, final Serializable[] parameters) {
	this.parameters = Arrays.copyOf(parameters, parameters.length);
	this.type = type;
    }

    /**
     * Returns the stored parameters
     *
     * @return Parameters
     */
    public Serializable[] getParameters() {
	return parameters;
    }

    /**
     * Returns the stored type
     *
     * @return Type
     */
    public String getType() {
	return type;
    }
}
